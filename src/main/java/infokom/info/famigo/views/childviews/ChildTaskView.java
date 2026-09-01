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

@Route(value = "child/tasks", layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ChildTaskView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TaskService taskService;
    private final UserService userService;
    private final TaskTemplateService taskTemplateService;
    private final RewardService rewardService;

    private Div standardTasksLayout;
    private Div specialTasksLayout;

    private TabSheet tabSheet;


    public ChildTaskView(
            TaskService taskService,
            UserService userService,
            TaskTemplateService taskTemplateService,
            RewardService rewardService
    ) {

        this.taskService = taskService;
        this.userService = userService;
        this.taskTemplateService = taskTemplateService;
        this.rewardService = rewardService;

        // =========================================================
        // Hauptlayout
        // =========================================================

        addClassName("famigo-page");

        setSpacing(true);
        setPadding(true);

        setWidthFull();
        setHeightFull();

        getStyle()
                .set("box-sizing", "border-box")
                .set("overflow", "hidden");

        // =========================================================
        // Überschrift
        // =========================================================

        H2 pageTitle =
                new H2("Deine Aufgaben ✅");

        pageTitle.addClassName(
                "famigo-page-title"
        );

        Paragraph pageSubtitle =
                new Paragraph(
                        "Erledige Aufgaben und sammle Sterne. "
                                + "Tippe auf „Erledigt!“, wenn du fertig bist."
                );

        pageSubtitle.addClassName(
                "famigo-page-subtitle"
        );

        add(
                pageTitle,
                pageSubtitle
        );

        // =========================================================
        // Aufgabenbereiche
        // =========================================================

        standardTasksLayout =
                createScrollableTaskLayout();

        specialTasksLayout =
                createScrollableTaskLayout();

        // =========================================================
        // TabSheet
        // =========================================================

        tabSheet = new TabSheet();

        tabSheet.addClassName(
                "famigo-tabsheet"
        );

        tabSheet.setWidthFull();
        tabSheet.setHeight("100%");

        tabSheet.getStyle()
                .set("min-height", "0")
                .set("overflow", "hidden")
                .set("box-sizing", "border-box");

        // =========================================================
        // Tabs
        // =========================================================

        tabSheet.add(
                "Standardaufgaben",
                standardTasksLayout
        );

        tabSheet.add(
                "Extra-Aufgaben",
                specialTasksLayout
        );

        // =========================================================
        // TabSheet hinzufügen
        // =========================================================

        add(tabSheet);

        // TabSheet nimmt den restlichen Platz ein
        expand(tabSheet);

        // =========================================================
        // Aufgaben laden
        // =========================================================

        refreshTasks();
    }


    /**
     * Erstellt einen scrollbaren Aufgabenbereich.
     *
     * Die Tabs bleiben dadurch fest an ihrer Position.
     * Nur die Aufgaben innerhalb des Tabs scrollen.
     */
    private Div createScrollableTaskLayout() {

        Div layout = new Div();

        layout.addClassName(
                "famigo-task-grid"
        );

        layout.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("min-height", "0")
                .set("box-sizing", "border-box")
                .set("padding-bottom", "70px");

        return layout;
    }


    // =============================================================
    // Specialaufgabe
    // =============================================================

    private Component createSpecialTaskCard(
            Task task
    ) {

        Div card = new Div();

        card.addClassName(
                "famigo-task-card"
        );

        Div body = new Div();

        body.addClassName(
                "famigo-task-body"
        );

        // ---------------------------------------------------------
        // Titel
        // ---------------------------------------------------------

        H3 title =
                new H3(task.getTitle());

        title.addClassName(
                "famigo-task-title"
        );

        body.add(title);

        // ---------------------------------------------------------
        // Beschreibung
        // ---------------------------------------------------------

        if (task.getDescription() != null
                && !task.getDescription().isBlank()) {

            Paragraph description =
                    new Paragraph(
                            task.getDescription()
                    );

            description.addClassName(
                    "famigo-task-desc"
            );

            body.add(description);
        }

        // ---------------------------------------------------------
        // Fälligkeitsdatum
        // ---------------------------------------------------------

        body.add(
                createDueDateChip(
                        task.getDueDate()
                )
        );

        // ---------------------------------------------------------
        // Details
        // ---------------------------------------------------------

        Button detailsButton =
                createDetailsButton(
                        task.getTitle(),
                        task.getDescription()
                );

        // ---------------------------------------------------------
        // Erledigt
        // ---------------------------------------------------------

        Button markAsDoneButton =
                createDoneButton();

        markAsDoneButton.addClickListener(e -> {

            task.setStatus(
                    TaskStatus.DONE
            );

            taskService.updateTask(
                    task
            );

            refreshTasks();
        });

        // ---------------------------------------------------------
        // Aktionen
        // ---------------------------------------------------------

        Div actions =
                new Div(
                        detailsButton,
                        markAsDoneButton
                );

        actions.addClassName(
                "famigo-task-actions"
        );

        body.add(actions);

        // ---------------------------------------------------------
        // Card
        // ---------------------------------------------------------

        card.add(
                createStarsBadge(
                        task.getStarsReward()
                ),
                body
        );

        return card;
    }


    // =============================================================
    // Standardaufgabe
    // =============================================================

    private Component createStandardTaskCard(
            TaskTemplate task
    ) {

        Div card = new Div();

        card.addClassName(
                "famigo-task-card"
        );

        Div body = new Div();

        body.addClassName(
                "famigo-task-body"
        );

        // ---------------------------------------------------------
        // Titel
        // ---------------------------------------------------------

        H3 title =
                new H3(task.getTitle());

        title.addClassName(
                "famigo-task-title"
        );

        body.add(title);

        // ---------------------------------------------------------
        // Beschreibung
        // ---------------------------------------------------------

        if (task.getDescription() != null
                && !task.getDescription().isBlank()) {

            Paragraph description =
                    new Paragraph(
                            task.getDescription()
                    );

            description.addClassName(
                    "famigo-task-desc"
            );

            body.add(description);
        }

        // ---------------------------------------------------------
        // Details
        // ---------------------------------------------------------

        Button detailsButton =
                createDetailsButton(
                        task.getTitle(),
                        task.getDescription()
                );

        // ---------------------------------------------------------
        // Erledigt
        // ---------------------------------------------------------

        Button markAsDoneButton =
                createDoneButton();

        markAsDoneButton.addClickListener(e -> {

            User currentUser =
                    userService.getCurrentUser();

            Task doneTask =
                    new Task();

            doneTask.setTitle(
                    task.getTitle()
            );

            doneTask.setStarsReward(
                    task.getStarsReward()
            );

            doneTask.setDescription(
                    task.getDescription()
            );

            doneTask.setDueDate(
                    LocalDate.now()
            );

            doneTask.setStatus(
                    TaskStatus.DONE
            );

            doneTask.setAssignedTo(
                    currentUser
            );

            doneTask.setTemplate(
                    task
            );

            taskService.save(
                    doneTask
            );

            // -----------------------------------------------------
            // Reward erstellen
            // -----------------------------------------------------

            Reward reward =
                    new Reward();

            reward.setTitle(
                    task.getTitle()
            );

            reward.setDescription(
                    task.getDescription()
            );

            reward.setStarCost(
                    task.getStarsReward()
            );

            reward.setChild(
                    currentUser
            );

            reward.setTask(
                    doneTask
            );

            rewardService.save(
                    reward
            );

            Notification.show(
                    "Erledigte Aufgabe wurde zum Elternteil geschickt."
            );

            refreshTasks();
        });

        // ---------------------------------------------------------
        // Aktionen
        // ---------------------------------------------------------

        Div actions =
                new Div(
                        detailsButton,
                        markAsDoneButton
                );

        actions.addClassName(
                "famigo-task-actions"
        );

        body.add(actions);

        // ---------------------------------------------------------
        // Card
        // ---------------------------------------------------------

        card.add(
                createStarsBadge(
                        task.getStarsReward()
                ),
                body
        );

        return card;
    }


    // =============================================================
    // Sterne
    // =============================================================

    private Component createStarsBadge(
            int stars
    ) {

        Span count =
                new Span(
                        String.valueOf(stars)
                );

        count.addClassName(
                "famigo-task-stars-count"
        );

        Div badge =
                new Div(
                        new Span("⭐"),
                        count
                );

        badge.addClassName(
                "famigo-task-stars"
        );

        badge.getElement().setAttribute(
                "title",
                stars + " Sterne"
        );

        return badge;
    }


    // =============================================================
    // Fälligkeitsdatum
    // =============================================================

    private Component createDueDateChip(
            LocalDate dueDate
    ) {

        Span chip =
                new Span();

        chip.addClassName(
                "famigo-task-chip"
        );

        if (dueDate == null) {

            chip.setText(
                    "Ohne Frist"
            );

        } else {

            chip.setText(
                    "Fällig bis "
                            + dueDate.format(DATE_FORMAT)
            );

            if (!dueDate.isAfter(
                    LocalDate.now()
            )) {

                chip.addClassName(
                        "famigo-task-chip-urgent"
                );
            }
        }

        return chip;
    }


    // =============================================================
    // Details
    // =============================================================

    private Button createDetailsButton(
            String title,
            String description
    ) {

        Button detailsButton =
                new Button("Details");

        detailsButton.addClassName(
                "famigo-task-details-button"
        );

        detailsButton.addThemeVariants(
                ButtonVariant.LUMO_TERTIARY,
                ButtonVariant.LUMO_SMALL
        );

        detailsButton.addClickListener(e -> {

            Dialog dialog =
                    new Dialog();

            dialog.setHeaderTitle(
                    title
            );

            dialog.add(
                    new Paragraph(
                            description == null
                                    || description.isBlank()
                                    ? "Zu dieser Aufgabe gibt es keine weitere Beschreibung."
                                    : description
                    )
            );

            Button closeButton =
                    new Button(
                            "Schließen",
                            event -> dialog.close()
                    );

            closeButton.addThemeVariants(
                    ButtonVariant.LUMO_TERTIARY
            );

            dialog.getFooter().add(
                    closeButton
            );

            dialog.setWidth(
                    "min(30rem, 90vw)"
            );

            dialog.open();
        });

        return detailsButton;
    }


    // =============================================================
    // Erledigt Button
    // =============================================================

    private Button createDoneButton() {

        Button markAsDoneButton =
                new Button(
                        "Erledigt!",
                        new Icon(VaadinIcon.CHECK)
                );

        markAsDoneButton.addClassName(
                "famigo-task-done-button"
        );

        markAsDoneButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SMALL
        );

        return markAsDoneButton;
    }


    // =============================================================
    // Empty State
    // =============================================================

    private Component createEmptyState(
            String text
    ) {

        Div card =
                new Div();

        card.addClassName(
                "famigo-empty-card"
        );

        Span mascot =
                new Span("🎉");

        mascot.addClassName(
                "famigo-empty-mascot"
        );

        H3 title =
                new H3("Alles erledigt!");

        title.addClassName(
                "famigo-empty-title"
        );

        Paragraph hint =
                new Paragraph(text);

        hint.addClassName(
                "famigo-empty-text"
        );

        card.add(
                mascot,
                title,
                hint
        );

        return card;
    }


    // =============================================================
    // Aufgaben aktualisieren
    // =============================================================

    private void refreshTasks() {

        standardTasksLayout.removeAll();
        specialTasksLayout.removeAll();

        User user =
                userService.getCurrentUser();

        // ---------------------------------------------------------
        // Extra-Aufgaben
        // ---------------------------------------------------------

        List<Task> specialTasks =
                taskService.findStillValidTasksAndStatusPending(
                        user.getId()
                );

        if (!specialTasks.isEmpty()) {

            specialTasks.reversed()
                    .forEach(
                            specialTask ->
                                    specialTasksLayout.add(
                                            createSpecialTaskCard(
                                                    specialTask
                                            )
                                    )
                    );

        } else {

            specialTasksLayout.add(
                    createEmptyState(
                            "Gerade wartet keine Extra-Aufgabe auf dich."
                    )
            );
        }

        // ---------------------------------------------------------
        // Standardaufgaben
        // ---------------------------------------------------------

        List<TaskTemplate> standardTasks =
                taskTemplateService.findAll();

        if (!standardTasks.isEmpty()) {

            standardTasks.reversed()
                    .forEach(
                            standardTask ->
                                    standardTasksLayout.add(
                                            createStandardTaskCard(
                                                    standardTask
                                            )
                                    )
                    );

        } else {

            standardTasksLayout.add(
                    createEmptyState(
                            "Hier gibt es momentan keine Standardaufgaben."
                    )
            );
        }
    }
}

