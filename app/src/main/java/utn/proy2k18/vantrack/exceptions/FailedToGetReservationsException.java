package utn.proy2k18.vantrack.exceptions;

public class FailedToGetReservationsException extends RuntimeException {
    public FailedToGetReservationsException() {
        super("Fallo al obtener las reservas del usuario. Inténtelo más tarde.");
    }

    public FailedToGetReservationsException(String message) {
        super(message);
    }
}
