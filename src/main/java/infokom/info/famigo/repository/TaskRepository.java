package infokom.info.famigo.repository;

import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User user);
}
