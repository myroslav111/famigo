package infokom.info.famigo.entity;

import infokom.info.famigo.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role;

    private int stars = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "parent_child",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private Set<User> children = new HashSet<>();

    @ManyToMany(mappedBy = "children")
    private Set<User> parents = new HashSet<>();

    public void addParent(User parent) {
        this.parents.add(parent);
    }

    public void deleteParent(User parent) {
        this.parents.remove(parent);
    }


}
