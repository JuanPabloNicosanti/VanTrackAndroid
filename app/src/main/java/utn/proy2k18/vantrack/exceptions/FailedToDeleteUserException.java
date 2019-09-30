package utn.proy2k18.vantrack.exceptions;

public class FailedToDeleteUserException extends RuntimeException {
    public FailedToDeleteUserException() {
        super("Fallo al eliminar el usuario. Inténtelo más tarde.");
    }

    public FailedToDeleteUserException(String message) {
        super(message);
    }
}
