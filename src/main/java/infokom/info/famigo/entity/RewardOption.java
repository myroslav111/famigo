package infokom.info.famigo.entity;


import infokom.info.famigo.entity.enums.RewardCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RewardOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private int cost;

    @Enumerated(EnumType.STRING)
    private RewardCategory category;

    @ManyToOne
    private User createBy;

    private boolean active = true;

    public RewardOption(String title, String description, int cost, RewardCategory category) {
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.category = category;
        this.active = true;
    }

}
