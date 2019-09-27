package utn.proy2k18.vantrack.exceptions;

public class FailedToGetTripStopsException extends RuntimeException {
    public FailedToGetTripStopsException() {
        super("Error al obtener las paradas para el viaje seleccionado. Inténtelo más tarde.");
    }

    public FailedToGetTripStopsException(String message) {
        super(message);
    }
}
