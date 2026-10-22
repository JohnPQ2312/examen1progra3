package cr.ac.una.exception;

public class RenovacionNoPermitidaException extends RuntimeException {

    public RenovacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}