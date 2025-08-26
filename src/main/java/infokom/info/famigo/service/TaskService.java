package infokom.info.famigo.service;

import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository  taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        taskRepository.save(task);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByAssignedTo(Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null && task.getAssignedTo().getId().equals(userId))
                .toList();
    }

    public List<Task> findByAssignedToSorted(Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null && task.getAssignedTo().getId().equals(userId))
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(LocalDate::compareTo)))
                .toList();
    }

    public List<Task> findByAssignedToSortedByDueDate(Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null
                              && task.getAssignedTo().getId().equals(userId)
                              && task.getDueDate() != null
                              && task.getDueDate().isAfter(LocalDate.now().minusDays(1))
                              && task.getStatus() != TaskStatus.APPROVED
                              )
                .sorted(Comparator.comparing(Task::getDueDate))
                .toList();
    }

    public List<Task> findTasksDueTodayForChild(Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null
                && task.getAssignedTo().getId().equals(userId)
                && task.getDueDate() != null
                && task.getDueDate().isEqual(LocalDate.now())
                && task.getStatus().equals(TaskStatus.PENDING)
                )
                .toList();
    }

    public List<Task> findStillValidTasks(Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null
                && task.getAssignedTo().getId().equals(userId)
                && task.getDueDate() != null
                && task.getDueDate().isAfter(LocalDate.now().minusDays(1))
                && task.getStatus().equals(TaskStatus.PENDING))
                .sorted(Comparator.comparing(Task::getDueDate))
                .toList();
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
