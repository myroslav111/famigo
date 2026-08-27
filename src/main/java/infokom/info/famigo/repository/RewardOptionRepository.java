package infokom.info.famigo.repository;

import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardOptionRepository extends JpaRepository<RewardOption, Long> {
    Optional<RewardOption> findById(Long childId);

    List<RewardOption> findByCategory(RewardCategory category);
}
