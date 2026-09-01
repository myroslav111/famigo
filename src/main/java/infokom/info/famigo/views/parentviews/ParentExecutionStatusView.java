package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.List;
import java.util.Optional;

@Route(value = "rewards", layout = MainViewLayout.class)
@PageTitle("Reward")
public class ParentExecutionStatusView extends VerticalLayout {

    private final UserService userService;
    private final RewardService rewardService;
    private final TaskService taskService;

    private ComboBox<User> childSelect;

    private VerticalLayout taskDoneLayout;
    private VerticalLayout taskPendingLayout;
    private VerticalLayout taskApprovedLayout;

    private TabSheet tabSheet;

    public ParentExecutionStatusView(
            UserService userService,
            RewardService rewardService,
            TaskService taskService
    ) {
        this.userService = userService;
        this.rewardService = rewardService;
        this.taskService = taskService;

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

        childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
        childSelect.setWidthFull();

        List<User> children = userService.findChildrenOfCurrentParent();
        childSelect.setItems(children);

        childSelect.addValueChangeListener(event -> refreshRewards());

        // =========================================================
        // TabSheet
        // =========================================================

        tabSheet = new TabSheet();
        tabSheet.addClassName("famigo-tabsheet");

        tabSheet.setWidthFull();
        tabSheet.setHeight("100%");

        tabSheet.getStyle()
                .set("min-height", "0")
                .set("overflow", "hidden");

        // =========================================================
        // Pending
        // =========================================================

        taskPendingLayout = createScrollableLayout();

        // =========================================================
        // Done
        // =========================================================

        taskDoneLayout = createScrollableLayout();

        // =========================================================
        // Approved
        // =========================================================

        taskApprovedLayout = createScrollableLayout();

        // =========================================================
        // Tabs
        // =========================================================

        tabSheet.add("Pending", taskPendingLayout);
        tabSheet.add("Done", taskDoneLayout);
        tabSheet.add("Approved", taskApprovedLayout);

        // =========================================================
        // View zusammensetzen
        // =========================================================

        add(childSelect, tabSheet);

        // TabSheet bekommt den gesamten restlichen Platz
        expand(tabSheet);

        // =========================================================
        // Erstes Kind automatisch auswählen
        // =========================================================

        if (!children.isEmpty()) {
            childSelect.setValue(children.getFirst());
        }
    }

