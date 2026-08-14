package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.securityviews.LoginView;

import java.util.List;

@Route(value = "parent", layout = MainViewLayout.class)
@PageTitle("Elternbereich")
public class ParentHomeView extends VerticalLayout implements BeforeEnterObserver {
    private final SessionService sessionService;
    private final TaskService taskService;
    private final UserService userService;



    public ParentHomeView(SessionService sessionService, TaskService taskService, UserService userService) {
        this.sessionService = sessionService;
        this.taskService = taskService;
        this.userService = userService;

        addClassName("famigo-page");
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        User currentParent = sessionService.getCurrentUser();

        add(createHero(currentParent));

        H3 childrenTitle = new H3("Deine Kinder");
        childrenTitle.addClassName("famigo-section-title");
        add(childrenTitle);

        List<User> children = userService.findChildrenOfParent(currentParent);
        if (children.isEmpty()) {
            add(createEmptyState());
        } else {
            Div grid = new Div();
            grid.addClassName("famigo-child-grid");
            for (User child : children) {
                grid.add(createChildCard(child));
            }
            add(grid);
        }

        Button rewardsManageBtn = new Button("Belohnungen verwalten", new Icon(VaadinIcon.GIFT));
        rewardsManageBtn.addClassName("famigo-button");
        rewardsManageBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        rewardsManageBtn.addClickListener(e -> UI.getCurrent().navigate("manage/rewards"));

        add(rewardsManageBtn);
        setAlignSelf(Alignment.START, rewardsManageBtn); // Button soll nicht auf ganze Breite gezogen werden

    }

    /** Begruessungskarte mit Familiencode – im Gradient-Look des Logins. */
    private Component createHero(User currentParent) {
        Div hero = new Div();
        hero.addClassName("famigo-hero");

        H2 greeting = new H2("Hallo " + currentParent.getName() + "! 👋");
        greeting.addClassName("famigo-hero-greeting");

        Paragraph subtitle = new Paragraph("Willkommen im Elternbereich. Verteile Aufgaben und belohne mit Sternen. ✨");
        subtitle.addClassName("famigo-hero-subtitle");

        Span codeLabel = new Span("Code für dein Kind");
        codeLabel.addClassName("famigo-code-label");

        Span codeValue = new Span(String.valueOf(currentParent.getId()));
        codeValue.addClassName("famigo-code-value");

        Div codeCard = new Div(codeLabel, codeValue);
        codeCard.addClassName("famigo-code-card");

        hero.add(greeting, subtitle, codeCard);
        return hero;
    }

    /** Freundlicher Hinweis, solange noch kein Kind verknuepft ist. */
    private Component createEmptyState() {
        Div card = new Div();
        card.addClassName("famigo-empty-card");

        Span mascot = new Span("⭐");
        mascot.addClassName("famigo-empty-mascot");

        H3 title = new H3("Noch kein Kind dabei");
        title.addClassName("famigo-empty-title");

        Paragraph text = new Paragraph("Gib deinem Kind den Code von oben – damit kann es sich registrieren und erscheint dann hier.");
        text.addClassName("famigo-empty-text");

        card.add(mascot, title, text);
        return card;
    }

    private Component createChildCard(User child){
        Div card = new Div();
        card.addClassName("famigo-child-card");

        Span avatar = new Span(initialOf(child.getName()));
        avatar.addClassName("famigo-avatar");

        Span name = new Span(child.getName());
        name.addClassName("famigo-child-name");

        Span stars = new Span("⭐ " + child.getStars() + " Sterne");
        stars.addClassName("famigo-star-badge");

        Div info = new Div(name, stars);
        info.addClassName("famigo-child-info");

        card.add(avatar, info);
        return card;
    }

    private String initialOf(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        return name.substring(0, 1).toUpperCase();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent e){
        if(!sessionService.isLoggedIn()){
            e.forwardTo(LoginView.class);
        }
    }
}
