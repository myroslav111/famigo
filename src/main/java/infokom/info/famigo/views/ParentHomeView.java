package infokom.info.famigo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.service.SessionService;

@Route("parent")
public class ParentHomeView extends VerticalLayout implements BeforeEnterObserver {
    private final SessionService sessionService;

    public ParentHomeView(SessionService sessionService) {
        this.sessionService = sessionService;
        add(new H1("Parent Home"));

        Button button = new Button("Logout");
        button.addClickListener(e -> {
            sessionService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        add(button);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent e){
        if(!sessionService.isLoggedIn()){
            e.forwardTo(LoginView.class);
        }
    }
}
