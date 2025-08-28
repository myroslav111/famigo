package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        H2 title = new H2("Elternbereich");

        add(title);

        User currentParent = sessionService.getCurrentUser();

        List<User> children = userService.findChildrenOfParent(currentParent);
        for (User child : children) {
            add(createChildCard(child));
        }

    }

    private Component createChildCard(User child){
        HorizontalLayout layout = new HorizontalLayout();

        Image avatar = new Image("" , "Avatar");
        avatar.getStyle().set("background", "red");
        avatar.setWidth("64px");
        avatar.setHeight("64px");

        VerticalLayout details = new VerticalLayout();
        details.add(new Span("Name: " + child.getName()));
        details.add(new Span("Sterne: " + child.getStars()));

        layout.add(avatar, details);
        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent e){
        if(!sessionService.isLoggedIn()){
            e.forwardTo(LoginView.class);
        }
    }
}
