package infokom.info.famigo.views.parentviews;

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
import infokom.info.famigo.views.components.ManageRewardOptionDialog;
import infokom.info.famigo.views.components.RewardCards;

import java.util.Arrays;
import java.util.List;

@Route(value = "manage/rewards", layout = MainViewLayout.class)
@PageTitle("Belohnungsverfahren")
public class ManageRewards extends VerticalLayout {

    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final UserService userService;

    private Div rewardsLayout;


    public ManageRewards(RewardOptionService rewardOptionService, ChildRewardTransactionService childRewardTransactionService, UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.userService = userService;

        addClassName("famigo-page");
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        H2 pageTitle = new H2("Belohnungen verwalten 🎁");
        pageTitle.addClassName("famigo-page-title");

        Paragraph pageSubtitle = new Paragraph("Lege je Kategorie fest, wogegen deine Kinder ihre Sterne eintauschen können.");
        pageSubtitle.addClassName("famigo-page-subtitle");

        rewardsLayout = new Div();
        rewardsLayout.addClassName("famigo-task-grid");

        add(pageTitle, pageSubtitle, rewardsLayout);

        refreshRewards();
    }

    public void refreshRewards() {
        rewardsLayout.removeAll();

        Arrays.stream(RewardCategory.values())
                .forEach(rewardCategory -> rewardsLayout.add(createRewardsParentCard(rewardCategory)));
    }

    public Div createRewardsParentCard(RewardCategory rewardCategory) {
        Button manageButton = new Button("Verwalten", new Icon(VaadinIcon.EDIT));
        manageButton.addClassName("famigo-task-done-button");
        manageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        manageButton.addClickListener(event -> {
            ManageRewardOptionDialog manageRewardOptionDialog = new ManageRewardOptionDialog(
                    rewardOptionService,
                    childRewardTransactionService,
                    rewardCategory.toString(),
                    userService,
                    this::refreshRewards);
            manageRewardOptionDialog.open();
        });

        return RewardCards.categoryCard(rewardCategory, describeCategory(rewardCategory), manageButton);
    }

    /** Chip-Text: wie viele Belohnungen in dieser Kategorie aktiv bzw. inaktiv sind. */
    private String describeCategory(RewardCategory rewardCategory) {
        List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(rewardCategory);

        long active = rewardOptions.stream().filter(RewardOption::isActive).count();
        long inactive = rewardOptions.size() - active;

        if (rewardOptions.isEmpty()) {
            return "Noch keine Belohnung";
        }

        return active + " aktiv · " + inactive + " inaktiv";
    }

}
