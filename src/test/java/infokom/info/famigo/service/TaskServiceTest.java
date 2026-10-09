package infokom.info.famigo.service;

import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.exception.DomainException;
import infokom.info.famigo.exception.InvalidTaskStateException;
import infokom.info.famigo.repository.RewardRepository;
import infokom.info.famigo.repository.TaskRepository;
import infokom.info.famigo.repository.TaskTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest extends ServiceTestSupport {

    @Autowired TaskService taskService;
    @Autowired TaskRepository taskRepository;
    @Autowired RewardRepository rewardRepository;
    @Autowired TaskTemplateRepository templateRepository;

    @Test
    void assignLegtAufgabeUndEinreichungAn() {
        loginAs(parent);

        Task task = taskService.assign(child.getId(), "Zimmer", "aufräumen", 3, LocalDate.now(), null);

        assertThat(taskRepository.findById(task.getId())).get()
                .extracting(Task::getStatus).isEqualTo(TaskStatus.PENDING);
        assertThat(rewardRepository.findFirstByTaskId(task.getId())).get()
                .extracting(Reward::getStarCost).isEqualTo(3);
    }

    @Test
    void assignNurFuerEigeneKinder() {
        loginAs(child);

        assertThatThrownBy(() -> taskService.assign(child.getId(), "x", null, 1, LocalDate.now(), null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void submitNurEigeneOffeneAufgabe() {
        Task task = assignAsParent(2);

        loginAs(parent);
        assertThatThrownBy(() -> taskService.submit(task.getId())).isInstanceOf(DomainException.class);

        loginAs(child);
        taskService.submit(task.getId());
        assertThat(statusOf(task)).isEqualTo(TaskStatus.DONE);

        assertThatThrownBy(() -> taskService.submit(task.getId()))
                .isInstanceOf(InvalidTaskStateException.class);
    }

    @Test
    void approveSchreibtSterneGenauEinmalGut() {
        Task task = assignAsParent(5);
        loginAs(child);
        taskService.submit(task.getId());
        Long rewardId = rewardOf(task).getId();

        loginAs(parent);
        taskService.approve(rewardId);

        assertThat(statusOf(task)).isEqualTo(TaskStatus.APPROVED);
        assertThat(starsOf(child)).isEqualTo(5);
        assertThat(rewardRepository.findById(rewardId).orElseThrow().isRedeemed()).isTrue();

        assertThatThrownBy(() -> taskService.approve(rewardId))
                .isInstanceOf(InvalidTaskStateException.class);
        assertThat(starsOf(child)).isEqualTo(5);
    }

    @Test
    void rejectSetztElternAufgabeZurueck() {
        Task task = assignAsParent(5);
        loginAs(child);
        taskService.submit(task.getId());

        loginAs(parent);
        taskService.reject(rewardOf(task).getId());

        assertThat(statusOf(task)).isEqualTo(TaskStatus.PENDING);
        assertThat(starsOf(child)).isZero();
    }

    @Test
    void rejectLoeschtSelbstEingereichteVorlagenAufgabe() {
        TaskTemplate template = new TaskTemplate();
        template.setTitle("Müll");
        template.setStarsReward(2);
        template = templateRepository.save(template);

        loginAs(child);
        Task task = taskService.submitFromTemplate(template.getId());
        assertThat(statusOf(task)).isEqualTo(TaskStatus.DONE);
        Long rewardId = rewardOf(task).getId();

        loginAs(parent);
        taskService.reject(rewardId);

        assertThat(taskRepository.findById(task.getId())).isEmpty();
        assertThat(rewardRepository.findById(rewardId)).isEmpty();
    }

    private Task assignAsParent(int stars) {
        loginAs(parent);
        return taskService.assign(child.getId(), "Aufgabe", null, stars, LocalDate.now(), null);
    }

    private TaskStatus statusOf(Task task) {
        return taskRepository.findById(task.getId()).orElseThrow().getStatus();
    }

    private Reward rewardOf(Task task) {
        return rewardRepository.findFirstByTaskId(task.getId()).orElseThrow();
    }
}
