package utn.proy2k18.vantrack.exceptions;

public class FailedToModifyReservationException extends RuntimeException {
    public FailedToModifyReservationException() {
        super("Fallo al realizar el cambio en la reserva. Inténtelo más tarde.");
    }

    public FailedToModifyReservationException(String message) {
        super(message);
    }
}
