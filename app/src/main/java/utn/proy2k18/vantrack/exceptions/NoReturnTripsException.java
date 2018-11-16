package utn.proy2k18.vantrack.exceptions;

public class NoReturnTripsException extends RuntimeException {
    public NoReturnTripsException() {
        super("No hay viajes de vuelta que cumplan con los criterios de busqueda.");
    }
}
