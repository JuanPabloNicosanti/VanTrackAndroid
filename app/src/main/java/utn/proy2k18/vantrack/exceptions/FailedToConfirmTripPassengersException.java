package utn.proy2k18.vantrack.exceptions;

public class FailedToConfirmTripPassengersException extends RuntimeException {
    public FailedToConfirmTripPassengersException() {
        super("Fallo al confirmar los pasajeros del servicio. Inténtelo más tarde.");
    }

    public FailedToConfirmTripPassengersException(String message) {
        super(message);
    }
}
