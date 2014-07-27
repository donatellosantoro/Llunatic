package it.unibas.lunatic.model.database.lazyloading;

import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;

public interface ITupleLoader {

    Tuple loadTuple();

    public TupleOID getOid();
}
