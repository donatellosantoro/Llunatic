package it.unibas.lunatic.exceptions;

public class PartialOrderException extends RuntimeException {

    public PartialOrderException() {
    }

    public PartialOrderException(String msg) {
        super(msg);
    }
    
    public PartialOrderException(Exception e) {
        super(e);
    }
}
