package infokom.info.famigo.views.securityviews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.AuthService;
import infokom.info.famigo.service.SessionService;

@Route("")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public LoginView(AuthService authService, SessionService sessionService) {
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button loginButton = new Button("Login");
        loginButton.addClickListener(e -> {
            try {
                User user = authService.login(username.getValue(), password.getValue());
                sessionService.login(user);

                if(user.getRole() == UserRole.PARENT) {
                    getUI().ifPresent(ui -> ui.navigate("parent"));
                } else if (user.getRole() == UserRole.CHILD) {
                    getUI().ifPresent(ui -> ui.navigate("child"));
                }else{
                    Notification.show("Unbekannte Rolle!");
                }

                Notification.show("Login successful " + user.getUsername());
//                getUI().ifPresent(ui -> ui.navigate(HomeView.class));

            } catch (Exception ex) {
                Notification.show("Fehler: " + ex.getMessage());
            }
        });

        Anchor registerLink = new Anchor("register", "Kein Konto? Das ist wie Pizza ohne Käse. Hol dir eins!");
        registerLink.getStyle().set("margin-top", "1em");

        add(username, password, loginButton, registerLink);
    }
}
