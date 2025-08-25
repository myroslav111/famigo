package infokom.info.famigo.views.childviews;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.views.MainViewLayout;

@Route(value = "child/tasks",  layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ChildTaskView extends VerticalLayout {

    private ChildTaskView(){
        add(new H1("Child Tasks"));
    }

}
