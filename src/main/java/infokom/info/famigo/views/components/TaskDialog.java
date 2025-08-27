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
import infokom.info.famigo.entity.Reward;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskDialog extends Dialog {
    private final UserService userService;
    private final TaskService taskService;
    private final RewardService rewardService;

    public TaskDialog(UserService userService, TaskService taskService,  RewardService rewardService) {
        this.userService = userService;
        this.taskService = taskService;
        this.rewardService = rewardService;

        setCloseOnOutsideClick(true);
        setWidth("90%");

        ComboBox<User> childSelect = new ComboBox<>("Kind auswählen");
        childSelect.setItemLabelGenerator(User::getName);
        childSelect.setItems(userService.findAllChildren());

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
            Task task = new Task();
            task.setTitle(titleField.getValue());
            task.setDescription(descriptionField.getValue());
            task.setStarsReward(starsField.getValue().intValue());
            task.setDueDate(dueDatePicker.getValue());
            task.setStatus(TaskStatus.PENDING);
            task.setAssignedTo(childSelect.getValue());

            User currentParent = userService.getCurrentUser();
            task.setCreatedBy(currentParent);

            taskService.save(task);

            Reward reward = new Reward();
            reward.setTitle(titleField.getValue());
            reward.setDescription(descriptionField.getValue());
            reward.setStarCost(starsField.getValue().intValue());
            reward.setChild(childSelect.getValue());
            reward.setCreatedBy(currentParent);

            reward.setTask(task);

            rewardService.save(reward);

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
}
