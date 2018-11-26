package utn.proy2k18.vantrack.exceptions;

public class FailedToModifyUserException extends RuntimeException {
    public FailedToModifyUserException() {
        super("Fallo al modificar el usuario. Inténtelo más tarde.");
    }
}
