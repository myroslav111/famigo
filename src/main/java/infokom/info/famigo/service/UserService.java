package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import infokom.info.famigo.security.SecurityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public List<User> findAllChildren() {
        return userRepository.findByRole(UserRole.CHILD);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUser() {
        String username = SecurityUtils.getLoggedUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User wurde nicht gefunden"));
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
