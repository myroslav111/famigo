package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "child/tasks",  layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ChildTaskView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TaskService taskService;
    private final UserService userService;
    private final TaskTemplateService taskTemplateService;
    private final RewardService rewardService;

    private Div standardTasksLayout;
    private Div specialTasksLayout;
    private TabSheet tabSheet;

    private ChildTaskView(TaskService taskService,  UserService userService,  TaskTemplateService taskTemplateService,  RewardService rewardService) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskTemplateService = taskTemplateService;
        this.rewardService = rewardService;

        addClassName("famigo-page");
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        H2 pageTitle = new H2("Deine Aufgaben ✅");
        pageTitle.addClassName("famigo-page-title");

        Paragraph pageSubtitle = new Paragraph("Erledige Aufgaben und sammle Sterne. Tippe auf „Erledigt!“, wenn du fertig bist.");
        pageSubtitle.addClassName("famigo-page-subtitle");

        add(pageTitle, pageSubtitle);

        standardTasksLayout = new Div();
        standardTasksLayout.addClassName("famigo-task-grid");

        specialTasksLayout = new Div();
        specialTasksLayout.addClassName("famigo-task-grid");

        refreshTasks();

        tabSheet = new TabSheet();
        tabSheet.addClassName("famigo-tabsheet");
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        tabSheet.getStyle().set("overflow", "auto");

        tabSheet.add("Standardaufgaben",  standardTasksLayout);
        tabSheet.add("Extra-Aufgaben",  specialTasksLayout);

        add(tabSheet);
        expand(tabSheet);
    }

    private Component createSpecialTaskCard(Task  task) {
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

        Button detailsButton = createDetailsButton(task.getTitle(), task.getDescription());

        Button markAsDoneButton = createDoneButton();
        markAsDoneButton.addClickListener(e -> {
            task.setStatus(TaskStatus.DONE);
            taskService.updateTask(task);
            refreshTasks();
        });

        Div actions = new Div(detailsButton, markAsDoneButton);
        actions.addClassName("famigo-task-actions");
        body.add(actions);

        card.add(createStarsBadge(task.getStarsReward()), body);
        return card;
    }

    private Component createStandardTaskCard(TaskTemplate task) {
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

        Button detailsButton = createDetailsButton(task.getTitle(), task.getDescription());

        Button markAsDoneButton = createDoneButton();
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

        Div actions = new Div(detailsButton, markAsDoneButton);
        actions.addClassName("famigo-task-actions");
        body.add(actions);

        card.add(createStarsBadge(task.getStarsReward()), body);
        return card;
    }

    /** Sterne-Belohnung als runder Blickfang links auf der Karte. */
    private Component createStarsBadge(int stars) {
        Span count = new Span(String.valueOf(stars));
        count.addClassName("famigo-task-stars-count");

        Div badge = new Div(new Span("⭐"), count);
        badge.addClassName("famigo-task-stars");
        badge.getElement().setAttribute("title", stars + " Sterne");
        return badge;
    }

    /** Frist-Chip; heute oder ueberfaellig wird rot hervorgehoben. */
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

    private Button createDetailsButton(String title, String description) {
        Button detailsButton = new Button("Details");
        detailsButton.addClassName("famigo-task-details-button");
        detailsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        detailsButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle(title);
            dialog.add(new Paragraph(description == null || description.isBlank()
                    ? "Zu dieser Aufgabe gibt es keine weitere Beschreibung."
                    : description));

            Button closeButton = new Button("Schließen", event -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getFooter().add(closeButton);

            dialog.setWidth("min(30rem, 90vw)");
            dialog.open();
        });
        return detailsButton;
    }

    private Button createDoneButton() {
        Button markAsDoneButton = new Button("Erledigt!", new Icon(VaadinIcon.CHECK));
        markAsDoneButton.addClassName("famigo-task-done-button");
        markAsDoneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        return markAsDoneButton;
    }

    /** Freundlicher Hinweis, wenn ein Tab keine Aufgaben enthaelt. */
    private Component createEmptyState(String text) {
        Div card = new Div();
        card.addClassName("famigo-empty-card");

        Span mascot = new Span("🎉");
        mascot.addClassName("famigo-empty-mascot");

        H3 title = new H3("Alles erledigt!");
        title.addClassName("famigo-empty-title");

        Paragraph hint = new Paragraph(text);
        hint.addClassName("famigo-empty-text");

        card.add(mascot, title, hint);
        return card;
    }

    private void refreshTasks(){
        standardTasksLayout.removeAll();
        specialTasksLayout.removeAll();

        User user = userService.getCurrentUser();
        List<Task> specialTasks = taskService.findStillValidTasksAndStatusPending(user.getId());

        if (!specialTasks.isEmpty()){
            specialTasks.reversed().forEach(specialTask -> specialTasksLayout.add(createSpecialTaskCard(specialTask)));
        }else{
            specialTasksLayout.add(createEmptyState("Gerade wartet keine Extra-Aufgabe auf dich."));
        }

        List<TaskTemplate> standardTasks = taskTemplateService.findAll();
        if (!standardTasks.isEmpty()){
            standardTasks.reversed().forEach(standardTask -> standardTasksLayout.add(createStandardTaskCard(standardTask)));
        }else{
            standardTasksLayout.add(createEmptyState("Hier gibt es momentan keine Standardaufgaben."));
        }

    }

}
