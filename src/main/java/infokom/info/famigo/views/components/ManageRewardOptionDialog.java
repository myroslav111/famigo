package infokom.info.famigo.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.service.ChildRewardTransactionService;
import infokom.info.famigo.service.RewardOptionService;
import infokom.info.famigo.service.UserService;

import java.util.List;
import java.util.Objects;

/**
 * Eltern-Gegenstueck zum {@link StarExchangeDialog}: waehrend das Kind eine Belohnung
 * einer Kategorie auswaehlt und eintauscht, verwalten die Eltern hier die Belohnungen
 * derselben Kategorie - also anlegen (Create) und aendern (Update).
 */
public class ManageRewardOptionDialog extends Dialog {
    private final RewardOptionService rewardOptionService;
    private final ChildRewardTransactionService childRewardTransactionService;
    private final RewardCategory category;
    private final UserService userService;
    private final Runnable onSaved;

    private final RadioButtonGroup<RewardOption> rewardOptionGroup = new RadioButtonGroup<>();
    private final Details rewardOptionDetails = new Details();
    private final Span emptyHint = new Span("Für diese Kategorie gibt es noch keine Belohnung.");

    private final TextField titleField = new TextField("Titel");
    private final TextArea descriptionField = new TextArea("Beschreibung");
    private final IntegerField costField = new IntegerField("Wert");
    private final Checkbox activeField = new Checkbox("Aktiv (für Kinder sichtbar)");
    private final Button deleteButton = new Button("Löschen");
    private final H3 formTitle = new H3();

    /** null = neue Belohnung anlegen, sonst wird diese Belohnung aktualisiert. */
    private RewardOption editedRewardOption;

    public ManageRewardOptionDialog(RewardOptionService rewardOptionService,
                                    ChildRewardTransactionService childRewardTransactionService,
                                    String category,
                                    UserService userService,
                                    Runnable onSaved) {
        this.rewardOptionService = rewardOptionService;
        this.childRewardTransactionService = childRewardTransactionService;
        this.category = RewardCategory.valueOf(category);
        this.userService = userService;
        this.onSaved = onSaved;

        setCloseOnOutsideClick(true);
        setWidth("90%");
        setMaxWidth("60rem");

        // Zwei Spalten (Formular links, Liste rechts), sobald der Bildschirm breit genug ist -
        // darunter untereinander, siehe .famigo-reward-manage in styles.css.
        Div columns = new Div(rewardOptionForm(), rewardOptionList());
        columns.addClassName("famigo-reward-manage");
        columns.setWidthFull();

        add(new H1("Belohnungen: " + this.category), columns);

        refreshRewardOptions();
        startCreate();
    }

