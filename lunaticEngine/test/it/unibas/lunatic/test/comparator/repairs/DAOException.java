package it.unibas.lunatic.test.comparator.repairs;


public class DAOException extends RuntimeException {

    public DAOException() {
    }

    public DAOException(String msg) {
        super(msg);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
