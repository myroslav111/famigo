package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.TaskTemplateService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.time.LocalDate;
import java.util.List;

@Route(value = "child/tasks",  layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ChildTaskView extends VerticalLayout {
    private final TaskService taskService;
    private final UserService userService;
    private final TaskTemplateService taskTemplateService;
    private final RewardService rewardService;

    private VerticalLayout standardTasksLayout;
    private VerticalLayout specialTasksLayout;
    private TabSheet tabSheet;

    private ChildTaskView(TaskService taskService,  UserService userService,  TaskTemplateService taskTemplateService,  RewardService rewardService) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskTemplateService = taskTemplateService;
        this.rewardService = rewardService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        tabSheet.getStyle().set("overflow", "auto");

        standardTasksLayout = new VerticalLayout();
        standardTasksLayout.setWidthFull();
        standardTasksLayout.setHeightFull();
        standardTasksLayout.setSpacing(true);
        standardTasksLayout.getStyle().set("overflow", "auto");

        specialTasksLayout = new VerticalLayout();
        specialTasksLayout.setWidthFull();
        specialTasksLayout.setHeightFull();
        specialTasksLayout.setSpacing(true);
        specialTasksLayout.getStyle().set("overflow", "auto");

        refreshTasks();

        tabSheet.add("Standardaufgaben",  standardTasksLayout);
        tabSheet.add("Specialaufgaben",  specialTasksLayout);

        add(tabSheet);

        add(new H1("Child Tasks ✅"));
    }

    private Component createSpecialTaskCard(Task  task) {
        Card cardTask = new Card();
        cardTask.getStyle().set("border", "1px solid #ccc");
        cardTask.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(task.getTitle()));
        content.add(new Span("⭐: " + task.getStarsReward()));
        content.add(new Span("Fällig bis: " + (task.getDueDate() != null ? task.getDueDate().toString() : "nicht gesetzt")));

        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph(task.getDescription()));
            dialog.setWidth("60%");
            dialog.open();
        });

        Button markAsDoneButton = new Button("Mark as Done");
        markAsDoneButton.addClickListener(e -> {
            task.setStatus(TaskStatus.DONE);
            taskService.updateTask(task);
            refreshTasks();
        });

        content.add(detailsButton, markAsDoneButton);
        cardTask.add(content);
        return cardTask;
    }

    private Component createStandardTaskCard(TaskTemplate task) {
        Card cardTask = new Card();
        cardTask.getStyle().set("border", "1px solid #999");
        cardTask.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(task.getTitle()));
        content.add(new Span("Sterne: " + task.getStarsReward()));

        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph(task.getDescription()));
            dialog.setWidth("60%");
            dialog.open();
        });

        Button markAsDoneButton = new Button("Mark as Done");
        markAsDoneButton.addClickListener(e -> {

            Task doneTask = new Task();
            doneTask.setTitle(task.getTitle());
            doneTask.setStarsReward(task.getStarsReward());
            doneTask.setDescription(task.getDescription());
            doneTask.setDueDate(LocalDate.now());
            doneTask.setStatus(TaskStatus.DONE);
            doneTask.setAssignedTo(userService.getCurrentUser());
            doneTask.setTemplate(task);
            taskService.save(doneTask);

            Reward reward = new Reward();
            reward.setTitle(task.getTitle());
            reward.setDescription(task.getDescription());
            reward.setStarCost(task.getStarsReward());
            reward.setChild(userService.getCurrentUser());
            reward.setTask(doneTask);
            rewardService.save(reward);

            Notification.show("Erledigte Aufgabe wurde zum Elternteil geschickt.");
            refreshTasks();
        });

        content.add(detailsButton, markAsDoneButton);
        cardTask.add(content);
        return cardTask;
    }

    private void refreshTasks(){
        standardTasksLayout.removeAll();
        specialTasksLayout.removeAll();

        User user = userService.getCurrentUser();
        List<Task> specialTasks = taskService.findStillValidTasksAndStatusPending(user.getId());

        if (!specialTasks.isEmpty()){
            specialTasks.forEach(specialTask -> specialTasksLayout.add(createSpecialTaskCard(specialTask)));
        }else{
            specialTasksLayout.add("Aktuell ist keine individuelle Aufgaben zur Erledigung");
        }

        List<TaskTemplate> standardTasks = taskTemplateService.findAll();
        if (!standardTasks.isEmpty()){
            standardTasks.forEach(standardTask -> standardTasksLayout.add(createStandardTaskCard(standardTask)));
        }

    }

}
