package infokom.info.famigo.views.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

public class StarExchangeDialog extends Dialog {
    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final String category;
    private final UserService userService;

    int countOfStars;

    public StarExchangeDialog(RewardOptionService rewardOptionService,
                              ChildRewardTransactionService childRewardTransactionService,
                              String category,
                              UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.category = category;
        this.userService = userService;

        VerticalLayout starExchangeLayout = new VerticalLayout();
        starExchangeLayout.setSizeFull();

        User child = userService.getCurrentUser();
        countOfStars = child.getStars();



        add(new H1("Dein Sternzustand ist: " + countOfStars), radioButtonSet(child));
    }

    private Div radioButtonSet(User child) {
        Div div = new Div();
        div.setSizeFull();
        div.setWidthFull();

        RadioButtonGroup<RewardOption> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioButtonGroup.setLabel("Art von den Belohnungen");

        List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(category);
        radioButtonGroup.setItems(rewardOptions);
        radioButtonGroup.setValue(rewardOptions.get(0));
        radioButtonGroup.setRenderer(new ComponentRenderer<>(rewardOption -> {
            H3 title = new H3(rewardOption.getTitle());
            Span cost = new Span("⭐: " + rewardOption.getCost());
            Text description = new Text(rewardOption.getDescription());

            return new Div(new VerticalLayout(title, cost, description));

        }));

        div.add(new VerticalLayout(radioButtonGroup,
                new Button("Umtauschen", event -> {
                           executeExchangeStars(radioButtonGroup.getValue(), child);
                    close();
                    Notification.show("Du hast " + radioButtonGroup.getValue().getCost() + " ⭐ ausgegeben!");
                })));
        return div;
    }

    public void executeExchangeStars(RewardOption rewardOption, User child){
        if (child.getStars() < rewardOption.getCost()) {
            Notification.show("Dir fählt noch " + (rewardOption.getCost() - child.getStars()) + " ⭐ !");
            return;
        }
        countOfStars = child.getStars() - rewardOption.getCost();
        userService.updateUserStar(countOfStars);

        ChildRewardTransaction  childRewardTransaction = new ChildRewardTransaction();
        childRewardTransaction.setRedeemedAt(LocalDateTime.now());
        childRewardTransaction.setStarsSpent(rewardOption.getCost());
        childRewardTransaction.setChild(child);
        childRewardTransaction.setReward(rewardOption);

        childRewardTransactionService.save(childRewardTransaction);


    }


}
