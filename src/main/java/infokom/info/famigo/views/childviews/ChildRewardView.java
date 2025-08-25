package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.views.MainViewLayout;

@Route(value = "child/rewards", layout =  MainViewLayout.class)
@PageTitle("Rewards")
public class ChildRewardView extends VerticalLayout {

    private ChildRewardView(){
        add(new H1("Children Rewards"));
    }
}
