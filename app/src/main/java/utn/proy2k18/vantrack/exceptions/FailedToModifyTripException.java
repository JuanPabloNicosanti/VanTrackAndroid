package utn.proy2k18.vantrack.exceptions;

public class FailedToModifyTripException extends RuntimeException {
    public FailedToModifyTripException() {
        super("Fallo al modificar el servicio. Inténtelo más tarde.");
    }

    public FailedToModifyTripException(String message) {
        super(message);
    }
}
