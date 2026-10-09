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
import infokom.info.famigo.service.RewardService;
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
    private final RewardService rewardService;
    private final UserService userService;

    private Div rewardsLayout;


    private ChildRewardTransactionView(
            RewardOptionService rewardOptionService,
            RewardService rewardService,
            UserService userService
    ) {

        this.rewardOptionService = rewardOptionService;
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
                .set("overflow", "hidden");

        // =========================================================
        // Überschrift
        // =========================================================

        H2 pageTitle =
                new H2("Sterne eintauschen ⭐");

        pageTitle.addClassName(
                "famigo-page-title"
        );

        // =========================================================
        // Untertitel
        // =========================================================

        Paragraph pageSubtitle =
                new Paragraph(
                        "Such dir eine Kategorie aus und "
                                + "tausche deine Sterne gegen eine Belohnung."
                );

        pageSubtitle.addClassName(
                "famigo-page-subtitle"
        );

        // =========================================================
        // Scrollbarer Reward-Bereich
        // =========================================================

        rewardsLayout =
                new Div();

        rewardsLayout.addClassName(
                "famigo-task-grid"
        );

        rewardsLayout.getStyle()
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("min-height", "0")
                .set("box-sizing", "border-box")
                .set("padding-bottom", "70px");

        // =========================================================
        // Rewards laden
        // =========================================================

        refreshRewards();

        // =========================================================
        // Layout aufbauen
        // =========================================================

        add(
                pageTitle,
                pageSubtitle,
                rewardsLayout
        );

        // Reward-Bereich nimmt den restlichen Platz ein
        expand(rewardsLayout);
    }


    // =============================================================
    // Rewards aktualisieren
    // =============================================================

    public void refreshRewards() {

        rewardsLayout.removeAll();

        Arrays.stream(
                        RewardCategory.values()
                )
                .forEach(
                        rewardCategory ->
                                rewardsLayout.add(
                                        createRewardChildCard(
                                                rewardCategory
                                        )
                                )
                );
    }


    // =============================================================
    // Reward Kategorie Card
    // =============================================================

    public Div createRewardChildCard(
            RewardCategory rewardCategory
    ) {

        Button exchangeButton =
                new Button(
                        "Einlösen",
                        new Icon(
                                VaadinIcon.GIFT
                        )
                );

        exchangeButton.addClassName(
                "famigo-task-done-button"
        );

        exchangeButton.addThemeVariants(
                ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SMALL
        );

        exchangeButton.addClickListener(
                event -> {

                    StarExchangeDialog starExchangeDialog =
                            new StarExchangeDialog(
                                    rewardOptionService,
                                    rewardService,
                                    rewardCategory.toString(),
                                    userService
                            );

                    starExchangeDialog.open();
                }
        );

        return RewardCards.categoryCard(
                rewardCategory,
                describeCategory(rewardCategory),
                exchangeButton
        );
    }


    // =============================================================
    // Kategorie Beschreibung
    // =============================================================

    /**
     * Zeigt an, wie viele Belohnungen vorhanden sind
     * und ab wie vielen Sternen sie verfügbar sind.
     */
    private String describeCategory(
            RewardCategory rewardCategory
    ) {

        if (rewardCategory ==
                RewardCategory.SELBSTWUNSCH) {

            return "Dein eigener Wunsch";
        }

        List<RewardOption> rewardOptions =
                rewardOptionService
                        .getRewardOptionByCategory(
                                rewardCategory
                        )
                        .stream()
                        .filter(
                                RewardOption::isActive
                        )
                        .toList();

        if (rewardOptions.isEmpty()) {

            return "Noch nichts zum Eintauschen";
        }

        int cheapest =
                rewardOptions.stream()
                        .mapToInt(
                                RewardOption::getCost
                        )
                        .min()
                        .orElse(0);

        return rewardOptions.size()
                + " Belohnungen · ab "
                + cheapest
                + " ⭐";
    }
}

