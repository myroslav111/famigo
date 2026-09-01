package infokom.info.famigo.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class NotificationService {

    private final Map<Long, UI> userUIs =
            new ConcurrentHashMap<>();

    private final Map<Long, Command> notificationActions =
            new ConcurrentHashMap<>();

    public void register(
            Long userId,
            UI ui,
            Command refreshAction
    ) {
        userUIs.put(userId, ui);
        notificationActions.put(userId, refreshAction);
    }

    public void unregister(Long userId) {
        userUIs.remove(userId);
        notificationActions.remove(userId);
    }

    public void notifyUser(Long userId) {

        UI ui = userUIs.get(userId);
        Command action = notificationActions.get(userId);

        if (ui != null && action != null) {
            ui.access(action);
        }
    }
}
