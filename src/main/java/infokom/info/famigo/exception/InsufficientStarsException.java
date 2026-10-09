package infokom.info.famigo.exception;

public class InsufficientStarsException extends DomainException {
    public InsufficientStarsException(int available, int required) {
        super("Nicht genug Sterne: " + available + " vorhanden, " + required + " benötigt.");
    }
}
