package utn.proy2k18.vantrack.exceptions;

public class NoPassengersException extends RuntimeException {
    public NoPassengersException() {
        super("El viaje no posee pasajeros.");
    }
}
