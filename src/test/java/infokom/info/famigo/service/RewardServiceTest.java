package infokom.info.famigo.service;

import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.exception.InsufficientStarsException;
import infokom.info.famigo.repository.ChildRewardTransactionRepository;
import infokom.info.famigo.repository.RewardOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardServiceTest extends ServiceTestSupport {

    @Autowired RewardService rewardService;
    @Autowired RewardOptionRepository optionRepository;
    @Autowired ChildRewardTransactionRepository transactionRepository;

    RewardOption option;

    @BeforeEach
    void createOption() {
        option = new RewardOption("Eis", "Ein Eis", 4, RewardCategory.MATERIELL);
        option.setCreateBy(parent);
        option = optionRepository.save(option);
    }

    @Test
    void redeemBuchtSterneAbUndLegtEinloesungAn() {
        setStars(child, 10);
        loginAs(child);

        ChildRewardTransaction tx = rewardService.redeem(option.getId());

        assertThat(starsOf(child)).isEqualTo(6);
        assertThat(transactionRepository.findById(tx.getId())).get()
                .extracting(ChildRewardTransaction::getStarsSpent).isEqualTo(4);
    }

    @Test
    void redeemOhneGenugSterneAendertNichts() {
        setStars(child, 3);
        loginAs(child);

        assertThatThrownBy(() -> rewardService.redeem(option.getId()))
                .isInstanceOf(InsufficientStarsException.class);

        assertThat(starsOf(child)).isEqualTo(3);
        assertThat(transactionRepository.count()).isZero();
    }

    @Test
    void fulfillUndAcknowledge() {
        setStars(child, 10);
        loginAs(child);
        Long txId = rewardService.redeem(option.getId()).getId();

        loginAs(parent);
        rewardService.fulfill(txId);

        loginAs(child);
        rewardService.acknowledge(txId);

        ChildRewardTransaction tx = transactionRepository.findById(txId).orElseThrow();
        assertThat(tx.isImplemented()).isTrue();
        assertThat(tx.isViewedByChild()).isTrue();
    }
}
