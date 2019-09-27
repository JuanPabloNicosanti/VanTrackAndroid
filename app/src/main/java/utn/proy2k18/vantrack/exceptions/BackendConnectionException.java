package utn.proy2k18.vantrack.exceptions;

public class BackendConnectionException extends RuntimeException {
    public BackendConnectionException() {
        super("Error accediendo al servidor.");
    }

    public BackendConnectionException(String message) {
        super(message);
    }

    public BackendConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
