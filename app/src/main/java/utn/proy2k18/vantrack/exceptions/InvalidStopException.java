package utn.proy2k18.vantrack.exceptions;

public class InvalidStopException extends RuntimeException {
    public InvalidStopException(String stopDesc) {
        super(String.format("Parada intermedia incorrecta: %s", stopDesc));
    }
}
