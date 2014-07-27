package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;

public interface IValueGenerator extends Cloneable {

    IValue generateValue(Tuple sourceTuple);

    String toString();
    
    IValueGenerator clone();
}
