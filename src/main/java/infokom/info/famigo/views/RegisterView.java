package infokom.info.famigo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.AuthService;
import infokom.info.famigo.service.SessionService;

@Route("register")
@PageTitle("Registrieren")
public class RegisterView extends VerticalLayout {

    private final AuthService authService;
    private final SessionService sessionService;
//    private final Router router;

    public RegisterView(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
//        this.router = router;

        TextField nameField = new TextField("Name");
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        ComboBox<UserRole> roleField = new ComboBox<>("Rolle");
        roleField.setItems(UserRole.PARENT, UserRole.CHILD);
        roleField.setItemLabelGenerator(Enum::name);

        Button registerButton = new Button("Register");
        registerButton.addClickListener(event -> handleRegistrationAndLogin(nameField, usernameField, passwordField, roleField));

        Anchor loginLink = new Anchor("login", "Schon registriert? Dann hopp, zurück zum Login!");
        loginLink.getStyle().set("margin-top", "1em");

        add(nameField, usernameField, passwordField, roleField, registerButton, loginLink);
    }

    private void handleRegistrationAndLogin(TextField nameField, TextField usernameField, PasswordField passwordField, ComboBox<UserRole> roleField) {
        try {
            User user = authService.register(
                    nameField.getValue(),
                    usernameField.getValue(),
                    passwordField.getValue(),
                    roleField.getValue()
            );
            sessionService.login(user);
            Notification.show("Register successful " + user.getUsername());

            if(user.getUserRole() ==  UserRole.PARENT){
                getUI().ifPresent(ui -> ui.navigate("parent"));
            } else if (user.getUserRole() == UserRole.CHILD) {
                getUI().ifPresent(ui -> ui.navigate("child"));
            }else {
                Notification.show("Unbekannte Rolle!");
            }
//            getUI().ifPresent(ui -> ui.navigate(HomeView.class));

        } catch (Exception ex) {
            Notification.show("Fehler: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);

        }
    }



}
