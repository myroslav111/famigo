package infokom.info.famigo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.service.SessionService;

@Route("")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final SessionService sessionService;

    public HomeView(SessionService sessionService) {
        this.sessionService = sessionService;


        add(new H1("Welcome to your new application"));
        add(new Paragraph("This is the home view"));

        add(new Paragraph("You can edit this view in src/main/java/infokom/info/famigo/views/HomeView.java"));

        Button button = new Button("Logout");
        button.addClickListener(e -> {
            sessionService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        add(button);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }

    }

}
