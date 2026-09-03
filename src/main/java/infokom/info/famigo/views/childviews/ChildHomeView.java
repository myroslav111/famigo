package infokom.info.famigo.views.childviews;

//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.card.Card;
//import com.vaadin.flow.component.dialog.Dialog;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.icon.Icon;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterObserver;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import infokom.info.famigo.entity.Reward;
//import infokom.info.famigo.entity.Task;
//import infokom.info.famigo.entity.User;
//import infokom.info.famigo.entity.enums.TaskStatus;
//import infokom.info.famigo.service.RewardService;
//import infokom.info.famigo.service.SessionService;
//import infokom.info.famigo.service.TaskService;
//import infokom.info.famigo.service.UserService;
//import infokom.info.famigo.views.MainViewLayout;
//import infokom.info.famigo.views.securityviews.LoginView;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Route(value = "child", layout =  MainViewLayout.class)
//@PageTitle("Childbereich")
//public class ChildHomeView extends VerticalLayout implements BeforeEnterObserver {
//    private final SessionService sessionService;
//    private final TaskService taskService;
//    private final RewardService rewardService;
//    private final UserService userService;
//
//    private static final DateTimeFormatter DATE_FORMAT =
//            DateTimeFormatter.ofPattern("dd.MM.yyyy");
//
//    private VerticalLayout currentTaskLayout;
//
////    public ChildHomeView(SessionService sessionService, TaskService taskService, RewardService rewardService, UserService userService) {
////        this.sessionService = sessionService;
////        this.taskService = taskService;
////        this.rewardService = rewardService;
////        this.userService = userService;
////
////        setSpacing(true);
////        setWidthFull();
////        setHeightFull();
////        setPadding(true);
////
////        User child = sessionService.getCurrentUser();
////        if(child == null) {
////            add(new H1("Kein benutzer eingeloggt"));
////            return;
////        }
////
////        currentTaskLayout = new VerticalLayout();
////        currentTaskLayout.setSpacing(true);
////        currentTaskLayout.setWidthFull();
////        currentTaskLayout.setHeightFull();
////        currentTaskLayout.getStyle().set("overflow", "auto");
////
////        refreshTasks();
////
////        add(new H2("Willkommen " + child.getUsername() + "!"));
////        add(new Paragraph("Du hast aktuell ⭐" + child.getStars() + " gesammelt!"));
////
////        add(new H4("Aktuell zur Erledigung "));
////        add(currentTaskLayout);
////    }
//
//    public ChildHomeView(
//            SessionService sessionService,
//            TaskService taskService,
//            RewardService rewardService,
//            UserService userService
//    ) {
//        this.sessionService = sessionService;
//        this.taskService = taskService;
//        this.rewardService = rewardService;
//        this.userService = userService;
//
//        setSpacing(true);
//        setWidthFull();
//        setHeightFull();
//        setPadding(true);
//
//        User child = sessionService.getCurrentUser();
//
//        if (child == null) {
//            add(new H1("😕 Kein Benutzer eingeloggt"));
//            return;
//        }
//
//        currentTaskLayout = new VerticalLayout();
//        currentTaskLayout.setSpacing(true);
//        currentTaskLayout.setWidthFull();
//        currentTaskLayout.setHeightFull();
//        currentTaskLayout.getStyle().set("overflow", "auto");
//
//        // Begrüßung
//        H1 welcome = new H1("👋 Hallo " + child.getUsername() + "!");
//        welcome.addClassName("famigo-child-welcome");
//
//        Paragraph welcomeText = new Paragraph(
//                "Schön, dass du da bist! 🌟"
//        );
//        welcomeText.addClassName("famigo-child-welcome-text");
//
//        // Sterne
////        H3 starsTitle = new H3("⭐ Deine Sterne");
////        Paragraph starsText = new Paragraph(
////                "Du hast schon " + child.getStars() + " Sterne gesammelt!"
////        );
//
//        Span starsCount = new Span(String.valueOf(child.getStars()) + " ⭐");
//        starsCount.addClassName("famigo-child-stars-count");
//
//        Span starsIcon = new Span("⭐");
//        starsIcon.addClassName("famigo-child-stars-icon");
//
//        Paragraph starsText = new Paragraph();
//        starsText.add("Du hast schon ");
//        starsText.add(starsCount);
//        starsText.add(starsIcon);
//        starsText.add(" gesammelt! 🎉");
//
//        Div starsBox = new Div(starsIcon, starsText);
//        starsBox.addClassName("famigo-child-stars-box");
//
//        // Aufgaben
//        H2 tasksTitle = new H2("🎯 Deine Aufgaben für heute");
//        tasksTitle.addClassName("famigo-child-section-title");
//
//        refreshTasks();
//
//        add(
//                welcome,
//                welcomeText,
//                starsBox,
//                tasksTitle,
//                currentTaskLayout
//        );
//    }
//
//    private Component createTaskChildCard(Task task) {
//        Div card = new Div();
//        card.addClassName("famigo-task-card");
//
//        Div body = new Div();
//        body.addClassName("famigo-task-body");
//
//        H3 title = new H3(task.getTitle());
//        title.addClassName("famigo-task-title");
//        body.add(title);
//
//        if (task.getDescription() != null && !task.getDescription().isBlank()) {
//            Paragraph description = new Paragraph(task.getDescription());
//            description.addClassName("famigo-task-desc");
//            body.add(description);
//        }
//
//        body.add(createDueDateChip(task.getDueDate()));
//
//        Button detailsButton = createDetailsButton(
//                task.getTitle(),
//                task.getDescription()
//        );
//
//        Button markAsDoneButton = createDoneButton();
//        markAsDoneButton.addClickListener(e -> {
//            task.setStatus(TaskStatus.DONE);
//            taskService.updateTask(task);
//
//            refreshTasks();
//        });
//
//        Div actions = new Div(detailsButton, markAsDoneButton);
//        actions.addClassName("famigo-task-actions");
//        body.add(actions);
//
//        card.add(createStarsBadge(task.getStarsReward()), body);
//
//        return card;
//    }
//
//    private Component createStarsBadge(int stars) {
//        Span count = new Span(String.valueOf(stars));
//        count.addClassName("famigo-task-stars-count");
//
//        Div badge = new Div(new Span("⭐"), count);
//        badge.addClassName("famigo-task-stars");
//        badge.getElement().setAttribute("title", stars + " Sterne");
//
//        return badge;
//    }
//
//    private Component createDueDateChip(LocalDate dueDate) {
//        Span chip = new Span();
//        chip.addClassName("famigo-task-chip");
//
//        if (dueDate == null) {
//            chip.setText("Ohne Frist");
//        } else {
//            chip.setText("Fällig bis " + dueDate.format(DATE_FORMAT));
//
//            if (!dueDate.isAfter(LocalDate.now())) {
//                chip.addClassName("famigo-task-chip-urgent");
//            }
//        }
//
//        return chip;
//    }
//
//    private Button createDetailsButton(String title, String description) {
//        Button detailsButton = new Button("Details");
//        detailsButton.addClassName("famigo-task-details-button");
//        detailsButton.addThemeVariants(
//                ButtonVariant.LUMO_TERTIARY,
//                ButtonVariant.LUMO_SMALL
//        );
//
//        detailsButton.addClickListener(e -> {
//            Dialog dialog = new Dialog();
//            dialog.setHeaderTitle(title);
//
//            dialog.add(new Paragraph(
//                    description == null || description.isBlank()
//                            ? "Zu dieser Aufgabe gibt es keine weitere Beschreibung."
//                            : description
//            ));
//
//            Button closeButton = new Button(
//                    "Schließen",
//                    event -> dialog.close()
//            );
//            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//            dialog.getFooter().add(closeButton);
//
//            dialog.setWidth("min(30rem, 90vw)");
//            dialog.open();
//        });
//
//        return detailsButton;
//    }
//
////    private Button createDoneButton() {
////        Button markAsDoneButton = new Button(
////                "Erledigt!",
////                new Icon(VaadinIcon.CHECK)
////        );
////
////        markAsDoneButton.addClassName("famigo-task-done-button");
////        markAsDoneButton.addThemeVariants(
////                ButtonVariant.LUMO_PRIMARY,
////                ButtonVariant.LUMO_SMALL
////        );
////
////        return markAsDoneButton;
////    }
//
//    private Button createDoneButton() {
//        Button markAsDoneButton = new Button(
//                "Geschafft! 🎉",
//                new Icon(VaadinIcon.CHECK)
//        );
//
//        markAsDoneButton.addClassName("famigo-task-done-button");
//        markAsDoneButton.addThemeVariants(
//                ButtonVariant.LUMO_PRIMARY,
//                ButtonVariant.LUMO_SMALL
//        );
//
//        return markAsDoneButton;
//    }
//
////    private void refreshTasks() {
////        currentTaskLayout.removeAll();
////
////        User child = sessionService.getCurrentUser();
////        List<Task> currentTask = taskService.findTasksDueTodayForChild(child.getId());
////
////        if(!currentTask.isEmpty()) {
////            currentTask.forEach(task -> currentTaskLayout.add(createTaskChildCard(task)));
////        }else {
////            currentTaskLayout.add(new Span("Aktuell ist keine individuelle Aufgaben zur Erledigung \uD83C\uDF89"));
////        }
////    }
//
//    private void refreshTasks() {
//        currentTaskLayout.removeAll();
//
//        User child = sessionService.getCurrentUser();
//
//        List<Task> currentTask =
//                taskService.findTasksDueTodayForChild(child.getId());
//
//        if (!currentTask.isEmpty()) {
//
//            currentTask.forEach(task ->
//                    currentTaskLayout.add(createTaskChildCard(task))
//            );
//
//        } else {
//
//            H3 noTasksTitle = new H3("🎉 Alles geschafft!");
//
//            Paragraph noTasksText = new Paragraph(
//                    "Heute gibt es keine Aufgaben mehr. " +
//                            "Du kannst dich entspannen! ⭐"
//            );
//
//            Div emptyState = new Div(
//                    noTasksTitle,
//                    noTasksText
//            );
//
//            emptyState.addClassName("famigo-child-empty-state");
//
//            currentTaskLayout.add(emptyState);
//        }
//    }
//
//    public void beforeEnter(BeforeEnterEvent event) {
//        if(!sessionService.isLoggedIn()){
//            event.forwardTo(LoginView.class);
//        }
//    }
//}


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.securityviews.LoginView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "child", layout = MainViewLayout.class)
@PageTitle("Childbereich")
public class ChildHomeView
        extends VerticalLayout
        implements BeforeEnterObserver {

    private final SessionService sessionService;
    private final TaskService taskService;
    private final RewardService rewardService;
    private final UserService userService;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private VerticalLayout currentTaskLayout;


    public ChildHomeView(
            SessionService sessionService,
            TaskService taskService,
            RewardService rewardService,
            UserService userService
    ) {

        this.sessionService = sessionService;
        this.taskService = taskService;
        this.rewardService = rewardService;
        this.userService = userService;

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
                .set("overflow", "hidden")
                .set("padding-bottom", "70px");

        // =========================================================
        // Benutzer prüfen
        // =========================================================

        User child =
                sessionService.getCurrentUser();

        if (child == null) {

            add(
                    new H1("😕 Kein Benutzer eingeloggt")
            );

            return;
        }

        // =========================================================
        // Begrüßung
        // =========================================================

        H1 welcome =
                new H1(
                        "👋 Hallo "
                                + child.getUsername()
                                + "!"
                );

        welcome.addClassName(
                "famigo-child-welcome"
        );

        Paragraph welcomeText =
                new Paragraph(
                        "Schön, dass du da bist! 🌟"
                );

        welcomeText.addClassName(
                "famigo-child-welcome-text"
        );

        // =========================================================
        // Sterne
        // =========================================================

        Span starsCount =
                new Span(
                        String.valueOf(
                                child.getStars()
                        )
                                + " ⭐"
                );

        starsCount.addClassName(
                "famigo-child-stars-count"
        );

        Span starsIcon =
                new Span("⭐");

        starsIcon.addClassName(
                "famigo-child-stars-icon"
        );

        Paragraph starsText =
                new Paragraph();

        starsText.add(
                "Du hast schon "
        );

        starsText.add(
                starsCount
        );

        starsText.add(
                starsIcon
        );

        starsText.add(
                " gesammelt! 🎉"
        );

        Div starsBox =
                new Div(
                        starsIcon,
                        starsText
                );

        starsBox.addClassName(
                "famigo-child-stars-box"
        );

        // =========================================================
        // Aufgaben Überschrift
        // =========================================================

        H2 tasksTitle =
                new H2(
                        "🎯 Deine Aufgaben für heute"
                );

        tasksTitle.addClassName(
                "famigo-child-section-title"
        );

        // =========================================================
        // Scrollbarer Aufgabenbereich
        // =========================================================

        currentTaskLayout =
                createScrollableTaskLayout();

        // Aufgaben laden
        refreshTasks();

        // =========================================================
        // Alles zusammensetzen
        // =========================================================

        add(
                welcome,
                welcomeText,
                starsBox,
                tasksTitle,
                currentTaskLayout
        );

        // Aufgabenbereich nimmt den restlichen Platz ein
        expand(currentTaskLayout);
    }


    /**
     * Erstellt den scrollbaren Bereich für die aktuellen Aufgaben.
     *
     * Der Header bleibt dadurch an seiner Position,
     * während nur die Aufgabenliste scrollt.
     */
    private VerticalLayout createScrollableTaskLayout() {

        VerticalLayout layout =
                new VerticalLayout();

        layout.setSpacing(true);
        layout.setPadding(true);

        layout.setWidthFull();
        layout.setHeight("100%");

        layout.getStyle()
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("min-height", "0")
                .set("box-sizing", "border-box")
                .set("padding-bottom", "70px")
                .setAlignItems(Style.AlignItems.CENTER);

        return layout;
    }


    // =============================================================
    // Aufgabenkarte
    // =============================================================

    private Component createTaskChildCard(
            Task task
    ) {

        Div card =
                new Div();

        card.addClassName(
                "famigo-task-card"
        );

        Div body =
                new Div();

        body.addClassName(
                "famigo-task-body"
        );

        // ---------------------------------------------------------
        // Titel
        // ---------------------------------------------------------

        H3 title =
                new H3(
                        task.getTitle()
                );

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
    // Sterne-Badge
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
                            + dueDate.format(
                            DATE_FORMAT
                    )
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
                new Button(
                        "Details"
                );

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
                        "Geschafft! 🎉",
                        new Icon(
                                VaadinIcon.CHECK
                        )
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
    // Aufgaben aktualisieren
    // =============================================================

    private void refreshTasks() {

        currentTaskLayout.removeAll();

        User child =
                sessionService.getCurrentUser();

        if (child == null) {
            return;
        }

        List<Task> currentTask =
                taskService.findTasksDueTodayForChild(
                        child.getId()
                );

        if (!currentTask.isEmpty()) {

            currentTask.forEach(
                    task ->
                            currentTaskLayout.add(
                                    createTaskChildCard(
                                            task
                                    )
                            )
            );

        } else {

            H3 noTasksTitle =
                    new H3(
                            "🎉 Alles geschafft!"
                    );

            Paragraph noTasksText =
                    new Paragraph(
                            "Heute gibt es keine Aufgaben mehr. "
                                    + "Du kannst dich entspannen! ⭐"
                    );

            Div emptyState =
                    new Div(
                            noTasksTitle,
                            noTasksText
                    );

            emptyState.addClassName(
                    "famigo-child-empty-state"
            );

            currentTaskLayout.add(
                    emptyState
            );
        }
    }


    // =============================================================
    // Login-Prüfung
    // =============================================================

    @Override
    public void beforeEnter(
            BeforeEnterEvent event
    ) {

        if (!sessionService.isLoggedIn()) {

            event.forwardTo(
                    LoginView.class
            );
        }
    }
}

