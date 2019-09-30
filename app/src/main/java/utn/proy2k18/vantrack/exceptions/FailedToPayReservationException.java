package utn.proy2k18.vantrack.exceptions;

public class FailedToPayReservationException extends RuntimeException {
    public FailedToPayReservationException() {
        super("Fallo al realizar el pago. Inténtelo más tarde.");
    }

    public FailedToPayReservationException(String message) {
        super(message);
    }
}
