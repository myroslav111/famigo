package infokom.info.famigo.views.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import infokom.info.famigo.entity.ChildRewardTransaction;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class StarExchangeDialog extends Dialog {
    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final String category;
    private final UserService userService;

    int countOfStars;

    public StarExchangeDialog(RewardOptionService rewardOptionService,
                              ChildRewardTransactionService childRewardTransactionService,
                              String category,
                              UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.category = category;
        this.userService = userService;

        VerticalLayout starExchangeLayout = new VerticalLayout();
        starExchangeLayout.setSizeFull();

        User child = userService.getCurrentUser();
        countOfStars = child.getStars();



        H1 starBalance = new H1("Dein Sternzustand ist: " + countOfStars);
        starBalance.addClassName("famigo-page-title");

        add(starBalance, radioButtonSet(child));
    }

    private Div radioButtonSet(User child) {
        Div div = new Div();
        div.setSizeFull();
        div.setWidthFull();

        System.out.println(category.getClass().getName());
        System.out.println(RewardCategory.SELBSTWUNSCH.getClass().getName());

        if (category.equals(RewardCategory.SELBSTWUNSCH.toString())) {
            div.add(desireChildForm(child));
            return div;
        }else{
            RadioButtonGroup<RewardOption> radioButtonGroup = new RadioButtonGroup<>();
            radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            radioButtonGroup.addClassName("famigo-reward-choices");
            radioButtonGroup.setWidthFull();
            radioButtonGroup.setLabel("Art von den Belohnungen");

            List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(category);
            radioButtonGroup.setItems(rewardOptions);
            radioButtonGroup.setValue(rewardOptions.get(0));
            radioButtonGroup.setRenderer(new ComponentRenderer<>(rewardOption -> {
                if (!rewardOption.isActive()) {
                    radioButtonGroup.getElement().setEnabled(false);
                }
                return RewardCards.rewardOptionCard(rewardOption);
            }));

            Button exchangeButton = new Button("Umtauschen", createIcon(VaadinIcon.STAR));
            exchangeButton.addClassName("famigo-task-done-button");
            exchangeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            exchangeButton.addClickListener(event -> {
                executeExchangeStars(radioButtonGroup.getValue(), child);
                close();
                Notification.show("Du hast " + radioButtonGroup.getValue().getCost() + " ⭐ ausgegeben!");
            });

            div.add(new VerticalLayout(radioButtonGroup, exchangeButton));
            return div;
        }

    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs)");
        return icon;
    }

    private VerticalLayout desireChildForm(User child){
        VerticalLayout desireContentFormLayout = new VerticalLayout();

        desireContentFormLayout.setSizeFull();
        desireContentFormLayout.setSpacing(true);
        desireContentFormLayout.setHeightFull();
        desireContentFormLayout.setPadding(true);

        TextArea textAreaDesire = new TextArea();
        textAreaDesire.setLabel("Dein Wunsch");
        textAreaDesire.setHelperText("Tippe hier deinen Wunsch ein");
        textAreaDesire.setId(RewardCategory.SELBSTWUNSCH.toString());
        textAreaDesire.setClearButtonVisible(true);
        textAreaDesire.setSuffixComponent(new Span(":)"));
        textAreaDesire.setWidthFull();

        IntegerField costField = new IntegerField();
        costField.setLabel("Wert");
        costField.setHelperText("max 100 items");
        costField.setRequiredIndicatorVisible(true);
        costField.setMin(1);
        costField.setMax(100);
        costField.setValue(0);
        costField.setStepButtonsVisible(true);

        costField.setI18n(new IntegerField.IntegerFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setBadInputErrorMessage("Invalid number format")
                .setMinErrorMessage("Quantity must be at least 1")
                .setMaxErrorMessage("Maximum 10o items available"));


        Button sent =  new Button("Senden", createIcon(VaadinIcon.PAPERPLANE));
        sent.addClassName("famigo-task-done-button");
        sent.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sent.addClickListener(event -> {
            RewardOption newOption = new RewardOption();
            newOption.setCost(costField.getValue());
            newOption.setDescription(textAreaDesire.getValue());
            newOption.setTitle("Eigene Wunsch");
            newOption.setCategory(RewardCategory.SELBSTWUNSCH);
            newOption.setActive(false);
            newOption.setCreateBy(child);

            rewardOptionService.create(newOption);

            close();

            Notification.show("Wunsch wurde geschickt!");
        });

        desireContentFormLayout.add(textAreaDesire, costField, sent);

        return desireContentFormLayout;
    }

    public void executeExchangeStars(RewardOption rewardOption, User child){
        if (child.getStars() < rewardOption.getCost()) {
            Notification.show("Dir fählt noch " + (rewardOption.getCost() - child.getStars()) + " ⭐ !");
            return;
        }
        countOfStars = child.getStars() - rewardOption.getCost();
        userService.updateUserStar(countOfStars);

        ChildRewardTransaction  childRewardTransaction = new ChildRewardTransaction();
        childRewardTransaction.setRedeemedAt(LocalDateTime.now());
        childRewardTransaction.setStarsSpent(rewardOption.getCost());
        childRewardTransaction.setChild(child);
        childRewardTransaction.setReward(rewardOption);

        childRewardTransactionService.save(childRewardTransaction);
    }

}
