package infokom.info.famigo.config;

import infokom.info.famigo.entity.RewardOption;
import infokom.info.famigo.entity.enums.RewardCategory;
import infokom.info.famigo.repository.RewardOptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RewardDataInitializer {

    @Bean
    public CommandLineRunner loadRewardTemplates(RewardOptionRepository rewardOptionRepository) {
        return args -> {
            if (rewardOptionRepository.count() == 0) {
                rewardOptionRepository.saveAll(List.of(
                        //  Materielle Belohnungen
                        new RewardOption(),
                        new RewardOption("Sticker-Set", "Ein kleines Stickerheft oder Sammelkarten.", 5, RewardCategory.MATERIELL),
                        new RewardOption("Nascherei", "Ein Eis oder eine Schokolade.", 3, RewardCategory.MATERIELL),
                        new RewardOption("Kleines Buch", "Ein Comic oder Rätselheft.", 10, RewardCategory.MATERIELL),

                        //  Zeit-Belohnungen
                        new RewardOption("Extra Bildschirmzeit", "30 Minuten zusätzliches Tablet/TV.", 7, RewardCategory.ZEIT),
                        new RewardOption("Spielzeit mit Eltern", "Ein Brettspiel oder Vorlesen.", 5, RewardCategory.ZEIT),
                        new RewardOption("Später ins Bett", "15-30 Minuten länger wach bleiben.", 4, RewardCategory.ZEIT),

                        //  Erlebnis-Belohnungen
                        new RewardOption("Ausflug", "Spielplatz, Schwimmbad oder Kino.", 20, RewardCategory.ERLEBNIS),
                        new RewardOption("Kochen/Backen", "Ein gemeinsames Rezept ausprobieren.", 8, RewardCategory.ERLEBNIS),
                        new RewardOption("Bastelnachmittag", "Eine kreative Bastelaktion.", 10, RewardCategory.ERLEBNIS),

                        //  Selbstwunsch / Kreativ
                        new RewardOption("Eigener Wunsch", "Kind darf etwas selbst vorschlagen.", 0, RewardCategory.SELBSTWUNSCH),
                        new RewardOption("Überraschung", "Eine geheime Box, die Eltern vorbereiten.", 12, RewardCategory.SELBSTWUNSCH),

                        //  Sterne als Währung
                        new RewardOption("1 € ins Sparschwein", "10 Sterne = 1 €", 10, RewardCategory.GELD),
                        new RewardOption("50 Sterne Bonus", "Sammelpunkte für ein größeres Geschenk.", 50, RewardCategory.GELD)
                ));
            }
        };
    }
}
