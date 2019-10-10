package utn.proy2k18.vantrack.exceptions;

public class FailedToGetDriverTripsException extends RuntimeException {
    public FailedToGetDriverTripsException() {
        super("Fallo al intentar obtener los viajes del conductor. Inténtelo más tarde.");
    }

    public FailedToGetDriverTripsException(String message) {
        super(message);
    }
}
