package infokom.info.famigo.exception;

/** Allgemeiner fachlicher "nicht gefunden"-Fehler (Benutzer, Belohnung, Vorlage …). */
public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
