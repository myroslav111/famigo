package infokom.info.famigo.service;

import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.repository.RewardOptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RewardOptionService {
    private final RewardOptionRepository rewardOptionRepository;

    public RewardOptionService(RewardOptionRepository rewardOptionRepository) {
        this.rewardOptionRepository = rewardOptionRepository;
    }

    public void create(RewardOption rewardOption){
        rewardOptionRepository.save(rewardOption);
    }

    public void update(RewardOption rewardOption){
        rewardOptionRepository.save(rewardOption);
    }

    public void delete(Long id) {
        rewardOptionRepository.deleteById(id);
    }

    public Optional<RewardOption> findById(Long id) {
        return rewardOptionRepository.findById(id);
    }

    public List<RewardOption> getRewardOptions() {
        return rewardOptionRepository.findAll();
    }

    public List<RewardOption> getRewardOptionByCategory(String category) {
        log.debug("getRewardOptionByCategory {}", category);

        return getRewardOptionByCategory(
                RewardCategory.valueOf(category)
        );
    }

    public List<RewardOption> getRewardOptionByCategory(RewardCategory category) {
        return rewardOptionRepository.findByCategory(category);
    }

}
