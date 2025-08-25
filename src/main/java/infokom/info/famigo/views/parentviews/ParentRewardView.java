package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.combobox.ComboBox;
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
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.List;
import java.util.Optional;

@Route(value = "rewards",  layout = MainViewLayout.class)
@PageTitle("Reward")
public class ParentRewardView extends VerticalLayout {

    private final UserService userService;
    private final RewardService rewardService;
    private final TaskService taskService;

    private ComboBox<User> childSelect;
    private VerticalLayout taskDoneLayout;
    private VerticalLayout taskPendingLayout;
    private VerticalLayout taskApprovedLayout;
    private TabSheet tabSheet;

    public ParentRewardView(UserService userService, RewardService rewardService,  TaskService taskService) {
        this.userService = userService;
        this.rewardService = rewardService;
        this.taskService = taskService;

        tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        tabSheet.getStyle().set("overflow", "auto");

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
        childSelect.setItems(userService.findAllChildren());
        childSelect.addValueChangeListener(event -> refreshRewards());

        taskDoneLayout = new VerticalLayout();
        taskDoneLayout.setSpacing(true);
        taskDoneLayout.setWidthFull();
        taskDoneLayout.setHeightFull();
        taskDoneLayout.getStyle().set("overflow", "auto");

        taskPendingLayout = new VerticalLayout();
        taskPendingLayout.setSpacing(true);
        taskPendingLayout.setWidthFull();
        taskPendingLayout.setHeightFull();
        taskPendingLayout.getStyle().set("overflow", "auto");

        taskApprovedLayout = new VerticalLayout();
        taskApprovedLayout.setSpacing(true);
        taskApprovedLayout.setWidthFull();
        taskApprovedLayout.setHeightFull();
        taskApprovedLayout.getStyle().set("overflow", "auto");

        tabSheet.add("Pending", taskPendingLayout);
        tabSheet.add("Done", taskDoneLayout);
        tabSheet.add("Approved", taskApprovedLayout);


        add(childSelect, tabSheet);
    }


    private Component createRewardCard(Reward reward) {
        Card card = new Card();
        card.setWidthFull();

        VerticalLayout layout = new VerticalLayout(
                new H5(reward.getTitle()),
                new Span("Kosten " + reward.getStarCost() + " ⭐"),
                new Paragraph(reward.getDescription()),
//                new Span("Status: " + (reward.getTask().getStatus().equals(TaskStatus.PENDING) ? "⏳ Offen" : "✅ Eingelöst"))
                new Span("Status: " + (getStatusTask(reward)))
        );

        if (reward.getTask().getStatus().equals(TaskStatus.DONE)) {
            Button markRedeemed = new Button("Als eingelöst markieren", e -> {
                Optional<Task> task = taskService.findById(reward.getTask().getId());
                task.get().setStatus(TaskStatus.APPROVED);
                taskService.updateTask(task.get());
                reward.setRedeemed(true);
                rewardService.save(reward);
                Notification.show("Belohnung als eingelöst markiert");
                User currentChild = reward.getChild();
                currentChild.setStars(currentChild.getStars() + reward.getStarCost());
                userService.updateUser(currentChild);
                refreshRewards();
            });

            layout.add(markRedeemed);
        }

        card.add(layout);
        return card;
    }

    private String getStatusTask(Reward reward) {
        String status = switch (reward.getTask().getStatus()) {
            case PENDING -> "⏳ noch nicht abgenommen";
            case DONE -> "✅ ist erledigt";
            case APPROVED -> "ist erledigt und angenommen";
            default -> "etwas nicht stimmt";
        };

        return status;
    }

    private void refreshRewards() {
        taskDoneLayout.removeAll();
        taskPendingLayout.removeAll();

        User selectedChild = childSelect.getValue();
        if(selectedChild == null) return;

        List<Reward> rewards = rewardService.findByChildId(selectedChild.getId());

        rewards.forEach(reward -> {
            if(reward.getTask().getStatus().equals(TaskStatus.DONE)) {
                taskDoneLayout.add(createRewardCard(reward));
            }else if(reward.getTask().getStatus().equals(TaskStatus.PENDING)) {
                taskPendingLayout.add(createRewardCard(reward));
            }else{
                taskApprovedLayout.add(createRewardCard(reward));
            }
        });
    }
}
