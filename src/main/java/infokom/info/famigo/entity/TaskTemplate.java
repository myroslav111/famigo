package infokom.info.famigo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_templates")
@Getter @Setter @NoArgsConstructor
public class TaskTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int starsReward;

    public TaskTemplate(String tasks, String description, int starsReward) {
        this.title = tasks;
        this.description = description;
        this.starsReward = starsReward;
    }
}
