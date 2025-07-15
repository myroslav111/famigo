package infokom.info.famigo.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;

public class TaskDialog extends Dialog {
    private final UserService userService;
    private final TaskService taskService;

    public TaskDialog(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;

        setCloseOnOutsideClick(true);
        setWidth("90%");

        ComboBox<User> childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
        childSelect.setItems(userService.findAllChildren());

        TextField titleField = new TextField("Titel");
        TextArea descriptionField = new TextArea("Description");
        NumberField starsField = new NumberField("Stars");
        starsField.setMin(1);
        starsField.setStep(1);

        Button saveButton = new Button("Save", event -> {
            if (childSelect.isEmpty() || titleField.isEmpty() || starsField.isEmpty()) {
                Notification.show("Bitte alle pflichtfelder ausfüllen");
                return;
            }
            Task task = new Task();
            task.setTitle(titleField.getValue());
            task.setDescription(descriptionField.getValue());
            task.setStarsReward(starsField.getValue().intValue());
            task.setStatus(TaskStatus.PENDING);
            task.setAssignedTo(childSelect.getValue());

            User currentParent = userService.getCurrentUser();
            task.setCreatedBy(currentParent);

            taskService.save(task);

            Notification.show("Task gespeichert");
            close();
        });

        add(new VerticalLayout(childSelect,titleField, descriptionField, starsField, saveButton));

    }
}
