package infokom.info.famigo.config;

import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.repository.TaskTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadTemplates (TaskTemplateRepository taskTemplateRepository) {
        return args -> {
            if(taskTemplateRepository.count() == 0) {
                taskTemplateRepository.saveAll(List.of(
                        new TaskTemplate("Zimmer aufräumen", "Ordne deine Spielsachen und mach dein Bett.", 5),
                        new TaskTemplate("Hausaufgaben machen", "Mathe und Deutsch bis 17 Uhr erledigen.", 8),
                        new TaskTemplate("Tisch decken", "Hilf mit, den Tisch fürs Abendessen zu decken.", 3),
                        new TaskTemplate("Wäsche sortieren", "Hilf beim Sortieren der Wäsche (weiß/bunt).", 4),
                        new TaskTemplate("Müll rausbringen", "Bringe den Müllbeutel zur Tonne.", 2)
                ));
            }
        };
    }
}
