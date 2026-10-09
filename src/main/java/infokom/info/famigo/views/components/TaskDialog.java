package infokom.info.famigo.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;

import java.time.LocalDate;

public class TaskDialog extends Dialog {
    private final UserService userService;
    private final TaskService taskService;

    private ComboBox<User> childSelect;

    public TaskDialog(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;

        setCloseOnOutsideClick(true);
        setWidth("90%");

        childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
//        childSelect.setItems(userService.findChildrenOfCurrentParent());

        TextField titleField = new TextField("Titel");
        TextArea descriptionField = new TextArea("Description");

        NumberField starsField = new NumberField("Stars");
        starsField.setMin(1);
        starsField.setMax(15);
        starsField.setStep(1);

        DatePicker dueDatePicker = new DatePicker("Fällig bis");
        dueDatePicker.setPlaceholder("Datum Auswählen");
        dueDatePicker.setValue(LocalDate.now().plusDays(1));



        Button saveButton = new Button("Save", event -> {
            if (childSelect.isEmpty() || titleField.isEmpty() || starsField.isEmpty() || dueDatePicker.isEmpty()) {
                Notification.show("Bitte alle pflichtfelder ausfüllen");
                return;
            }
            boolean saved = UiActions.run(() -> taskService.assign(
                    childSelect.getValue().getId(),
                    titleField.getValue(),
                    descriptionField.getValue(),
                    starsField.getValue().intValue(),
                    dueDatePicker.getValue(),
                    null));
            if (!saved) {
                return;
            }

            Notification.show("Task gespeichert");
            close();
        });


        add(new VerticalLayout(childSelect,
                titleField,
                descriptionField,
                dueDatePicker,
                starsField,
                saveButton));

    }

    @Override
    public void open() {
        childSelect.setItems(userService.findChildrenOfCurrentParent());
        super.open();
    }
}
