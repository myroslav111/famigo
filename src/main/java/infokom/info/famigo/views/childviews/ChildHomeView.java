package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.components.TaskDialog;
import infokom.info.famigo.views.securityviews.LoginView;

import java.util.List;

@Route(value = "child", layout =  MainViewLayout.class)
@PageTitle("Childbereich")
public class ChildHomeView extends VerticalLayout implements BeforeEnterObserver {
    private final SessionService sessionService;
    private final TaskService taskService;
    private final RewardService rewardService;
    private final UserService userService;

    private VerticalLayout currentTaskLayout;

    public ChildHomeView(SessionService sessionService, TaskService taskService, RewardService rewardService, UserService userService) {
        this.sessionService = sessionService;
        this.taskService = taskService;
        this.rewardService = rewardService;
        this.userService = userService;

        setSpacing(true);
        setWidthFull();
        setHeightFull();
        setPadding(true);

        User child = sessionService.getCurrentUser();
        if(child == null) {
            add(new H1("Kein benutzer eingeloggt"));
            return;
        }

        currentTaskLayout = new VerticalLayout();
        currentTaskLayout.setSpacing(true);
        currentTaskLayout.setWidthFull();
        currentTaskLayout.setHeightFull();
        currentTaskLayout.getStyle().set("overflow", "auto");

        refreshTasks();

        add(new H2("Willkommen " + child.getUsername() + "!"));
        add(new Paragraph("Du hast aktuell ⭐" + child.getStars() + " gesammelt!"));

        add(new H4("Aktuell zur Erledigung "));
        add(currentTaskLayout);
    }

    private Component createTaskChildCard(Task task) {
        Card  card = new Card();
        card.getStyle().set("border", "1px solid #ccc");
        card.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(task.getTitle()));
        content.add(new Span("Sterne: " + task.getStarsReward()));
        content.add(new Span("Fällig bis zum Ende des Tages"));

        Button detailsButton = new Button("Beschreibung");
        detailsButton.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph(task.getDescription()));
            dialog.setWidth("60%");
            dialog.open();
        });

        Button markAsDoneButton = new Button("ist Erledigt");
        markAsDoneButton.addClickListener(event -> {
            task.setStatus(TaskStatus.DONE);
            taskService.updateTask(task);

            Reward reward = new Reward();
            reward.setTitle(task.getTitle());
            reward.setDescription(task.getDescription());
            reward.setStarCost(task.getStarsReward());
            reward.setChild(userService.getCurrentUser());
            reward.setTask(task);

            rewardService.save(reward);

            refreshTasks();
        });

        content.add(detailsButton);
        card.add(content, markAsDoneButton);
        return card;
    }

    private void refreshTasks() {
        currentTaskLayout.removeAll();

        User child = sessionService.getCurrentUser();
        List<Task> currentTask = taskService.findTasksDueTodayForChild(child.getId());

        if(!currentTask.isEmpty()) {
            currentTask.forEach(task -> currentTaskLayout.add(createTaskChildCard(task)));
        }else {
            currentTaskLayout.add(new Span("Aktuell ist keine individuelle Aufgaben zur Erledigung"));
        }
    }

    public void beforeEnter(BeforeEnterEvent event) {
        if(!sessionService.isLoggedIn()){
            event.forwardTo(LoginView.class);
        }
    }
}
