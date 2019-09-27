package utn.proy2k18.vantrack.exceptions;

public class FailedToGetNotificationsException extends RuntimeException {
    public FailedToGetNotificationsException() {
        super("Error al obtener las notificaciones. Inténtelo más tarde.");
    }

    public FailedToGetNotificationsException(String message) {
        super(message);
    }
}
