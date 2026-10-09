package infokom.info.famigo.service;

import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.exception.DomainException;
import infokom.info.famigo.exception.InsufficientStarsException;
import infokom.info.famigo.exception.NotFoundException;
import infokom.info.famigo.repository.ChildRewardTransactionRepository;
import infokom.info.famigo.repository.RewardOptionRepository;
import infokom.info.famigo.repository.RewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Einreichungen ({@link Reward}) lesen sowie Einlösen, Erfüllen und Bestätigen von Belohnungen. */
@Service
public class RewardService {
    private final RewardRepository rewardRepository;
    private final RewardOptionRepository rewardOptionRepository;
    private final ChildRewardTransactionRepository transactionRepository;
    private final UserService userService;

    public RewardService(RewardRepository rewardRepository,
                         RewardOptionRepository rewardOptionRepository,
                         ChildRewardTransactionRepository transactionRepository,
                         UserService userService) {
        this.rewardRepository = rewardRepository;
        this.rewardOptionRepository = rewardOptionRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<Reward> findByChildId(Long childId) {
        return rewardRepository.findByChildId(childId);
    }

    /** Kind tauscht Sterne gegen eine Belohnungsoption. Abbuchung und Einlösung erfolgen atomar. */
    @Transactional
    public ChildRewardTransaction redeem(Long rewardOptionId) {
        User child = userService.requireChild();
        RewardOption option = rewardOptionRepository.findById(rewardOptionId)
                .orElseThrow(() -> new NotFoundException("Belohnung nicht gefunden."));

        if (!option.isActive()) {
            throw new DomainException("Diese Belohnung ist derzeit nicht verfügbar.");
        }
        if (child.getStars() < option.getCost()) {
            throw new InsufficientStarsException(child.getStars(), option.getCost());
        }
        child.setStars(child.getStars() - option.getCost());

        ChildRewardTransaction transaction = new ChildRewardTransaction();
        transaction.setRedeemedAt(LocalDateTime.now());
        transaction.setStarsSpent(option.getCost());
        transaction.setChild(child);
        transaction.setReward(option);
        return transactionRepository.save(transaction);
    }

    /** Elternteil hat die eingelöste Belohnung erfüllt. */
    @Transactional
    public void fulfill(Long transactionId) {
        ChildRewardTransaction transaction = getTransaction(transactionId);
        userService.requireParentOf(transaction.getChild().getId());
        transaction.setImplemented(true);
    }

    /** Kind hat die Erfüllung gesehen. */
    @Transactional
    public void acknowledge(Long transactionId) {
        User child = userService.requireChild();
        ChildRewardTransaction transaction = getTransaction(transactionId);
        if (!transaction.getChild().getId().equals(child.getId())) {
            throw new DomainException("Diese Belohnung gehört nicht dir.");
        }
        transaction.setViewedByChild(true);
    }

    private ChildRewardTransaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Eingelöste Belohnung nicht gefunden."));
    }
}
