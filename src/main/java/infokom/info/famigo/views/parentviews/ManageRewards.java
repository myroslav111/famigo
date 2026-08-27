package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.lang.reflect.Array;
import java.util.Arrays;

@Route(value = "manage/rewards", layout = MainViewLayout.class)
@PageTitle("Belohnungsverfahren")
public class ManageRewards extends VerticalLayout {

    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final UserService userService;

    private VerticalLayout rewardsLayout;


    public ManageRewards(RewardOptionService rewardOptionService, ChildRewardTransactionService childRewardTransactionService, UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.userService = userService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        rewardsLayout = new VerticalLayout();

        add(new H1("Manage Rewards"), createRewardsParentList());
    }

    public VerticalLayout createRewardsParentList() {
        VerticalLayout layout = new VerticalLayout();

        layout.setSizeFull();
        layout.setHeightFull();
        layout.getStyle().set("overflow", "auto");

        Arrays.stream(RewardCategory.values())
                .forEach(rewardCategory -> {
                    Image img = new Image("images/default-page.png", "default-page");
                    img.setWidth("50px");
                    layout.add(createRewardsParentCard(rewardCategory.toString(), img));
                });

        return layout;
    }

    public Card createRewardsParentCard(String rewardCategory, Image image) {
        Card card = new Card();
        card.getStyle().set("border", "1px solid #ccc");
        card.setWidthFull();
        card.setId(rewardCategory);

        card.getElement().addEventListener("click", event -> {
            System.out.println("card click");
            String category = RewardCategory.valueOf(rewardCategory).toString();
        });

        VerticalLayout cardContent = new VerticalLayout();

        cardContent.add(image);
        cardContent.add(new H3(rewardCategory.toString()));
        cardContent.add(new Paragraph("Lapland is the northern-most region of Finland"));

        card.add(cardContent);
        return card;
    }

}
