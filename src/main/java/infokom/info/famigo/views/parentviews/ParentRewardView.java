package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.Comparator;
import java.util.List;

@Route(value = "rewards",  layout = MainViewLayout.class)
@PageTitle("Reward")
public class ParentRewardView extends VerticalLayout {

    private final UserService userService;
    private final RewardService rewardService;

    private ComboBox<User> childSelect;
    private VerticalLayout rewardLayout;

    public ParentRewardView(UserService userService, RewardService rewardService) {
        this.userService = userService;
        this.rewardService = rewardService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
        childSelect.setItems(userService.findAllChildren());
        childSelect.addValueChangeListener(event -> refreshRewards());

//        Button addRewardButton = new Button("Neue Belohnung hinzufügen", e -> openRewardDialog());

        rewardLayout = new VerticalLayout();
        rewardLayout.setSpacing(true);
        rewardLayout.setWidthFull();
        rewardLayout.setHeightFull();
        rewardLayout.getStyle().set("overflow", "auto");

//        add(childSelect, addRewardButton, rewardLayout);
        add(childSelect, rewardLayout);



    }

    private void openRewardDialog() {
       Dialog dialog = new Dialog();
       dialog.setWidth("90%");

        TextField title = new TextField("Titel");
        TextArea description = new TextArea("Beschreibung");
        NumberField starCost = new NumberField("Sternkosten");
        starCost.setMin(1);
        starCost.setStep(1);

        Button save = new Button("Speichern", e -> {
            if (childSelect.isEmpty() || title.isEmpty() || starCost.isEmpty()) {
                Notification.show("Bitte alle Felder ausfühllen");
                return;
            }

            Reward reward = new Reward();
            reward.setTitle(title.getValue());
            reward.setDescription(description.getValue());
            reward.setStarCost(starCost.getValue().intValue());
            reward.setChild(childSelect.getValue());
            reward.setCreatedBy(userService.getCurrentUser());

            rewardService.save(reward);
            dialog.close();
            refreshRewards();
        });

        dialog.add(new VerticalLayout(title, description, starCost, save));
        dialog.open();
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
        rewardLayout.removeAll();

        User selectedChild = childSelect.getValue();
        if(selectedChild == null) return;

        List<Reward> rewards = rewardService.findByChildId(selectedChild.getId());

        rewards.stream()
                .sorted(Comparator.comparing(Reward::isRedeemed))
                .forEach(reward -> rewardLayout.add(createRewardCard(reward)));

    }

}
