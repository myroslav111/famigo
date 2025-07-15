package infokom.info.famigo.repository;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole role);
}
