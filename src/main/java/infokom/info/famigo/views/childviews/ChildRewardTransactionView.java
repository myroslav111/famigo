package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;
import infokom.info.famigo.views.components.StarExchangeDialog;

import java.util.Arrays;

@Route(value = "child/stars-exchange", layout = MainViewLayout.class)
@PageTitle("Star exchange")
public class ChildRewardTransactionView extends VerticalLayout {
    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final UserService userService;

    private VerticalLayout rewardsLayout;

    private ChildRewardTransactionView(RewardOptionService rewardOptionService, ChildRewardTransactionService childRewardTransactionService, UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.userService = userService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();

        rewardsLayout = new VerticalLayout();

        add(new H1("Child Reward"), createRewardChildCardList());
    }

    public VerticalLayout createRewardChildCardList(){
        VerticalLayout layout = new VerticalLayout();

        layout.setSizeFull();
        layout.setHeightFull();
        layout.getStyle().set("overflow", "auto");



        Arrays.stream(RewardCategory.values())
                .forEach(rewardCategory -> {
                    Image img = new Image("images/default-page.png", "default-page");
                    img.setWidth("50px");
                    layout.add(createRewardChildCard(rewardCategory.toString(), img));
                });

        return layout;
    }

    public Card createRewardChildCard(String rewardCategory, Image image){
        Card card = new Card();
        card.getStyle().set("border", "1px solid #ccc");
        card.setWidthFull();
        card.setId(rewardCategory);
        card.getElement().addEventListener("click", event -> {
            String category = RewardCategory.valueOf(rewardCategory).toString();
            StarExchangeDialog starExchangeDialog = new StarExchangeDialog(rewardOptionService,
                    childRewardTransactionService,
                    category,
                    userService);
            starExchangeDialog.open();
        });


        VerticalLayout cardContent = new VerticalLayout();


        cardContent.add(image);
        cardContent.add(new H3(rewardCategory.toString()));
        cardContent.add(new Paragraph("Lapland is the northern-most region of Finland"));

        card.add(cardContent);
        return card;
    }
}
