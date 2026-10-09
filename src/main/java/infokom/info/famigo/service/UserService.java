package infokom.info.famigo.service;

import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.exception.DomainException;
import infokom.info.famigo.exception.NotFoundException;
import infokom.info.famigo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public UserService(UserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    public List<User> findAllChildren() {
        return userRepository.findByRole(UserRole.CHILD);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** Lädt den angemeldeten Benutzer frisch aus der DB (aktueller Sternestand, aktuelle Kinder). */
    public User getCurrentUser() {
        Long id = sessionService.getCurrentUserId();
        if (id == null) {
            throw new DomainException("Kein Benutzer angemeldet.");
        }
        return getById(id);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden."));
    }

    public List<User> findChildrenOfParent(User parent) {
        return new ArrayList<>(getById(parent.getId()).getChildren());
    }

    public List<User> findChildrenOfCurrentParent() {
        return new ArrayList<>(getCurrentUser().getChildren());
    }

    /** Liefert den aktuellen Benutzer, wenn er Elternteil ist und {@code childId} zu seinen Kindern gehört. */
    public User requireParentOf(Long childId) {
        User parent = getCurrentUser();
        boolean isOwnChild = parent.getRole() == UserRole.PARENT
                && parent.getChildren().stream().anyMatch(c -> c.getId().equals(childId));
        if (!isOwnChild) {
            throw new DomainException("Keine Berechtigung für dieses Kind.");
        }
        return parent;
    }

    /** Liefert den aktuellen Benutzer, wenn er ein Kind ist. */
    public User requireChild() {
        User child = getCurrentUser();
        if (child.getRole() != UserRole.CHILD) {
            throw new DomainException("Diese Aktion ist nur für Kinder möglich.");
        }
        return child;
    }

    @Transactional
    public void addParentToChildren(User child, Long parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Elternteil nicht gefunden."));

        child.getParents().add(parent);
        parent.getChildren().add(child);

        userRepository.save(parent);
    }
}
