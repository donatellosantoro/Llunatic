package it.unibas.lunatic.model.algebra.operators;

import java.util.Comparator;

public class StringComparator implements Comparator {
    
    public int compare(Object t1, Object t2) {
        return t1.toString().compareTo(t2.toString());
    }
}
