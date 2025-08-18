package infokom.info.famigo.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.service.SessionService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.components.TaskDialog;
import infokom.info.famigo.views.securityviews.LoginView;


public class MainViewLayout extends VerticalLayout implements RouterLayout {

    private final SessionService sessionService;
    private final Div contentArea = new Div();
//    private final TaskDialog taskDialog;
    private final UserService userService;
    private final TaskService taskService;

    public MainViewLayout(SessionService sessionService, UserService userService, TaskService taskService) {
        this.sessionService = sessionService;
        this.taskService = taskService;
        this.userService = userService;




        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // === Header ===
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("100px");
        header.getStyle()
                .set("border", "2px solid orange")
                .set("background-color", "blue");
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        header.setAlignItems(Alignment.CENTER);

        // Optional Logo or Title
        Image logo = new Image("images/logo.png", "Logo");
        logo.setHeight("60px");

        Button logoutButton = new Button("Logout", e -> {
            sessionService.logout();
            UI.getCurrent().navigate(LoginView.class);
        });
        logoutButton.getStyle().setColor("black").setBackgroundColor("white");

        header.add(logo, logoutButton);

        add(header);

        // Content
        contentArea.setHeightFull();
        contentArea.getStyle()
                .set("overfllow", "auto")
                .set("flex-grow", "1")
                .set("min-height", "0");
        contentArea.setWidthFull();
        add(contentArea);
        expand(contentArea); // wichtig, damit Footer unten bleibt

        // === Footer ===
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setHeight("60px");
        footer.getStyle()
                .set("border-top", "2px solid black")
                .set("padding", "10px");
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setAlignItems(Alignment.CENTER);

        Button taskButton = new Button("Task");
        taskButton.addClickListener(e -> {

            if (taskButton.getText().equals("Task")) {
                UI.getCurrent().navigate("tasks");
                taskButton.setText("Home");
            }else{
                UI.getCurrent().navigate("parent");
                taskButton.setText("Task");
            }



        });

        Button rewardButton = new Button("Reward");
        rewardButton.addClickListener(e -> {
            if (rewardButton.getText().equals("Reward")) {
                UI.getCurrent().navigate("rewards");
                rewardButton.setText("Home");
            }else{
                UI.getCurrent().navigate("parent");
                rewardButton.setText("Reward");
            }
        });

        Button addTaskButton = new Button("+", e -> {
            TaskDialog taskDialog = new TaskDialog(userService, taskService);
            taskDialog.open();
        });

        footer.add(taskButton, addTaskButton, rewardButton);
        add(footer);
    }

    public String getCurrentBtnText(){
        String currentPath = UI.getCurrent()
                .getInternals()
                .getActiveViewLocation()
                .getPath();

        if (currentPath.equals("tasks")) {
            return "Home";
        }else{
            return "Task";
        }
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        // hier wird die View eingefügt
        contentArea.getElement().appendChild(content.getElement());
    }

}
