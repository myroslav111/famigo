package infokom.info.famigo.service;

import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardService {
    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;

    }

    public void save(Reward reward) {
         rewardRepository.save(reward);
    }

    public List<Reward> findByChildId(Long childId) {
        return rewardRepository.findByChildId(childId);
    }

    public void delete(Long id) {
        rewardRepository.deleteById(id);
    }

}
