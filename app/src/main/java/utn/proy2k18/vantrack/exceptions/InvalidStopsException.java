package utn.proy2k18.vantrack.exceptions;

public class InvalidStopsException extends RuntimeException {
    public InvalidStopsException() {
        super("Paradas inválidas. Asegúrese que el origen y destino coincidan con la primera y " +
                "última parada de la lista.");
    }
}
