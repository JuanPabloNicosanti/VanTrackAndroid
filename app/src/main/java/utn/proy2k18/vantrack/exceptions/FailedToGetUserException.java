package utn.proy2k18.vantrack.exceptions;

public class FailedToGetUserException extends RuntimeException {
    public FailedToGetUserException() {
        super("Fallo al obtener el usuario. Inténtelo más tarde");
    }

    public FailedToGetUserException(String message) {
        super(message);
    }
}
