package infokom.info.famigo.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;

/**
 * Baut die Belohnungs-Karten im selben Markup wie die Aufgaben-Karten in
 * {@link infokom.info.famigo.views.childviews.ChildTaskView}: aussen .famigo-task-card,
 * links der runde Badge, rechts .famigo-task-body mit Titel, Beschreibung, Chip und Aktionen.
 * Kinder- und Elternseite teilen sich diese Fabrik, damit beide Seiten identisch aussehen.
 */
public final class RewardCards {

    private RewardCards() {
    }

    /**
     * Kachel fuer eine Belohnungs-Kategorie. Der Badge traegt das Kategorie-Symbol an der
     * Stelle, an der die Aufgaben-Karte ihre Sterne zeigt.
     */
    public static Div categoryCard(RewardCategory category, String chipText, Component... actions) {
        Div body = new Div();
        body.addClassName("famigo-task-body");

        H3 title = new H3(category.toString());
        title.addClassName("famigo-task-title");
        body.add(title);

        if (chipText != null && !chipText.isBlank()) {
            body.add(chip(chipText, false));
        }

        if (actions.length > 0) {
            Div actionBar = new Div(actions);
            actionBar.addClassName("famigo-task-actions");
            body.add(actionBar);
        }

        Div card = new Div(categoryBadge(category), body);
        card.addClassName("famigo-task-card");
        card.setId(category.toString());
        return card;
    }

    /**
     * Karte fuer eine einzelne Belohnung - der Sterne-Preis sitzt im selben gelben Badge wie
     * die Sterne-Belohnung einer Aufgabe. Wird in den Auswahllisten der Dialoge verwendet und
     * traegt dort zusaetzlich .famigo-reward-option (ohne Schatten, da die Radio-Auswahl
     * bereits hervorhebt).
     */
    public static Div rewardOptionCard(RewardOption rewardOption) {
        Div body = new Div();
        body.addClassName("famigo-task-body");

        H3 title = new H3(rewardOption.getTitle());
        title.addClassName("famigo-task-title");
        body.add(title);

        if (rewardOption.getDescription() != null && !rewardOption.getDescription().isBlank()) {
            Paragraph description = new Paragraph(rewardOption.getDescription());
            description.addClassName("famigo-task-desc");
            body.add(description);
        }

        if (!rewardOption.isActive()) {
            body.add(chip("Noch nicht freigegeben", true));
        }

        Div card = new Div(starsBadge(rewardOption.getCost()), body);
        card.addClassNames("famigo-task-card", "famigo-reward-option");
        return card;
    }

    /** Sterne-Preis als runder Blickfang links - identisch zur Aufgaben-Karte. */
    public static Div starsBadge(int stars) {
        Span count = new Span(String.valueOf(stars));
        count.addClassName("famigo-task-stars-count");

        Div badge = new Div(new Span("⭐"), count);
        badge.addClassName("famigo-task-stars");
        badge.getElement().setAttribute("title", stars + " Sterne");
        return badge;
    }

    /** Chip wie die Frist-Chips der Aufgaben; urgent faerbt ihn rot. */
    public static Span chip(String text, boolean urgent) {
        Span chip = new Span(text);
        chip.addClassName("famigo-task-chip");
        if (urgent) {
            chip.addClassName("famigo-task-chip-urgent");
        }
        return chip;
    }

    private static Div categoryBadge(RewardCategory category) {
        Div badge = new Div(new Span(iconOf(category)));
        badge.addClassName("famigo-reward-icon");
        badge.getElement().setAttribute("title", category.toString());
        return badge;
    }

    /** Symbole wie in {@link RewardCategory} kommentiert. */
    public static String iconOf(RewardCategory category) {
        return switch (category) {
            case MATERIELL -> "🎁";
            case ZEIT -> "⏰";
            case ERLEBNIS -> "🌟";
            case SELBSTWUNSCH -> "💡";
            case GELD -> "💶";
            case SPECIAL -> "\uD83E\uDD84";
        };
    }
}
