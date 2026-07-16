package infokom.info.famigo.views.securityviews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Maskottchen: ein winkender Stern (die App belohnt Kinder mit Sternen)
        Span mascot = new Span("⭐");
        mascot.addClassName("login-mascot");

        H1 title = new H1("famigo");
        title.addClassName("login-title");

        Paragraph subtitle = new Paragraph("Schön, dass du da bist! Melde dich an und sammle Sterne. ✨");
        subtitle.addClassName("login-subtitle");

        TextField username = new TextField("Benutzername");
        username.setPlaceholder("Wie heißt du?");
        username.setPrefixComponent(new Icon(VaadinIcon.USER));
        username.addClassName("login-field");
        username.setAutofocus(true);

        PasswordField password = new PasswordField("Passwort");
        password.setPlaceholder("Dein Geheimcode");
        password.setPrefixComponent(new Icon(VaadinIcon.LOCK));
        password.addClassName("login-field");

        Button loginButton = new Button("Los geht's!", new Icon(VaadinIcon.ROCKET));
        loginButton.addClassName("login-button");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.addClickShortcut(Key.ENTER);
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

            } catch (Exception ex) {
                Notification.show("Fehler: " + ex.getMessage());
            }
        });

        Anchor registerLink = new Anchor("register", "Kein Konto? Das ist wie Pizza ohne Käse. Hol dir eins!");
        registerLink.addClassName("login-register-link");

        Div card = new Div(mascot, title, subtitle, username, password, loginButton, registerLink);
        card.addClassName("login-card");

        add(card);
    }
}
