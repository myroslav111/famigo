package infokom.info.famigo.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.*;
import infokom.info.famigo.views.components.NotificationPopup;
import infokom.info.famigo.views.components.TaskDialog;
import infokom.info.famigo.views.securityviews.LoginView;

import java.util.List;


public class MainViewLayout extends VerticalLayout implements RouterLayout, AfterNavigationObserver {

    private final SessionService sessionService;
    private final Div contentArea = new Div();
    private final UserService userService;
    private final TaskService taskService;
    private final RewardService rewardService;
    private ChildRewardTransactionService childRewardTransactionService;
    private NotificationService notificationService;

    private Button taskButton;
    private Button rewardButton;
    private Button addTaskButton;
    private NotificationPopup notificationPopup;


    public MainViewLayout(SessionService sessionService, UserService userService, TaskService taskService,  RewardService rewardService, ChildRewardTransactionService childRewardTransactionService, NotificationService notificationService) {
        this.sessionService = sessionService;
        this.taskService = taskService;
        this.userService = userService;
        this.rewardService = rewardService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.notificationService = notificationService;

        User currentUser = sessionService.getCurrentUser();

        addClassName("famigo-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // === Header ===
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("famigo-header");
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        // Wortmarke mit Maskottchen – wie auf der Login-Seite
        Span mascot = new Span("⭐");
        mascot.addClassName("famigo-brand-mascot");
        Span brand = new Span(mascot, new Span("famigo"));
        brand.addClassName("famigo-brand");

        Button logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT), e -> {
            sessionService.logout();
            UI.getCurrent().navigate(LoginView.class);
        });
        logoutButton.addClassName("famigo-header-button");

        HorizontalLayout headerActions = new HorizontalLayout(executeRequest(currentUser), logoutButton);
        headerActions.setAlignItems(Alignment.CENTER);

        header.add(brand, headerActions);

        UI ui = UI.getCurrent();

        if (ui != null) {

            notificationService.register(
                    currentUser.getId(),
                    ui,
                    () -> {
                        if (notificationPopup != null) {
                            notificationPopup.refresh();
                        }
                    }
            );

            ui.addDetachListener(event ->
                    notificationService.unregister(currentUser.getId())
            );
        }

        add(header);

        // Content
        contentArea.addClassName("famigo-content");
        contentArea.setHeightFull();
        contentArea.getStyle()
                .set("overflow", "auto")
                .set("flex-grow", "1")
                .set("min-height", "0");
        contentArea.setWidthFull();
        add(contentArea);
        expand(contentArea); // wichtig, damit Footer unten bleibt

        // === Footer ===
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("famigo-footer");
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setAlignItems(Alignment.CENTER);

        taskButton = new Button();
        rewardButton = new Button();
        addTaskButton = new Button();

        taskButton.addClassName("famigo-nav-button");
        rewardButton.addClassName("famigo-nav-button");
        addTaskButton.addClassNames("famigo-nav-button", "famigo-nav-button-accent");

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
            taskButton.setText("\uD83C\uDFE0");
            System.out.println(path);
        }else {
            taskButton.setText("\uD83D\uDCCB");
            System.out.println(path);
        }

        if(path.equals("rewards") || path.equals("child/rewards")) {
            rewardButton.setText("\uD83C\uDFE0");
        }else {
            rewardButton.setText("\uD83D\uDCCA");
        }

        if (userService.getCurrentUser().getRole().equals(UserRole.PARENT)) {
            addTaskButton.setText("✏️");
        } else {
            if (path.equals("child/stars-exchange")) {
                addTaskButton.setText("\uD83C\uDFE0");
            } else {
                addTaskButton.setText("🎁");
            }
        }
    }

    public NotificationPopup executeRequest(User user) {

        notificationPopup = new NotificationPopup(
                user,
                childRewardTransactionService,
                sessionService,
                notificationService
        );

        return notificationPopup;
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
