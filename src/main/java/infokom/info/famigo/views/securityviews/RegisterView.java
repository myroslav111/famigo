package infokom.info.famigo.views.securityviews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.AuthService;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.components.AddParents;

@Route("register")
@PageTitle("Registrieren")
public class RegisterView extends VerticalLayout {

    private final AuthService authService;
    private final SessionService sessionService;
    private final UserService userService;

    public RegisterView(AuthService authService, SessionService sessionService, UserService userService) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.userService = userService;

        TextField nameField = new TextField("Name");
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField passwordRepeatField = new PasswordField("Passwort wiederholen");
        passwordRepeatField.setErrorMessage("Die Passwörter stimmen nicht überein");
        ComboBox<UserRole> roleField = new ComboBox<>("Rolle");
        roleField.setItems(UserRole.PARENT, UserRole.CHILD);
        roleField.setItemLabelGenerator(Enum::name);

        Button registerButton = new Button("Register");
        registerButton.addClassName("login-button");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.addClickShortcut(Key.ENTER);
        registerButton.addClickListener(event -> {
            if (!passwordsMatch(passwordField, passwordRepeatField)) {
                passwordRepeatField.setInvalid(true);
                Notification.show("Die Passwörter stimmen nicht überein", 5000, Notification.Position.MIDDLE);
                return;
            }
            passwordRepeatField.setInvalid(false);
            handleRegistrationAndLogin(nameField, usernameField, passwordField, roleField);
        });

        // Fehleranzeige aktualisieren, sobald wieder getippt wird
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordRepeatField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField.addValueChangeListener(event -> passwordRepeatField.setInvalid(
                !passwordRepeatField.isEmpty() && !passwordsMatch(passwordField, passwordRepeatField)));
        passwordRepeatField.addValueChangeListener(event -> passwordRepeatField.setInvalid(
                !passwordRepeatField.isEmpty() && !passwordsMatch(passwordField, passwordRepeatField)));

        Anchor loginLink = new Anchor("/", "Schon registriert? Dann hopp, zurück zum Login!");
//        loginLink.getStyle().set("margin-top", "1em");
        loginLink.addClassName("login-register-link");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Div card = new Div(nameField, usernameField, passwordField, passwordRepeatField, roleField, registerButton, loginLink);
        card.addClassName("login-card");

//        add(nameField, usernameField, passwordField, roleField, registerButton, loginLink);
        add(card);

    }

    private boolean passwordsMatch(PasswordField passwordField, PasswordField passwordRepeatField) {
        return passwordField.getValue().equals(passwordRepeatField.getValue());
    }

    private void handleRegistrationAndLogin(TextField nameField, TextField usernameField, PasswordField passwordField, ComboBox<UserRole> roleField) {

        if(roleField.getValue().equals(UserRole.CHILD)) {
            AddParents addParents = new AddParents(parentId ->
            {
                User child = authService.register(
                        nameField.getValue(),
                        usernameField.getValue(),
                        passwordField.getValue(),
                        roleField.getValue(),
                        null
                );
                sessionService.login(child);

                userService.addParentToChildren(child, parentId);

                Notification.show("Eltern erfolgreich hinzufügt");

                getUI().ifPresent(ui -> ui.navigate("child"));
            }
        );
            addParents.open();
        }else{
            try {
                User user = authService.register(
                        nameField.getValue(),
                        usernameField.getValue(),
                        passwordField.getValue(),
                        roleField.getValue(),
                        null
                );
                sessionService.login(user);
                Notification.show("Register successful " + user.getUsername());

                if(user.getRole() ==  UserRole.PARENT){
                    getUI().ifPresent(ui -> ui.navigate("parent"));
                } else if (user.getRole() == UserRole.CHILD) {
                    getUI().ifPresent(ui -> ui.navigate("child"));
                }else {
                    Notification.show("Unbekannte Rolle!");
                }

            } catch (Exception ex) {
                Notification.show("Fehler: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);

            }
        }

    }



}
