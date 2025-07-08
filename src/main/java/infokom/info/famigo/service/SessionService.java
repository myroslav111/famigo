package infokom.info.famigo.service;

import com.vaadin.flow.server.VaadinSession;
import infokom.info.famigo.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

//@Component
//@SessionScope
@Service
public class SessionService {
    private static final String USER_KEY = "loggedUser";

   public void login(User user) {
       VaadinSession.getCurrent().setAttribute(USER_KEY, user);
   }

   public User getCurrentUser() {
       return (User) VaadinSession.getCurrent().getAttribute(USER_KEY);

   }

   public boolean isLoggedIn() {
       return getCurrentUser() != null;
   }

   public void logout() {
       VaadinSession.getCurrent().setAttribute(USER_KEY, null);
       VaadinSession.getCurrent().close();
   }
}
