package utn.proy2k18.vantrack.exceptions;

public class FailedToCreateUserException extends RuntimeException {
    public FailedToCreateUserException() {
        super("Error al crear el usuario. Inténtelo más tarde.");
    }

    public FailedToCreateUserException(String message) {
        super(message);
    }
}
