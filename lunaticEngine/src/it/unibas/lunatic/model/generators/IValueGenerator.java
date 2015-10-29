package it.unibas.lunatic.model.generators;

import speedy.model.database.IValue;
import speedy.model.database.Tuple;

public interface IValueGenerator extends Cloneable {

    IValue generateValue(Tuple sourceTuple);

    String toString();

    IValueGenerator clone();
}
