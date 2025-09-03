package infokom.info.famigo.repository;

import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User user);
    Optional<Task> findById(Long id);
    int countByAssignedTo(User user);
}
