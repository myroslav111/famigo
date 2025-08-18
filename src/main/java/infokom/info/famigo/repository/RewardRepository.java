package infokom.info.famigo.repository;

import infokom.info.famigo.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByChildId(Long childId);
}
