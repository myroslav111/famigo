package infokom.info.famigo.repository;

import infokom.info.famigo.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByChildId(Long childId);

    Optional<Reward> findFirstByTaskId(Long taskId);
}
