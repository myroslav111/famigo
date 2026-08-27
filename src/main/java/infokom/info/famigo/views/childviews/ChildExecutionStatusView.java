package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.*;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "child/rewards", layout =  MainViewLayout.class)
@PageTitle("Rewards")
public class ChildExecutionStatusView extends VerticalLayout {
    private final TaskService taskService;
    private final UserService userService;

    private VerticalLayout taskDoneLayout;
    private VerticalLayout rewardApprovedLayout;
    private TabSheet taskTabSheet;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ChildExecutionStatusView(TaskService taskService, UserService userService) {
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
    }

    private void refreshTasks() {
        rewardApprovedLayout.removeAll();
        taskDoneLayout.removeAll();

        User currentUser = userService.getCurrentUser();
        List<Task> allDoneTasks = taskService.findTasksByAssignedToAndDueDateAndStatusDone(currentUser.getId());

        if (!allDoneTasks.isEmpty()) {
            allDoneTasks.reversed().forEach(task -> taskDoneLayout.add(createTaskDoneCard(task)));
        }else{
            taskDoneLayout.add("Aktuell ist nichts zur Bestätigung");
        }

        List<Task> allApprovedTask =  taskService.findTasksByAssignedToAndDueDateAndStatusApproved(currentUser.getId());

        if (!allApprovedTask.isEmpty()) {
            allApprovedTask.reversed().forEach(task -> rewardApprovedLayout.add(createTaskDoneCard(task)));
        }else{
            rewardApprovedLayout.add("Aktuell ist keine bestätigte Aufgaben");
        }
    }

    private Component createTaskDoneCard(Task task) {
        Div card = new Div();
        card.addClassName("famigo-task-card");

        Div body = new Div();
        body.addClassName("famigo-task-body");

        H3 title = new H3(task.getTitle());
        title.addClassName("famigo-task-title");
        body.add(title);

        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            Paragraph description = new Paragraph(task.getDescription());
            description.addClassName("famigo-task-desc");
            body.add(description);
        }

        body.add(createDueDateChip(task.getDueDate()));

        Span status = new Span();

        if (task.getStatus().equals(TaskStatus.DONE)) {
            status.setText("⏳ Wartet auf die Bestätigung");
        } else if (task.getStatus().equals(TaskStatus.APPROVED)) {
            status.setText("👍 Die Aufgabe wurde akzeptiert");
        }

        status.addClassName("famigo-task-chip");

        body.add(status);

        card.add(createStarsBadge(task.getStarsReward()), body);

        return card;
    }

    private Component createStarsBadge(int stars) {
        Span count = new Span(String.valueOf(stars));
        count.addClassName("famigo-task-stars-count");

        Div badge = new Div(new Span("⭐"), count);
        badge.addClassName("famigo-task-stars");
        badge.getElement().setAttribute("title", stars + " Sterne");

        return badge;
    }

    private Component createDueDateChip(LocalDate dueDate) {
        Span chip = new Span();
        chip.addClassName("famigo-task-chip");

        if (dueDate == null) {
            chip.setText("Ohne Frist");
        } else {
            chip.setText("Fällig bis " + dueDate.format(DATE_FORMAT));

            if (!dueDate.isAfter(LocalDate.now())) {
                chip.addClassName("famigo-task-chip-urgent");
            }
        }

        return chip;
    }


}
