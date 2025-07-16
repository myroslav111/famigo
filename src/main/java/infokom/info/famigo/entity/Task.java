package infokom.info.famigo.entity;

import infokom.info.famigo.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter @Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int starsReward;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status =  TaskStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "assignet_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    User createdBy;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private TaskTemplate template;


}
