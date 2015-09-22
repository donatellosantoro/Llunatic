package it.unibas.lunatic.model.database.lazyloading;

import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public interface ITupleLoader {

    Tuple loadTuple();

    public TupleOID getOid();
}
