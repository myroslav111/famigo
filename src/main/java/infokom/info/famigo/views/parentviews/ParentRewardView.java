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
import infokom.info.famigo.entity.User;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.List;

@Route(value = "rewards",  layout = MainViewLayout.class)
@PageTitle("Reward")
public class ParentRewardView extends VerticalLayout {

    private final UserService userService;
    private final RewardService rewardService;
    private final TaskService taskService;

    private ComboBox<User> childSelect;
    private VerticalLayout rewardLayout;
    private VerticalLayout taskDoneLayout;
    private VerticalLayout taskPendingLayout;
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

        rewardLayout = new VerticalLayout();
        rewardLayout.setSpacing(true);
        rewardLayout.setWidthFull();
        rewardLayout.setHeightFull();
        rewardLayout.getStyle().set("overflow", "auto");

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

        tabSheet.add("Erledigte", taskDoneLayout);
        tabSheet.add("Unerledigte", taskPendingLayout);

        add(childSelect, tabSheet);
    }


    private Component createRewardCard(Reward reward) {
        Card card = new Card();
        card.setWidthFull();


        VerticalLayout layout = new VerticalLayout(
                new H5(reward.getTitle()),
                new Span("Kosten " + reward.getStarCost() + " ⭐"),
                new Paragraph(reward.getDescription()),
                new Span("Status: " + ((reward.isRedeemed()) ? "✅ Eingelöst" : "⏳ Offen"))
        );

        if (!reward.isRedeemed()) {
            Button markRedeemed = new Button("Als eingelöst markieren", e -> {
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

    private void refreshRewards() {
        taskDoneLayout.removeAll();
        taskPendingLayout.removeAll();

        User selectedChild = childSelect.getValue();
        if(selectedChild == null) return;

        List<Reward> rewards = rewardService.findByChildId(selectedChild.getId());

        rewards.forEach(reward -> {
            if(reward.isRedeemed()) {
                taskDoneLayout.add(createRewardCard(reward));
            }else {
                taskPendingLayout.add(createRewardCard(reward));
            }
        });
    }
}
