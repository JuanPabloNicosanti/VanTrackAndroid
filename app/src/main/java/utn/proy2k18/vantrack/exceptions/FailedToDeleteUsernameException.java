package utn.proy2k18.vantrack.exceptions;

public class FailedToDeleteUsernameException extends RuntimeException {
    public FailedToDeleteUsernameException() {
        super("Fallo al eliminar el usuario. Inténtelo más tarde.");
    }
}
