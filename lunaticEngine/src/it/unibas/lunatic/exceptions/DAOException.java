package it.unibas.lunatic.exceptions;

public class DAOException extends RuntimeException {
    
    public DAOException() {
        super();
    }
    
    public DAOException(String s) {
        super(s);
    }
    
    public DAOException(Exception e) {
        super(e);
    }

}
