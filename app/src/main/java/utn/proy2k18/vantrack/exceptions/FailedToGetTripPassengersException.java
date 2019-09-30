package utn.proy2k18.vantrack.exceptions;

public class FailedToGetTripPassengersException extends RuntimeException {
    public FailedToGetTripPassengersException() {
        super("Fallo al obtener intentar obtener los pasajeros del viaje. Inténtelo más tarde");
    }

    public FailedToGetTripPassengersException(String message) {
        super(message);
    }
}
