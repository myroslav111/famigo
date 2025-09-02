package infokom.info.famigo.service;

import infokom.info.famigo.repository.ChildRewardTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class ChildRewardTransactionService {
    private final ChildRewardTransactionRepository childRewardTransactionRepository;

    public ChildRewardTransactionService(ChildRewardTransactionRepository childRewardTransactionRepository) {
        this.childRewardTransactionRepository = childRewardTransactionRepository;
    }
}
