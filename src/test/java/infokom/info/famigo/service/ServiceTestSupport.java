package infokom.info.famigo.service;

import infokom.info.famigo.TestcontainersConfiguration;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;

/** Gemeinsame Basis: echte DB (Testcontainers), Session über Mock gesteuert. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
abstract class ServiceTestSupport {

    @MockitoBean
    SessionService sessionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbc;

    User parent;
    User child;

    @BeforeEach
    void resetData() {
        jdbc.execute("""
                delete from reward;
                delete from child_reward_transaction;
                delete from tasks;
                delete from parent_child;
                delete from reward_option where create_by_id is not null;
                delete from users;
                """);

        child = userRepository.save(newUser("kind", UserRole.CHILD, 0));
        parent = newUser("eltern", UserRole.PARENT, 0);
        parent.getChildren().add(child);
        parent = userRepository.save(parent);
    }

    void loginAs(User user) {
        when(sessionService.getCurrentUserId()).thenReturn(user.getId());
    }

    int starsOf(User user) {
        return userRepository.findById(user.getId()).orElseThrow().getStars();
    }

    void setStars(User user, int stars) {
        jdbc.update("update users set stars = ? where id = ?", stars, user.getId());
    }

    private static User newUser(String username, UserRole role, int stars) {
        User user = new User();
        user.setName(username);
        user.setUsername(username);
        user.setPasswordHash("x");
        user.setRole(role);
        user.setStars(stars);
        return user;
    }
}
