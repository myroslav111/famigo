package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.TaskTemplateService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.components.UiActions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "tasks", layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ParentTaskView extends VerticalLayout {

    private final UserService userService;
    private final TaskService taskService;
    private final TaskTemplateService taskTemplateService;

    private ComboBox<User> childrenSelector;

    private VerticalLayout standardTaskLayout;
    private VerticalLayout specialTaskLayout;

    private TabSheet tabSheet;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public ParentTaskView(
            UserService userService,
            TaskService taskService,
            TaskTemplateService taskTemplateService
    ) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskTemplateService = taskTemplateService;

        // =========================================================
        // Hauptlayout
        // =========================================================

        setSpacing(true);
        setPadding(true);

        setWidthFull();
        setHeightFull();

        getStyle()
                .set("box-sizing", "border-box")
                .set("overflow", "hidden");

        // =========================================================
        // Kind auswählen
        // =========================================================

        childrenSelector = new ComboBox<>("Kind auswählen");

        childrenSelector.setItemLabelGenerator(User::getName);
        childrenSelector.setWidthFull();

        List<User> children =
                userService.findChildrenOfCurrentParent();

        childrenSelector.setItems(children);

        childrenSelector.addValueChangeListener(
                event -> refreshTasks()
        );

        // =========================================================
        // Scrollbarer Content
        // =========================================================

        specialTaskLayout = createScrollableLayout();
        standardTaskLayout = createScrollableLayout();

        // =========================================================
        // TabSheet
        // =========================================================

        tabSheet = new TabSheet();

        tabSheet.addClassName("famigo-tabsheet");

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
                "Specialaufgaben",
                specialTaskLayout
        );

        tabSheet.add(
                "Standardaufgaben",
                standardTaskLayout
        );

        // =========================================================
        // Layout zusammensetzen
        // =========================================================

        add(
                childrenSelector,
                tabSheet
        );

        // TabSheet nimmt den restlichen Platz ein
        expand(tabSheet);

        // =========================================================
        // Erstes Kind automatisch auswählen
        // =========================================================

        if (!children.isEmpty()) {
            childrenSelector.setValue(
                    children.getFirst()
            );
        }
    }


    /**
     * Erstellt einen Tab-Inhalt,
     * der unabhängig vom TabSheet scrollbar ist.
     */
    private VerticalLayout createScrollableLayout() {

        VerticalLayout layout = new VerticalLayout();

        layout.setSpacing(true);
        layout.setPadding(true);

        layout.setWidthFull();
        layout.setHeightFull();

        layout.getStyle()
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("min-height", "0")
                .set("box-sizing", "border-box")
                .set("padding-bottom", "70px");

        return layout;
    }


    public void refreshTasks() {

        specialTaskLayout.removeAll();
        standardTaskLayout.removeAll();

        User selectedChild =
                childrenSelector.getValue();

        if (selectedChild == null) {
            return;
        }

        // =========================================================
        // Specialaufgaben
        // =========================================================

        List<Task> tasks =
                taskService.findByAssignedToSortedByDueDate(
                        selectedChild.getId()
                );

        if (!tasks.isEmpty()) {

            tasks.reversed().forEach(task ->
                    specialTaskLayout.add(
                            createTaskCard(task, true)
                    )
            );
        }

        // =========================================================
        // Standardaufgaben
        // =========================================================

        List<TaskTemplate> templates =
                taskTemplateService.findAll();

        if (!templates.isEmpty()) {

            templates.reversed().forEach(template ->
                    standardTaskLayout.add(
                            createTemplateCard(template)
                    )
            );
        }
    }


    private Component createTaskCard(
            Task task,
            boolean flags
    ) {

        Div card = new Div();

        card.addClassName(
                "famigo-task-card"
        );

        card.getStyle()
                .set("width", "100%")
                .set("max-width", "500px")
                .set("box-sizing", "border-box");

        Div body = new Div();

        body.addClassName(
                "famigo-task-body"
        );

        // =========================================================
        // Titel
        // =========================================================

        H3 title =
                new H3(task.getTitle());

        title.addClassName(
                "famigo-task-title"
        );

        body.add(title);

        // =========================================================
        // Beschreibung
        // =========================================================

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

        // =========================================================
        // Fälligkeitsdatum
        // =========================================================

        body.add(
                createDueDateChip(
                        task.getDueDate()
                )
        );

        // =========================================================
        // Status
        // =========================================================

        if (flags) {

            Span status =
                    new Span(
                            "Status: "
                                    + task.getStatus()
                    );

            status.addClassName(
                    "famigo-task-chip"
            );

            body.add(status);
        }

        // =========================================================
        // Details
        // =========================================================

        Button detailsButton =
                createDetailsButton(
                        task.getTitle(),
                        task.getDescription()
                );

        Div actions =
                new Div(detailsButton);

        actions.addClassName(
                "famigo-task-actions"
        );

        body.add(actions);

        // =========================================================
        // Card
        // =========================================================

        card.add(
                createStarsBadge(
                        task.getStarsReward()
                ),
                body
        );

        return card;
    }


    private Component createTemplateCard(
            TaskTemplate template
    ) {

        Div card = new Div();

        card.addClassName(
                "famigo-task-card"
        );

        card.getStyle()
                .set("width", "100%")
                .set("max-width", "500px")
                .set("box-sizing", "border-box");

        Div body = new Div();

        body.addClassName(
                "famigo-task-body"
        );

        // =========================================================
        // Titel
        // =========================================================

        H3 title =
                new H3(template.getTitle());

        title.addClassName(
                "famigo-task-title"
        );

        body.add(title);

        // =========================================================
        // Beschreibung
        // =========================================================

        if (template.getDescription() != null
                && !template.getDescription().isBlank()) {

            Paragraph description =
                    new Paragraph(
                            template.getDescription()
                    );

            description.addClassName(
                    "famigo-task-desc"
            );

            body.add(description);
        }

        // =========================================================
        // Details
        // =========================================================

        Button detailsButton =
                createDetailsButton(
                        template.getTitle(),
                        template.getDescription()
                );

        // =========================================================
        // Zuweisen
        // =========================================================

        Button assignedButton =
                new Button(
                        "Zuweisen",
                        new Icon(VaadinIcon.PLUS)
                );

        assignedButton.addClassName(
                "famigo-task-done-button"
        );

        assignedButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SMALL
        );

        assignedButton.addClickListener(e -> {

            User selectedChild =
                    childrenSelector.getValue();

            if (selectedChild == null) {

                Notification.show(
                        "Bitte zuerst ein Kind auswählen."
                );

                return;
            }

            boolean assigned = UiActions.run(() -> taskService.assign(
                    selectedChild.getId(),
                    template.getTitle(),
                    template.getDescription(),
                    template.getStarsReward(),
                    LocalDate.now(),
                    template.getId()));
            if (!assigned) {
                return;
            }

            Notification.show(
                    "Aufgabe wurde zugewiesen."
            );

            refreshTasks();
        });

        // =========================================================
        // Aktionen
        // =========================================================

        Div actions =
                new Div(
                        detailsButton,
                        assignedButton
                );

        actions.addClassName(
                "famigo-task-actions"
        );

        body.add(actions);

        // =========================================================
        // Card
        // =========================================================

        card.add(
                createStarsBadge(
                        template.getStarsReward()
                ),
                body
        );

        return card;
    }


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
}

