package infokom.info.famigo;

import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@Theme("my-theme")
@PWA(
        name = "Famigo",
        shortName = "Famigo",
        description = "Famigo - Aufgaben und Belohnungen für die Familie",
        backgroundColor = "#b094ff", // Deine lila Hintergrundfarbe
        themeColor = "#b094ff"
)
@Viewport("width=device-width, initial-scale=1.0, viewport-fit=cover")
@Push
public class FamigoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(FamigoApplication.class, args);
    }
}
