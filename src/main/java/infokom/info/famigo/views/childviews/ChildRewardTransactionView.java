package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.components.RewardCards;
import infokom.info.famigo.views.components.StarExchangeDialog;

import java.util.Arrays;
import java.util.List;

@Route(value = "child/stars-exchange", layout = MainViewLayout.class)
@PageTitle("Sterneumtauschen")
public class ChildRewardTransactionView extends VerticalLayout {
    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final UserService userService;

    private Div rewardsLayout;

    private ChildRewardTransactionView(RewardOptionService rewardOptionService, ChildRewardTransactionService childRewardTransactionService, UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.userService = userService;

        addClassName("famigo-page");
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        H2 pageTitle = new H2("Sterne eintauschen ⭐");
        pageTitle.addClassName("famigo-page-title");

        Paragraph pageSubtitle = new Paragraph("Such dir eine Kategorie aus und tausche deine Sterne gegen eine Belohnung.");
        pageSubtitle.addClassName("famigo-page-subtitle");

        rewardsLayout = new Div();
        rewardsLayout.addClassName("famigo-task-grid");

        refreshRewards();

        add(pageTitle, pageSubtitle, rewardsLayout);
    }

    public void refreshRewards() {
        rewardsLayout.removeAll();

        Arrays.stream(RewardCategory.values())
                .forEach(rewardCategory -> rewardsLayout.add(createRewardChildCard(rewardCategory)));
    }

    public Div createRewardChildCard(RewardCategory rewardCategory) {
        Button exchangeButton = new Button("Einlösen", new Icon(VaadinIcon.GIFT));
        exchangeButton.addClassName("famigo-task-done-button");
        exchangeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        exchangeButton.addClickListener(event -> {
            StarExchangeDialog starExchangeDialog = new StarExchangeDialog(rewardOptionService,
                    childRewardTransactionService,
                    rewardCategory.toString(),
                    userService);
            starExchangeDialog.open();
        });

        return RewardCards.categoryCard(rewardCategory, describeCategory(rewardCategory), exchangeButton);
    }

    /** Chip-Text: wie viele Belohnungen es hier gibt und ab wie vielen Sternen. */
    private String describeCategory(RewardCategory rewardCategory) {
        if (rewardCategory == RewardCategory.SELBSTWUNSCH) {
            return "Dein eigener Wunsch";
        }

        List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(rewardCategory)
                .stream()
                .filter(RewardOption::isActive)
                .toList();

        if (rewardOptions.isEmpty()) {
            return "Noch nichts zum Eintauschen";
        }

        int cheapest = rewardOptions.stream().mapToInt(RewardOption::getCost).min().orElse(0);
        return rewardOptions.size() + " Belohnungen · ab " + cheapest + " ⭐";
    }
}
