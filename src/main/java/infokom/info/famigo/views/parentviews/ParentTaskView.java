package infokom.info.famigo.views.parentviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import infokom.info.famigo.entity.Task;
import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.TaskStatus;
import infokom.info.famigo.service.TaskService;
import infokom.info.famigo.service.TaskTemplateService;
import infokom.info.famigo.service.UserService;
import infokom.info.famigo.views.MainViewLayout;

import java.util.List;

@Route(value = "tasks",layout = MainViewLayout.class)
@PageTitle("Aufgabenübersicht")
public class ParentTaskView extends VerticalLayout {
    private final UserService userService;
    private final TaskService taskService;
    private final TaskTemplateService taskTemplateService;

    private ComboBox<User> childrenSelector;

    private VerticalLayout standardTaskLayout;
    private VerticalLayout specialTaskLayout;
    private TabSheet tabSheet;


    private ParentTaskView(UserService userService, TaskService taskService, TaskTemplateService taskTemplateService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskTemplateService = taskTemplateService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setWidthFull();


        tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        tabSheet.getStyle().set("overflow", "auto");



        childrenSelector = new ComboBox<>("Kind auswählen");
        childrenSelector.setItemLabelGenerator(User::getName);
        childrenSelector.setItems(userService.findChildrenOfCurrentParent());
        childrenSelector.addValueChangeListener(e -> refreshTasks());

        standardTaskLayout = new VerticalLayout();
        standardTaskLayout.setSpacing(true);
        standardTaskLayout.setWidthFull();
        standardTaskLayout.setHeightFull();
        standardTaskLayout.getStyle().set("overflow", "auto");

        specialTaskLayout = new VerticalLayout();
        specialTaskLayout.setSpacing(true);
        specialTaskLayout.setWidthFull();
        specialTaskLayout.setHeightFull();
        specialTaskLayout.getStyle().set("overflow", "auto");

        tabSheet.add("Specialaufgaben",  specialTaskLayout);
        tabSheet.add("Standardaufgaben",  standardTaskLayout);

        add(childrenSelector, tabSheet);
    }


    public void refreshTasks() {
        specialTaskLayout.removeAll();
        standardTaskLayout.removeAll();

        User selectedChild = childrenSelector.getValue();
        if (selectedChild == null) return;

        List<Task> tasks = taskService.findByAssignedToSortedByDueDate(selectedChild.getId());

        System.out.println(tasks);
        if(!tasks.isEmpty()) {
            specialTaskLayout.add(new H4("Individuelle Aufgaben"));
            tasks.forEach(task -> specialTaskLayout.add(createTaskCard(task, true)));
        }

        List<TaskTemplate> templates = taskTemplateService.findAll();
        System.out.println(templates);
        if(!templates.isEmpty()) {
            standardTaskLayout.add(new H4("Standardaufgaben"));
            templates.forEach(template -> standardTaskLayout.add(createTemplateCard(template)));
        }

    }

    private Component createTaskCard(Task task, boolean flags){
        Card cardTask = new Card();
        cardTask.getStyle().set("border", "1px solid #ccc");
        cardTask.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(task.getTitle()));
        content.add(new Span("Sterne: " + task.getStarsReward()));
        content.add(new Span("Fällig bis: " + (task.getDueDate() != null ? task.getDueDate().toString() : "nicht gesetzt")));
        if(flags){
            content.add(new Span("Status: " + task.getStatus().toString()));
        }

        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph(task.getDescription()));
            dialog.setWidth("60%");
            dialog.open();
        });

        content.add(detailsButton);
        cardTask.add(content);
        return cardTask;
    }

    private Component createTemplateCard(TaskTemplate template){
        Card card = new Card();
        card.getStyle().set("border", "1px solid #999");
        card.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.add(new H5(template.getTitle()));
        content.add(new Span("Sterne: " + template.getStarsReward()));

        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph(template.getDescription()));
            dialog.setWidth("60%");
            dialog.open();

        });

        Button assignedButton = new Button("Assign");
        assignedButton.addClickListener(e -> {
            Task task = new Task();
            task.setTitle(template.getTitle());
            task.setStarsReward(template.getStarsReward());
            task.setDescription(template.getDescription());
            task.setStatus(TaskStatus.PENDING);
            task.setAssignedTo(childrenSelector.getValue());
            task.setCreatedBy(userService.getCurrentUser());
            task.setTemplate(template);
            taskService.save(task);
            Notification.show("Aufgaben wurde zugewiesen");
            refreshTasks();
        });

        HorizontalLayout buttons = new HorizontalLayout(detailsButton);
        content.add(buttons);
        card.add(content);

        return card;
    }
}
