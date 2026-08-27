package infokom.info.famigo.service;

import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.repository.ChildRewardTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChildRewardTransactionService {
    private final ChildRewardTransactionRepository childRewardTransactionRepository;

    public ChildRewardTransactionService(ChildRewardTransactionRepository childRewardTransactionRepository) {
        this.childRewardTransactionRepository = childRewardTransactionRepository;
    }

    public void save(ChildRewardTransaction childRewardTransaction) {
        childRewardTransactionRepository.save(childRewardTransaction);
    }

    public List<ChildRewardTransaction> getImplementedRewards(Set<User> children) {
       return childRewardTransactionRepository.findByImplementedFalseAndChildIn(children);
    }

    public List<ChildRewardTransaction> getImplementedRewardsAccepted(Long userId) {
        return childRewardTransactionRepository.findByImplementedTrueAndViewedByChildFalseAndChild_Id(userId).get();
    }

    /** Wie oft wurde diese Belohnung bereits eingetauscht? Verhindert das Loeschen benutzter Belohnungen. */
    public long countByRewardOption(Long rewardOptionId) {
        return childRewardTransactionRepository.countByReward_Id(rewardOptionId);
    }

    public void updateStatusImplemented(boolean statusExecuteReward, Long childRewardTransactionId) {
        Optional<ChildRewardTransaction> childRewardTransaction = childRewardTransactionRepository.findById(childRewardTransactionId);

        childRewardTransaction.get().setImplemented(statusExecuteReward);
        childRewardTransactionRepository.save(childRewardTransaction.get());
    }

    public void updateStatusViewedByChild(boolean statusViewedByChild, Long childRewardTransactionId) {
        Optional<ChildRewardTransaction> childRewardTransaction = childRewardTransactionRepository.findById(childRewardTransactionId);
        childRewardTransaction.get().setViewedByChild(statusViewedByChild);
        childRewardTransactionRepository.save(childRewardTransaction.get());
    }
}
