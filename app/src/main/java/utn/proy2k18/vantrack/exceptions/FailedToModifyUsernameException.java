package utn.proy2k18.vantrack.exceptions;

public class FailedToModifyUsernameException extends RuntimeException {
    public FailedToModifyUsernameException() {
        super("Fallo al modificar el usuario. Inténtelo más tarde.");
    }
}
