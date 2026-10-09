package infokom.info.famigo.exception;

/** Basis für fachliche Fehler. Die Meldung ist für die Anzeige in der UI gedacht. */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
