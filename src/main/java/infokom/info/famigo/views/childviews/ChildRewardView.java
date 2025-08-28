package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.List;

@Route(value = "child/rewards", layout =  MainViewLayout.class)
@PageTitle("Rewards")
public class ChildRewardView extends VerticalLayout {
    private final TaskService taskService;
    private final UserService userService;

    private VerticalLayout taskDoneLayout;
    private VerticalLayout rewardApprovedLayout;
    private TabSheet taskTabSheet;

    private ChildRewardView(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        taskTabSheet = new TabSheet();
        taskTabSheet.setWidthFull();
        taskTabSheet.setHeightFull();
        taskTabSheet.getStyle().set("overflow", "auto");

        taskDoneLayout = new VerticalLayout();
        taskDoneLayout.setWidthFull();
        taskDoneLayout.setHeightFull();
        taskDoneLayout.setSpacing(true);
        taskDoneLayout.getStyle().set("overflow", "auto");

        rewardApprovedLayout = new VerticalLayout();
        rewardApprovedLayout.setWidthFull();
        rewardApprovedLayout.setHeightFull();
        rewardApprovedLayout.setSpacing(true);
        rewardApprovedLayout.getStyle().set("overflow", "auto");

        refreshTasks();

        taskTabSheet.add("Task was approved", rewardApprovedLayout);
        taskTabSheet.add("Tasks are pending", taskDoneLayout);

        add(taskTabSheet);

        add(new H1("Children Rewards"));
    }

    private void refreshTasks() {
        rewardApprovedLayout.removeAll();
        taskDoneLayout.removeAll();

        User currentUser = userService.getCurrentUser();
        List<Task> allDoneTasks = taskService.findTasksByAssignedToAndDueDateAndStatusDone(currentUser.getId());

        if (!allDoneTasks.isEmpty()) {
            allDoneTasks.forEach(task -> taskDoneLayout.add(createTaskDoneCard(task)));
        }else{
            taskDoneLayout.add("Aktuell ist nichts zur Bestätigung");
        }

        List<Task> allApprovedTask =  taskService.findTasksByAssignedToAndDueDateAndStatusApproved(currentUser.getId());

        if (!allApprovedTask.isEmpty()) {
            allApprovedTask.forEach(task -> rewardApprovedLayout.add(createTaskDoneCard(task)));
        }else{
            rewardApprovedLayout.add("Aktuell ist keine bestätigte Aufgaben");
        }
    }

    private Component createTaskDoneCard(Task task) {
        Card card = new Card();
        card.setWidthFull();
        card.getStyle().set("border", "1px solid #ccc");

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(task.getTitle()));
        content.add(new Span("⭐: " + task.getStarsReward()));
        content.add(task.getStatus().equals(TaskStatus.DONE) ? "⏳  Wartet auf die Bestätigung" : " \uD83D\uDC4D die Aufgabe wurde akzeptiert");

        card.add(content);

        return card;
    }
}
