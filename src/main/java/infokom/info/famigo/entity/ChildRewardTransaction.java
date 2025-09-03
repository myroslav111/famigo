package infokom.info.famigo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter @NoArgsConstructor
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

    @Column(nullable = false)
    private boolean implemented  = false;

    @Column(nullable = false)
    private boolean viewedByChild = false;
}
