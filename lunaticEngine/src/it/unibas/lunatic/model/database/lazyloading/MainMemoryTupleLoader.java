package it.unibas.lunatic.model.database.lazyloading;

import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;

public class MainMemoryTupleLoader implements ITupleLoader {

    private final Tuple tuple;

    public MainMemoryTupleLoader(Tuple tuple) {
        this.tuple = tuple;
    }

    @Override
    public Tuple loadTuple() {
        return tuple;
    }

    public TupleOID getOid() {
        return tuple.getOid();
    }
}