    private Details rewardOptionList() {
        rewardOptionGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        rewardOptionGroup.addClassName("famigo-reward-choices");
        rewardOptionGroup.setWidthFull();
        rewardOptionGroup.setRenderer(new ComponentRenderer<>(RewardCards::rewardOptionCard));

        // Auswahl einer vorhandenen Belohnung schaltet das Formular in den Update-Modus.
        rewardOptionGroup.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                startUpdate(event.getValue());
            }
        });

        Button newButton = new Button("Neue Belohnung", createIcon(VaadinIcon.PLUS), event -> startCreate());
        newButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout listContent = new VerticalLayout(emptyHint, rewardOptionGroup, newButton);
        listContent.setPadding(false);
        listContent.setWidthFull();
        listContent.getStyle().set("max-height", "55vh").set("overflow", "auto");

        rewardOptionDetails.addClassName("famigo-reward-list");
        rewardOptionDetails.add(listContent);
        // Auf schmalen Screens bleibt die Liste eingeklappt, damit das Formular oben sichtbar
        // ist; ab der Zwei-Spalten-Breite steht sie offen daneben (Breakpoint wie in styles.css).
        rewardOptionDetails.setOpened(false);
        rewardOptionDetails.getElement().executeJs(
                "this.opened = window.matchMedia('(min-width: 900px)').matches;");

        return rewardOptionDetails;
    }

    private VerticalLayout rewardOptionForm() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.addClassName("famigo-reward-form");
        formLayout.setWidthFull();
        formLayout.setSpacing(true);
        formLayout.setPadding(true);

        titleField.setWidthFull();
        titleField.setRequiredIndicatorVisible(true);
        titleField.setClearButtonVisible(true);

        descriptionField.setWidthFull();
        descriptionField.setHelperText("Was bekommt das Kind für diese Belohnung?");
        descriptionField.setClearButtonVisible(true);

        costField.setHelperText("Anzahl der Sterne");
        costField.setRequiredIndicatorVisible(true);
        costField.setMin(0);
        costField.setMax(100);
        costField.setStepButtonsVisible(true);
        costField.setI18n(new IntegerField.IntegerFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setBadInputErrorMessage("Invalid number format")
                .setMinErrorMessage("Der Wert darf nicht negativ sein")
                .setMaxErrorMessage("Maximal 100 Sterne möglich"));

        Button saveButton = new Button("Speichern", event -> save());
        saveButton.addClassName("famigo-task-done-button");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Abbrechen", event -> close());

        // Nur im Update-Modus aktiv - eine noch nicht angelegte Belohnung kann nicht geloescht werden.
        deleteButton.setIcon(createIcon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> confirmDelete());

        formLayout.add(formTitle, titleField, descriptionField, costField, activeField,
                new HorizontalLayout(saveButton, cancelButton, deleteButton));

        return formLayout;
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs)");
        return icon;
    }

    private void refreshRewardOptions() {
        List<RewardOption> rewardOptions = rewardOptionService.getRewardOptionByCategory(category);

        rewardOptionGroup.setItems(rewardOptions);
        rewardOptionGroup.setVisible(!rewardOptions.isEmpty());
        emptyHint.setVisible(rewardOptions.isEmpty());
        rewardOptionDetails.setSummaryText("Vorhandene Belohnungen (" + rewardOptions.size() + ")");
    }

    /** Formular leeren -> die naechste Speicherung legt eine neue Belohnung an. */
    private void startCreate() {
        editedRewardOption = null;
        rewardOptionGroup.clear();

        formTitle.setText("Neue Belohnung anlegen");
        deleteButton.setEnabled(false);
        titleField.clear();
        descriptionField.clear();
        costField.setValue(0);
        activeField.setValue(true);
    }

    /** Formular mit einer vorhandenen Belohnung fuellen -> die naechste Speicherung aktualisiert sie. */
    private void startUpdate(RewardOption rewardOption) {
        editedRewardOption = rewardOption;

        formTitle.setText("Belohnung bearbeiten");
        deleteButton.setEnabled(true);
        titleField.setValue(rewardOption.getTitle() == null ? "" : rewardOption.getTitle());
        descriptionField.setValue(rewardOption.getDescription() == null ? "" : rewardOption.getDescription());
        costField.setValue(rewardOption.getCost());
        activeField.setValue(rewardOption.isActive());
    }

    private void save() {
        if (titleField.isEmpty() || costField.isEmpty()) {
            Notification.show("Bitte alle Pflichtfelder ausfüllen");
            return;
        }

        boolean isNew = editedRewardOption == null;
        RewardOption rewardOption = isNew ? new RewardOption() : editedRewardOption;

        rewardOption.setTitle(titleField.getValue());
        rewardOption.setDescription(descriptionField.getValue());
        rewardOption.setCost(costField.getValue());
        rewardOption.setCategory(category);
        rewardOption.setActive(activeField.getValue());

        if (isNew) {
            rewardOption.setCreateBy(userService.getCurrentUser());
            rewardOptionService.create(rewardOption);
        } else {
            rewardOptionService.update(rewardOption);
        }

        Notification.show(isNew ? "Belohnung wurde angelegt" : "Belohnung wurde aktualisiert");

        reloadAfterChange(rewardOption);
    }

    /**
     * Loeschen mit Rueckfrage. Belohnungen, die bereits eingetauscht wurden, haengen an
     * {@link infokom.info.famigo.entity.ChildRewardTransaction}-Eintraegen und werden deshalb
     * nicht geloescht, sondern nur deaktiviert - sonst zeigt die Historie der Kinder ins Leere.
     */
    private void confirmDelete() {
        RewardOption target = editedRewardOption;
        if (target == null) {
            return;
        }

        long redeemed = childRewardTransactionService.countByRewardOption(target.getId());

        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Belohnung löschen?");
        confirmDialog.setCloseOnOutsideClick(true);
        confirmDialog.setWidth("60%");

        Button cancelButton = new Button("Abbrechen", event -> confirmDialog.close());

        if (redeemed > 0) {
            confirmDialog.add(new Paragraph("\"" + target.getTitle() + "\" wurde bereits "
                    + redeemed + "-mal eingetauscht und kann deshalb nicht gelöscht werden. "
                    + "Du kannst sie stattdessen deaktivieren – dann taucht sie beim Kind nicht mehr auf."));

            Button deactivateButton = new Button("Deaktivieren", event -> {
                target.setActive(false);
                rewardOptionService.update(target);
                confirmDialog.close();

                Notification.show("Belohnung wurde deaktiviert");
                reloadAfterChange(target);
            });
            deactivateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            confirmDialog.getFooter().add(cancelButton, deactivateButton);
        } else {
            confirmDialog.add(new Paragraph("\"" + target.getTitle() + "\" wird endgültig gelöscht."));

            Button deleteConfirmButton = new Button("Löschen", event -> {
                rewardOptionService.delete(target.getId());
                confirmDialog.close();

                Notification.show("Belohnung wurde gelöscht");
                reloadAfterChange(null);
            });
            deleteConfirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            confirmDialog.getFooter().add(cancelButton, deleteConfirmButton);
        }

        confirmDialog.open();
    }

    /** Liste neu laden, Formular auf die uebergebene Belohnung stellen (null = Neuanlage) und die View informieren. */
    private void reloadAfterChange(RewardOption selection) {
        refreshRewardOptions();

        if (selection == null) {
            startCreate();
        } else {
            selectSaved(selection);
        }

        if (onSaved != null) {
            onSaved.run();
        }
    }

    /** Nach dem Neuladen dieselbe Belohnung wieder markieren, damit weitergearbeitet werden kann. */
    private void selectSaved(RewardOption saved) {
        rewardOptionGroup.getListDataView().getItems()
                .filter(rewardOption -> Objects.equals(rewardOption.getId(), saved.getId()))
                .findFirst()
                .ifPresentOrElse(rewardOptionGroup::setValue, this::startCreate);
    }
}
