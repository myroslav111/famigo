package infokom.info.famigo.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.UserRole;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.NotificationService;
import infokom.info.famigo.service.SessionService;


import java.util.Arrays;
import java.util.List;


public class NotificationPopup extends Div {
    private final ChildRewardTransactionService childRewardTransactionService;
    private final User user;
    private final SessionService sessionService;
    private final NotificationService notificationService;

    private final ContextMenu menu;
    private final MessagesButton bellBtn;

    public NotificationPopup(User user, ChildRewardTransactionService childRewardTransactionService,  SessionService sessionService, NotificationService notificationService) {
        this.user = user;
        this.childRewardTransactionService = childRewardTransactionService;
        this.sessionService = sessionService;
        this.notificationService = notificationService;

        bellBtn = new MessagesButton();

        menu = new ContextMenu();
        menu.setOpenOnClick(true);
        menu.setTarget(bellBtn);

        add(bellBtn);

        refresh();
    }

    public void updateStatusExecuteReward(Long childRewardTransactionId) {
        childRewardTransactionService.updateStatusImplemented(true, childRewardTransactionId);
    }

    public void updateStatusViewedByChild(Long childRewardTransactionId) {
        childRewardTransactionService.updateStatusViewedByChild(true, childRewardTransactionId);
    }

    public void refresh() {
        menu.removeAll();
        if (user.getRole() == UserRole.PARENT) {
            List<ChildRewardTransaction> childRewardTransactions = childRewardTransactionService
                    .getImplementedRewards(user.getChildren());

            bellBtn.setUnreadMessages(childRewardTransactions.size());

            childRewardTransactions.forEach(childRewardTransaction -> {
                menu.addItem(childRewardTransaction.getChild().getName() + " "
                                + "categ: " + childRewardTransaction.getReward().getCategory()
                                + " desc: " + childRewardTransaction.getReward().getDescription(),
                        event -> {
                            updateStatusExecuteReward(childRewardTransaction.getId());
                            Notification.show(childRewardTransaction.getChild().getName() + " ist akzeptiert");
                            refresh();
                        } );
            });
        }else {
            User currentUser = sessionService.getCurrentUser();
            List<ChildRewardTransaction> childRewardTransactions = childRewardTransactionService
                    .getImplementedRewardsAccepted(currentUser.getId());



            bellBtn.setUnreadMessages(childRewardTransactions.size());
            childRewardTransactions.forEach(childRewardTransaction -> {
                menu.addItem("dein Wunsch nach " + childRewardTransaction.getReward().getDescription() + " wird erfühlen",
                        event -> {
                            updateStatusViewedByChild(childRewardTransaction.getId());
                            Notification.show(" ist geschaut und entfernt");
                            refresh();
                        });
            });
        }



    }


    public class MessagesButton extends Button {
        private final Element numberOfNotifications;

        public MessagesButton() {
            super(VaadinIcon.BELL_O.create());
            numberOfNotifications = new Element("span");
            numberOfNotifications.getStyle()
                    .setPosition(Style.Position.ABSOLUTE)
                    .setTransform("translate(-40%, -85%)");
            numberOfNotifications.getThemeList().addAll(Arrays.asList("badge",
                    "error", "primary", "small", "pill"));
        }

        public void setUnreadMessages(int unread) {
            numberOfNotifications.setText(unread + "");

            if (unread > 0) {
                if (numberOfNotifications.getParent() == null) {
                    getElement().appendChild(numberOfNotifications);
                }
            } else {
                if (numberOfNotifications.getParent() != null) {
                    numberOfNotifications.removeFromParent();
                }
            }
        }

    }

}