    /**
     * Erstellt ein Layout, dessen Inhalt vertikal scrollbar ist.
     * Die Tabs selbst bleiben dadurch an ihrer Position.
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
                .setPaddingBottom("70px");

        return layout;
    }

    private Component createRewardCard(Reward reward) {

        Div card = new Div();
        card.addClassName("famigo-task-card");

        card.getStyle()
                .set("width", "100%")
                .set("max-width", "500px")
                .set("box-sizing", "border-box")
                .setPaddingBottom("70px");

        Div body = new Div();
        body.addClassName("famigo-task-body");

        // =========================================================
        // Titel
        // =========================================================

        H3 title = new H3(reward.getTitle());
        title.addClassName("famigo-task-title");

        body.add(title);

        // =========================================================
        // Beschreibung
        // =========================================================

        if (reward.getDescription() != null
                && !reward.getDescription().isBlank()) {

            Paragraph description =
                    new Paragraph(reward.getDescription());

            description.addClassName("famigo-task-desc");

            body.add(description);
        }

        // =========================================================
        // Kosten
        // =========================================================

        Span costChip = new Span(
                "Kosten: " + reward.getStarCost() + " ⭐"
        );

        costChip.addClassName("famigo-task-chip");

        body.add(costChip);

        // =========================================================
        // Status
        // =========================================================

        Span statusChip = new Span(
                "Status: " + getStatusTask(reward)
        );

        statusChip.addClassName("famigo-task-chip");

        if (reward.getTask().getStatus().equals(TaskStatus.DONE)) {
            statusChip.addClassName("famigo-task-chip-urgent");
        }

        body.add(statusChip);

        // =========================================================
        // Herkunft der Aufgabe
        // =========================================================

        Span sourceChip = new Span(
                reward.getTask().getTemplate() == null
                        ? "Individuelle Aufgabe"
                        : "Standardaufgabe"
        );

        sourceChip.addClassName("famigo-task-chip");

        body.add(sourceChip);

        // =========================================================
        // Aktionen
        // =========================================================

        Div actions = new Div();
        actions.addClassName("famigo-task-actions");

        if (reward.getTask().getStatus().equals(TaskStatus.DONE)) {

            // -----------------------------------------------------
            // Als eingelöst markieren
            // -----------------------------------------------------

            Button markRedeemed = new Button(
                    "Als eingelöst markieren",
                    new Icon(VaadinIcon.CHECK)
            );

            markRedeemed.addClassName("famigo-task-done-button");

            markRedeemed.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SMALL
            );

            markRedeemed.addClickListener(e -> {

                Optional<Task> task =
                        taskService.findById(
                                reward.getTask().getId()
                        );

                if (task.isEmpty()) {
                    Notification.show(
                            "Aufgabe wurde nicht gefunden."
                    );
                    return;
                }

                task.get().setStatus(TaskStatus.APPROVED);
                taskService.updateTask(task.get());

                reward.setRedeemed(true);
                rewardService.save(reward);

                Notification.show(
                        "Belohnung als eingelöst markiert"
                );

                User currentChild = reward.getChild();

                currentChild.setStars(
                        currentChild.getStars()
                                + reward.getStarCost()
                );

                userService.updateUser(currentChild);

                refreshRewards();
            });

            // -----------------------------------------------------
            // Ablehnen
            // -----------------------------------------------------

            Button markAsUndone = new Button(
                    "Ablehnen",
                    new Icon(VaadinIcon.CLOSE)
            );

            markAsUndone.addThemeVariants(
                    ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_SMALL
            );

            markAsUndone.addClickListener(e -> {

                if (reward.getTask().getTemplate() != null) {

                    rewardService.delete(reward.getId());

                    taskService.deleteById(
                            reward.getTask().getId()
                    );

                    refreshRewards();

                    return;
                }

                reward.setRedeemed(false);

                Optional<Task> task =
                        taskService.findById(
                                reward.getTask().getId()
                        );

                if (task.isPresent()) {

                    task.get().setStatus(TaskStatus.PENDING);

                    taskService.updateTask(task.get());
                }

                refreshRewards();
            });

            actions.add(
                    markRedeemed,
                    markAsUndone
            );
        }

        body.add(actions);

        // =========================================================
        // Sterne-Badge
        // =========================================================

        card.add(
                createStarsBadge(reward.getStarCost()),
                body
        );

        return card;
    }

    private Component createStarsBadge(int stars) {

        Span count =
                new Span(String.valueOf(stars));

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

    private String getStatusTask(Reward reward) {

        return switch (reward.getTask().getStatus()) {

            case PENDING ->
                    "⏳ noch nicht abgenommen";

            case DONE ->
                    "✅ ist erledigt";

            case APPROVED ->
                    "ist erledigt und angenommen";

            default ->
                    "etwas stimmt nicht";
        };
    }

    private void refreshRewards() {

        taskDoneLayout.removeAll();
        taskPendingLayout.removeAll();
        taskApprovedLayout.removeAll();

        User selectedChild =
                childSelect.getValue();

        if (selectedChild == null) {
            return;
        }

        List<Reward> rewards =
                rewardService.findByChildId(
                        selectedChild.getId()
                );

        rewards.reversed().forEach(reward -> {

            if (reward.getTask()
                    .getStatus()
                    .equals(TaskStatus.DONE)) {

                taskDoneLayout.add(
                        createRewardCard(reward)
                );

            } else if (reward.getTask()
                    .getStatus()
                    .equals(TaskStatus.PENDING)) {

                taskPendingLayout.add(
                        createRewardCard(reward)
                );

            } else {

                taskApprovedLayout.add(
                        createRewardCard(reward)
                );
            }
        });
    }
}


