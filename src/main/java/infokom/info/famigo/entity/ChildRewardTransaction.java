package infokom.info.famigo.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChildRewardTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User child;

    @ManyToOne
    private RewardOption reward;

    private int starsSpent = 0;

    private LocalDateTime redeemedAt;

}
