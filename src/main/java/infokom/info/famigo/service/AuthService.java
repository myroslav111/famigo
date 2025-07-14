package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(String name, String username, String password, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Benutzername ist bereits vergeben.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setUserRole(role);

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
