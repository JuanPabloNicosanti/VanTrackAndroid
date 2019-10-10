package utn.proy2k18.vantrack.exceptions;

public class FailedToEndTripException extends RuntimeException {
    public FailedToEndTripException() {
        super("Error al finalizar el viaje.");
    }

    public FailedToEndTripException(String message) {
        super(message);
    }
}
