package utn.proy2k18.vantrack.exceptions;

public class FailedToRateTripException extends RuntimeException {
    public FailedToRateTripException() {
        super("Fallo al calificar el servicio. Inténtelo más tarde.");
    }

    public FailedToRateTripException(String message) {
        super(message);
    }
}
