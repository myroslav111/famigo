package infokom.info.famigo.service;

import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.repository.ChildRewardTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/** Lesezugriffe auf eingelöste Belohnungen. Schreibende Use-Cases liegen im {@link RewardService}. */
@Service
public class ChildRewardTransactionService {
    private final ChildRewardTransactionRepository childRewardTransactionRepository;

    public ChildRewardTransactionService(ChildRewardTransactionRepository childRewardTransactionRepository) {
        this.childRewardTransactionRepository = childRewardTransactionRepository;
    }

    public List<ChildRewardTransaction> getImplementedRewards(Set<User> children) {
       return childRewardTransactionRepository.findByImplementedFalseAndChildIn(children);
    }

    public List<ChildRewardTransaction> getImplementedRewardsAccepted(Long userId) {
        return childRewardTransactionRepository.findByImplementedTrueAndViewedByChildFalseAndChild_Id(userId);
    }

    /** Wie oft wurde diese Belohnung bereits eingetauscht? Verhindert das Loeschen benutzter Belohnungen. */
    public long countByRewardOption(Long rewardOptionId) {
        return childRewardTransactionRepository.countByReward_Id(rewardOptionId);
    }
}
