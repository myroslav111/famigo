package infokom.info.famigo.exception;

public class InvalidTaskStateException extends DomainException {
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
