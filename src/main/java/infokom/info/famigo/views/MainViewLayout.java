package infokom.info.famigo.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.views.securityviews.LoginView;


public class MainViewLayout extends VerticalLayout implements RouterLayout {

    private final SessionService sessionService;
    private final Div contentArea = new Div();

    public MainViewLayout(SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // === Header ===
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("100px");
        header.getStyle()
                .set("border", "2px solid orange")
                .set("background-color", "blue");
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        header.setAlignItems(Alignment.CENTER);

        // Optional Logo or Title
        Image logo = new Image("images/logo.png", "Logo");
        logo.setHeight("60px");

        Button logoutButton = new Button("Logout", e -> {
            sessionService.logout();
            UI.getCurrent().navigate(LoginView.class);
        });
        logoutButton.getStyle().setColor("black").setBackgroundColor("white");

        header.add(logo, logoutButton);

        add(header);

        // Content
        contentArea.setHeightFull();
        contentArea.getStyle().set("padding", "1em");
        add(contentArea);
        expand(contentArea); // wichtig, damit Footer unten bleibt

        // === Footer ===
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setHeight("60px");
        footer.getStyle()
                .set("border-top", "2px solid black")
                .set("padding", "10px");
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setAlignItems(Alignment.CENTER);

        Button taskButton = new Button("Task", e -> {
            UI.getCurrent().navigate("tasks");
        });

        Button rewardButton = new Button("Reward", e -> {
            UI.getCurrent().navigate("rewards");
        });

        Button addTaskButton = new Button("+", e -> {
        });

        footer.add(taskButton, addTaskButton, rewardButton);
        add(footer);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        // hier wird die View eingefügt
        contentArea.getElement().appendChild(content.getElement());
    }

}
