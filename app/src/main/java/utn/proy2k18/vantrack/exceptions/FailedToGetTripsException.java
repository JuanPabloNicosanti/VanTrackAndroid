package utn.proy2k18.vantrack.exceptions;

public class FailedToGetTripsException extends RuntimeException {
    public FailedToGetTripsException() {
        super("Fallo al intentar obtener los servicios que cumplen con los criterios de búsqueda. " +
                "Inténtelo más tarde.");
    }

    public FailedToGetTripsException(String message) {
        super(message);
    }
}
