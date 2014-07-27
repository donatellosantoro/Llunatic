package it.unibas.lunatic.utility.combinatorial;

public class CombinatorialException extends RuntimeException {
    
    public CombinatorialException() {}
    
    public CombinatorialException(String message) {
        super(message);
    }
    
    public CombinatorialException(Throwable t) {
        super(t);
    }

}
