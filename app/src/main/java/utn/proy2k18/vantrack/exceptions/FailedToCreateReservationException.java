package utn.proy2k18.vantrack.exceptions;

public class FailedToCreateReservationException extends RuntimeException {
    public FailedToCreateReservationException() {
        super("Fallo al realizar la reserva del servicio. Inténtelo más tarde.");
    }

    public FailedToCreateReservationException(String message) {
        super(message);
    }
}
