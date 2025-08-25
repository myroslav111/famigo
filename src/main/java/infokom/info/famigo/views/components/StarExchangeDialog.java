package infokom.info.famigo.views.components;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import org.springframework.stereotype.Component;

@Component
public class StarExchangeDialog extends Dialog {

    public StarExchangeDialog() {
        add(new H1("Logic vom Antrag auf Sterntausch"));
    }
}
