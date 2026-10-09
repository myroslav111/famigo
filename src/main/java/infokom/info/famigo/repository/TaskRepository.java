package infokom.info.famigo.repository;

import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.enums.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToId(Long userId, Sort sort);

    List<Task> findByAssignedToIdAndDueDate(Long userId, LocalDate dueDate);

    List<Task> findByAssignedToIdAndDueDateAndStatus(Long userId, LocalDate dueDate, TaskStatus status);

    /** Aufgaben eines Kindes, die ab {@code from} fällig sind, aufsteigend nach Fälligkeit. */
    List<Task> findByAssignedToIdAndDueDateGreaterThanEqualAndStatusOrderByDueDateAsc(
            Long userId, LocalDate from, TaskStatus status);

    @Query("""
            select t from Task t
            where t.assignedTo.id = :userId and t.dueDate >= :from and t.status <> :excluded
            order by t.dueDate asc""")
    List<Task> findUpcomingExcludingStatus(Long userId, LocalDate from, TaskStatus excluded);
}
