package utn.proy2k18.vantrack.exceptions;

public class FailedToDeleteReservationException extends RuntimeException {
    public FailedToDeleteReservationException() {
        super("Fallo al cancelar la reserva. Inténtelo más tarde.");
    }

    public FailedToDeleteReservationException(String message) {
        super(message);
    }
}
