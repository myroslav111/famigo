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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.User;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.RewardService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;

import java.util.List;

public class StarExchangeDialog extends Dialog {
    private final RewardOptionService rewardOptionService;
    private final RewardService rewardService;
    private final String category;
    private final UserService userService;

    public StarExchangeDialog(RewardOptionService rewardOptionService,
                              RewardService rewardService,
                              String category,
                              UserService userService) {
        this.rewardOptionService = rewardOptionService;
        this.rewardService = rewardService;
        this.category = category;
        this.userService = userService;

        VerticalLayout starExchangeLayout = new VerticalLayout();
        starExchangeLayout.setSizeFull();

        User child = userService.getCurrentUser();

        H1 starBalance = new H1("Dein Sternzustand ist: " + child.getStars());
        starBalance.addClassName("famigo-page-title");

        add(starBalance, radioButtonSet(child));
    }

    private Div radioButtonSet(User child) {
        Div div = new Div();
        div.setSizeFull();
        div.setWidthFull();

        if (category.equals(RewardCategory.SELBSTWUNSCH.toString())) {
            div.add(desireChildForm(child));
            return div;
        }else{
            RadioButtonGroup<RewardOption> radioButtonGroup = new RadioButtonGroup<>();
            radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            radioButtonGroup.addClassName("famigo-reward-choices");
            radioButtonGroup.setWidthFull();
            radioButtonGroup.setLabel("Art von den Belohnungen");

            List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(category).stream()
                    .filter(RewardOption::isActive)
                    .toList();
            if (rewardOptions.isEmpty()) {
                div.add(new Paragraph("In dieser Kategorie gibt es gerade keine Belohnungen."));
                return div;
            }
            radioButtonGroup.setItems(rewardOptions);
            radioButtonGroup.setValue(rewardOptions.getFirst());
            radioButtonGroup.setRenderer(new ComponentRenderer<>(RewardCards::rewardOptionCard));

            Button exchangeButton = new Button("Umtauschen", createIcon(VaadinIcon.STAR));
            exchangeButton.addClassName("famigo-task-done-button");
            exchangeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            exchangeButton.addClickListener(event -> {
                RewardOption selected = radioButtonGroup.getValue();
                if (selected == null) {
                    return;
                }
                if (UiActions.run(() -> rewardService.redeem(selected.getId()))) {
                    close();
                    Notification.show("Du hast " + selected.getCost() + " ⭐ ausgegeben!");
                }
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
        costField.setValue(1);
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
            Integer cost = costField.getValue();
            if (cost == null || cost < 1 || cost > 100 || textAreaDesire.isEmpty()) {
                UiActions.showError("Bitte einen Wunsch und einen Wert zwischen 1 und 100 eingeben.");
                return;
            }
            RewardOption newOption = new RewardOption();
            newOption.setCost(cost);
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

}
