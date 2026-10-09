package infokom.info.famigo.exception;

public class TaskNotFoundException extends DomainException {
    public TaskNotFoundException(Long id) {
        super("Aufgabe nicht gefunden (ID " + id + ").");
    }
}
