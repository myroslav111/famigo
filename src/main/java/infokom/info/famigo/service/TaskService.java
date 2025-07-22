package infokom.info.famigo.service;

import infokom.info.famigo.entity.Task;
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

    public Task save(Task task) {
        return taskRepository.save(task);
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
        return taskRepository.findAll().stream().
                filter(task -> task.getAssignedTo() != null && task.getAssignedTo().getId().equals(userId))
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(LocalDate::compareTo)))
                .toList();
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
