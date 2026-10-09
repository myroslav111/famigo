package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.exception.DomainException;
import infokom.info.famigo.exception.NotFoundException;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private static final String LOGIN_FAILED = "Benutzername oder Passwort falsch.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String username, String password, UserRole role, Long parentId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DomainException("Benutzername ist bereits vergeben.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);

        if (role.equals(UserRole.CHILD) && parentId != null) {
            User parent = userRepository.findById(parentId)
                    .orElseThrow(() -> new NotFoundException("Elternteil nicht gefunden."));

            user.getParents().add(parent);
            parent.getChildren().add(user);

            userRepository.save(parent);
        }

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException(LOGIN_FAILED));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new DomainException(LOGIN_FAILED);
        }

        return user;
    }

}
