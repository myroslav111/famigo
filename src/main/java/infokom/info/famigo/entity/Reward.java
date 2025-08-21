package infokom.info.famigo.entity;

import infokom.info.famigo.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reward")
@Getter @Setter
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int starCost;

    private boolean redeemed = false;

    @ManyToOne
    @JoinColumn(name = "child_id",  nullable = false)
    private User child;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

}
