package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public void updateUserStar(int countOfStars){
        User user = getCurrentUser();
        user.setStars(countOfStars);

        userRepository.save(user);
    }

    public List<User> findChildrenOfParent(User parent) {
        Optional<User> managedParent = Optional.ofNullable(userRepository.findUserById(parent.getId())
                .orElseThrow(() -> new RuntimeException("Eltern nicht gefunden")));

        return new ArrayList<>(managedParent.get().getChildren());
    }

    public List<User> findChildrenOfCurrentParent() {
        User currentParent = sessionService.getCurrentUser();

        return new ArrayList<>(currentParent.getChildren());
    }

    @Transactional
    public void addParentToChildren(User child, Long parentId) {
        Optional<User> parent = Optional.ofNullable(userRepository.findUserById(parentId).orElseThrow(() -> new RuntimeException("Eltern nicht gefunden")));

        child.getParents().add(parent.get());
        parent.get().getChildren().add(child);

        userRepository.save(parent.get());
    }
}
