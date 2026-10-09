package infokom.info.famigo.repository;

import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChildRewardTransactionRepository extends JpaRepository<ChildRewardTransaction, Long> {
    List<ChildRewardTransaction> findByImplementedTrueAndViewedByChildFalseAndChild_Id(Long childId);
        List<ChildRewardTransaction> findByImplementedFalseAndChildIn(Set<User> children);
        Optional<ChildRewardTransaction> findById(Long id);

        long countByReward_Id(Long rewardOptionId);


}
