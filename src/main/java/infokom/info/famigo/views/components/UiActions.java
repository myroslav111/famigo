package infokom.info.famigo.views.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import infokom.info.famigo.exception.DomainException;
import org.springframework.dao.OptimisticLockingFailureException;

/** Führt einen Use-Case aus und zeigt fachliche Fehler einheitlich als Notification an. */
public final class UiActions {

    private UiActions() {
    }

    /** @return {@code true}, wenn die Aktion ohne fachlichen Fehler durchlief. */
    public static boolean run(Runnable action) {
        try {
            action.run();
            return true;
        } catch (DomainException e) {
            showError(e.getMessage());
        } catch (OptimisticLockingFailureException e) {
            showError("Die Daten wurden inzwischen geändert. Bitte neu laden.");
        }
        return false;
    }

    public static void showError(String message) {
        Notification.show(message, 4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
