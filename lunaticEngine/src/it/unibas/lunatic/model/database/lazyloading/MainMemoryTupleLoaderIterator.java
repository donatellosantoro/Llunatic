package it.unibas.lunatic.model.database.lazyloading;

import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.database.Tuple;
import java.util.Iterator;

public class MainMemoryTupleLoaderIterator implements Iterator<ITupleLoader> {

    private final ITupleIterator tupleIterator;

    public MainMemoryTupleLoaderIterator(ITupleIterator tupleIterator) {
        this.tupleIterator = tupleIterator;
    }

    public boolean hasNext() {
        return tupleIterator.hasNext();
    }

    public MainMemoryTupleLoader next() {
        Tuple tuple = tupleIterator.next();
        return new MainMemoryTupleLoader(tuple);
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
