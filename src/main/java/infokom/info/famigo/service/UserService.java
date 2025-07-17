package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;

    }
    
    public List<User> findAllChildren() {
        return userRepository.findByRole(UserRole.CHILD);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUser() {
        User currentUser = sessionService.getCurrentUser();
        if(currentUser == null) {
            throw new UsernameNotFoundException("Kein Benutzer in der Session gefunden");
        }
        return currentUser;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
