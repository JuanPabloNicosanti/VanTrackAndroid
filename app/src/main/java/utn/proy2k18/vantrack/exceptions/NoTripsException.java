package utn.proy2k18.vantrack.exceptions;

public class NoTripsException extends RuntimeException {
    public NoTripsException() {
        super("No hay viajes de ida que cumplan con los criterios de busqueda.");
    }
}
