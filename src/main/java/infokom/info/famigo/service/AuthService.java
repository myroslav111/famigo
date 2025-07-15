package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String username, String password, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Benutzername ist bereits vergeben.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Benutzername nicht gefunden"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("password ist falsch");
        }

        return user;
    }

}
