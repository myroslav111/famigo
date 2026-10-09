package infokom.info.famigo.service;

import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.exception.DomainException;
import infokom.info.famigo.exception.InvalidTaskStateException;
import infokom.info.famigo.exception.NotFoundException;
import infokom.info.famigo.exception.TaskNotFoundException;
import infokom.info.famigo.repository.RewardRepository;
import infokom.info.famigo.repository.TaskRepository;
import infokom.info.famigo.repository.TaskTemplateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Use-Cases rund um Aufgaben. Jede Einreichung einer Aufgabe wird von genau einem {@link Reward}
 * begleitet, über den die Eltern-Freigabe läuft (bis Phase 4 der Spezifikation).
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final RewardRepository rewardRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository,
                       RewardRepository rewardRepository,
                       TaskTemplateRepository taskTemplateRepository,
                       UserService userService) {
        this.taskRepository = taskRepository;
        this.rewardRepository = rewardRepository;
        this.taskTemplateRepository = taskTemplateRepository;
        this.userService = userService;
    }

    // =========================================================
    // Use-Cases (schreibend)
    // =========================================================

    /** Elternteil weist einem eigenen Kind eine Aufgabe zu (frei oder aus einer Vorlage). */
    @Transactional
    public Task assign(Long childId, String title, String description, int starsReward,
                       LocalDate dueDate, Long templateId) {
        User parent = userService.requireParentOf(childId);
        User child = userService.getById(childId);
        if (starsReward < 1) {
            throw new DomainException("Eine Aufgabe muss mindestens 1 Stern wert sein.");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStarsReward(starsReward);
        task.setDueDate(dueDate);
        task.setStatus(TaskStatus.PENDING);
        task.setAssignedTo(child);
        task.setCreatedBy(parent);
        if (templateId != null) {
            task.setTemplate(findTemplate(templateId));
        }
        taskRepository.save(task);

        createReward(task, parent);
        return task;
    }

    /** Kind meldet eine ihm zugewiesene, offene Aufgabe als erledigt. */
    @Transactional
    public void submit(Long taskId) {
        User child = userService.requireChild();
        Task task = getTask(taskId);

        if (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(child.getId())) {
            throw new DomainException("Diese Aufgabe ist dir nicht zugewiesen.");
        }
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new InvalidTaskStateException("Diese Aufgabe wurde bereits erledigt gemeldet.");
        }
        task.setStatus(TaskStatus.DONE);

        // Altdaten: Aufgaben aus Vorlagen wurden früher ohne Reward angelegt
        if (rewardRepository.findFirstByTaskId(task.getId()).isEmpty()) {
            createReward(task, task.getCreatedBy());
        }
    }

    /** Kind wählt selbst eine Vorlage und meldet sie direkt als erledigt. */
    @Transactional
    public Task submitFromTemplate(Long templateId) {
        User child = userService.requireChild();
        TaskTemplate template = findTemplate(templateId);

        Task task = new Task();
        task.setTitle(template.getTitle());
        task.setDescription(template.getDescription());
        task.setStarsReward(template.getStarsReward());
        task.setDueDate(LocalDate.now());
        task.setStatus(TaskStatus.DONE);
        task.setAssignedTo(child);
        task.setCreatedBy(child);
        task.setTemplate(template);
        taskRepository.save(task);

        createReward(task, child);
        return task;
    }

    /** Elternteil bestätigt eine erledigte Aufgabe; das Kind erhält die Sterne. Atomar. */
    @Transactional
    public void approve(Long rewardId) {
        Reward reward = getReward(rewardId);
        User child = reward.getChild();
        userService.requireParentOf(child.getId());

        Task task = requireDoneTask(reward);
        task.setStatus(TaskStatus.APPROVED);
        reward.setRedeemed(true);
        child.setStars(child.getStars() + reward.getStarCost());
    }

    /**
     * Elternteil lehnt eine erledigte Aufgabe ab. Hat das Kind die Aufgabe selbst eingereicht,
     * wird sie gelöscht, sonst geht sie zurück auf {@link TaskStatus#PENDING}.
     */
    @Transactional
    public void reject(Long rewardId) {
        Reward reward = getReward(rewardId);
        User child = reward.getChild();
        userService.requireParentOf(child.getId());

        Task task = requireDoneTask(reward);
        User creator = task.getCreatedBy();
        boolean submittedByChild = creator == null || creator.getId().equals(child.getId());

        if (submittedByChild) {
            rewardRepository.delete(reward);
            taskRepository.delete(task);
        } else {
            reward.setRedeemed(false);
            task.setStatus(TaskStatus.PENDING);
        }
    }

    // =========================================================
    // Abfragen
    // =========================================================

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByAssignedTo(Long userId) {
        return taskRepository.findByAssignedToId(userId, Sort.unsorted());
    }

    public List<Task> findByAssignedToSorted(Long userId) {
        return taskRepository.findByAssignedToId(userId, Sort.by(Sort.Order.asc("dueDate").nullsLast()));
    }

    /** Noch gültige (ab heute fällige), nicht bestätigte Aufgaben. */
    public List<Task> findByAssignedToSortedByDueDate(Long userId) {
        return taskRepository.findUpcomingExcludingStatus(userId, LocalDate.now(), TaskStatus.APPROVED);
    }

    public List<Task> findTasksDueTodayForChild(Long userId) {
        return taskRepository.findByAssignedToIdAndDueDateAndStatus(userId, LocalDate.now(), TaskStatus.PENDING);
    }

    public List<Task> findStillValidTasksAndStatusPending(Long userId) {
        return findUpcoming(userId, TaskStatus.PENDING);
    }

    public List<Task> findTasksByAssignedToAndDueDateAndStatusDone(Long userId) {
        return findUpcoming(userId, TaskStatus.DONE);
    }

    public List<Task> findTasksByAssignedToAndDueDateAndStatusApproved(Long userId) {
        return findUpcoming(userId, TaskStatus.APPROVED);
    }

    // =========================================================
    // Hilfsmethoden
    // =========================================================

    private List<Task> findUpcoming(Long userId, TaskStatus status) {
        return taskRepository.findByAssignedToIdAndDueDateGreaterThanEqualAndStatusOrderByDueDateAsc(
                userId, LocalDate.now(), status);
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private Reward getReward(Long rewardId) {
        return rewardRepository.findById(rewardId)
                .orElseThrow(() -> new NotFoundException("Einreichung nicht gefunden."));
    }

    private TaskTemplate findTemplate(Long templateId) {
        return taskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Vorlage nicht gefunden."));
    }

    private Task requireDoneTask(Reward reward) {
        Task task = reward.getTask();
        if (task == null) {
            throw new NotFoundException("Zu dieser Einreichung gehört keine Aufgabe.");
        }
        if (task.getStatus() != TaskStatus.DONE) {
            throw new InvalidTaskStateException("Diese Aufgabe wurde bereits entschieden.");
        }
        return task;
    }

    private void createReward(Task task, User createdBy) {
        Reward reward = new Reward();
        reward.setTitle(task.getTitle());
        reward.setDescription(task.getDescription());
        reward.setStarCost(task.getStarsReward());
        reward.setChild(task.getAssignedTo());
        reward.setCreatedBy(createdBy);
        reward.setTask(task);
        rewardRepository.save(reward);
    }
}
