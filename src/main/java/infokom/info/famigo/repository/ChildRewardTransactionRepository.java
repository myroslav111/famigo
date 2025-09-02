package infokom.info.famigo.repository;

import infokom.info.famigo.entity.ChildRewardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRewardTransactionRepository extends JpaRepository<ChildRewardTransaction, Long> {
        List<ChildRewardTransaction> findByChildId(Long childId);
}
