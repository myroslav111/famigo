package infokom.info.famigo.service;

import com.vaadin.flow.server.VaadinSession;
import infokom.info.famigo.entity.User;
import org.springframework.stereotype.Service;

/**
 * Hält den angemeldeten Benutzer in der {@link VaadinSession}.
 * Das gespeicherte Objekt ist ein Snapshot vom Login (ID, Name, Rolle). Für veränderliche
 * Daten (Sterne, Kinder) immer {@link UserService#getCurrentUser()} verwenden, das frisch aus der DB lädt.
 */
@Service
public class SessionService {
    private static final String USER_KEY = "loggedUser";

    public void login(User user) {
        VaadinSession.getCurrent().setAttribute(USER_KEY, user);
    }

    public User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(USER_KEY);
    }

    /** ID des angemeldeten Benutzers oder {@code null}, wenn niemand angemeldet ist. */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user == null ? null : user.getId();
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public void logout() {
        VaadinSession.getCurrent().setAttribute(USER_KEY, null);
        VaadinSession.getCurrent().close();
    }
}
