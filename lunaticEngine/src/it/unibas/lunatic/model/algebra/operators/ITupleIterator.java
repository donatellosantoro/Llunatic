package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.database.Tuple;
import java.util.Iterator;

public interface ITupleIterator extends Iterator<Tuple> {

    public void reset();

    public void close();

}
