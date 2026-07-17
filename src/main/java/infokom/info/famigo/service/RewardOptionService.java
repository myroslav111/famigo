package infokom.info.famigo.service;

import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.repository.RewardOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardOptionService {
    private final RewardOptionRepository rewardOptionRepository;

    public RewardOptionService(RewardOptionRepository rewardOptionRepository) {
        this.rewardOptionRepository = rewardOptionRepository;
    }

    public void create(RewardOption rewardOption){
        rewardOptionRepository.save(rewardOption);
    }

    public List<RewardOption> getRewardOptions() {
        return rewardOptionRepository.findAll();
    }

    public List<RewardOption> getRewardOptionByCategory(String category) {
        return rewardOptionRepository.findAll().stream()
                .filter(r -> r.getCategory().equals(RewardCategory.valueOf(category)))
                .toList();
    }
}
