package infokom.info.famigo.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.*;
import infokom.info.famigo.views.components.StarExchangeDialog;
import infokom.info.famigo.views.components.TaskDialog;
import infokom.info.famigo.views.securityviews.LoginView;


public class MainViewLayout extends VerticalLayout implements RouterLayout, AfterNavigationObserver {

    private final SessionService sessionService;
    private final Div contentArea = new Div();
    private final UserService userService;
    private final TaskService taskService;
    private final RewardService rewardService;

    private Button taskButton;
    private Button rewardButton;
    private Button addTaskButton;


    public MainViewLayout(SessionService sessionService, UserService userService, TaskService taskService,  RewardService rewardService) {
        this.sessionService = sessionService;
        this.taskService = taskService;
        this.userService = userService;
        this.rewardService = rewardService;

        User currentUser = sessionService.getCurrentUser();

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

        taskButton = new Button();
        rewardButton = new Button();
        addTaskButton = new Button();

        taskButton.addClickListener(e -> {
            String path = UI.getCurrent().getInternals().getActiveViewLocation().getPath();

            if(currentUser.getRole().equals(UserRole.PARENT)){
                if (path.equals("tasks")) {
                    UI.getCurrent().navigate("parent");
                }else{
                    UI.getCurrent().navigate("tasks");
                }
            }else {
                if (path.equals("child/tasks")) {
                    UI.getCurrent().navigate("child");
                }else{
                    UI.getCurrent().navigate("child/tasks");
                }
            }

            updateButtonText();
        });

        rewardButton.addClickListener(e -> {
            String path = UI.getCurrent().getInternals().getActiveViewLocation().getPath();

            if(currentUser.getRole().equals(UserRole.PARENT)){
                if (path.equals("rewards")) {
                    UI.getCurrent().navigate("parent");
                }else{
                    UI.getCurrent().navigate("rewards");
                }
            }else{
                if (path.equals("child/rewards")) {
                    UI.getCurrent().navigate("child");
                }else{
                    UI.getCurrent().navigate("child/rewards");
                }
            }

            updateButtonText();
        });

        addTaskButton.addClickListener(e -> {
            String path = UI.getCurrent().getInternals().getActiveViewLocation().getPath();

            if(currentUser.getRole().equals(UserRole.PARENT)){
                TaskDialog taskDialog = new TaskDialog(userService, taskService, rewardService);
                taskDialog.open();
            }else{
//                StarExchangeDialog starExchangeDialog = new StarExchangeDialog();
//                starExchangeDialog.open();
                if(path.equals("child/stars-exchange")) {
                    UI.getCurrent().navigate("child");
                }else{
                    UI.getCurrent().navigate("child/stars-exchange");
                }
            }
            updateButtonText();

        });

        footer.add(taskButton, addTaskButton, rewardButton);
        add(footer);
    }


    private void updateButtonText(){
        String path = UI.getCurrent()
                .getInternals()
                .getActiveViewLocation()
                .getPath();

        if(path.equals("tasks") || path.equals("child/tasks")) {
            taskButton.setText("Home");
            System.out.println(path);
        }else {
            taskButton.setText("Task");
            System.out.println(path);
        }

        if(path.equals("rewards") || path.equals("child/rewards")) {
            rewardButton.setText("Home");
        }else {
            rewardButton.setText("Reward");
        }

        if(path.equals("child/stars-exchange")) {
            addTaskButton.setText("Home");
        }else{
            addTaskButton.setText("+");
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent e){
        updateButtonText();
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        // hier wird die View eingefügt
        contentArea.getElement().appendChild(content.getElement());
    }

}
