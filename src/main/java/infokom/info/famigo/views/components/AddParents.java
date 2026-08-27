package infokom.info.famigo.views.components;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

//@Component
public class AddParents extends Dialog {

    private final TextField parentIdField;

    public AddParents(Consumer<Long> onParentIdConfirmed) {
        setHeaderTitle("Eltern hinzufügen.");

        parentIdField = new TextField("Eltern-ID");
        parentIdField.setPlaceholder("z.B 123");

        Button addButton = new Button("Add", event -> {
            String input = parentIdField.getValue().trim();

            if(input.isEmpty()) {
                Notification.show("Bitte eine Eltern-ID eingeben");
                return;
            }

            try {
                Long parentId = Long.parseLong(input);
                onParentIdConfirmed.accept(parentId);
                close();
            } catch (NumberFormatException e) {
                Notification.show("Bitte eine Zahl eingeben");
            }

            Notification.show("Eltern-ID: " + input + " wird hinzufügt");

            close();
        });

        Button cancelButton = new Button("Abrechnen", event -> close());

        VerticalLayout layout = new VerticalLayout(
                new H2("Eltern-Konto verbinden"),
                parentIdField,
                addButton,
                cancelButton
        );
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);
    }

    public Long getParentId() {
        return Long.parseLong(parentIdField.getValue().trim());
    }
}
