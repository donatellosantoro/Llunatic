package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;

public class SkolemFunctionGenerator implements IValueGenerator {

    private ISkolemPart skolemFunction;
    private String type;

    public SkolemFunctionGenerator(ISkolemPart skolemFunction, String type) {
        this.skolemFunction = skolemFunction;
        this.type = type;
    }

    public IValue generateValue(Tuple sourceTuple) {
        return new NullValue(this.skolemFunction.getValue(sourceTuple));
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.skolemFunction.toString();
    }

    public String toSQLString() {
        return toString();
    }

    @Override
    public IValueGenerator clone() {
        try {
            return (SkolemFunctionGenerator) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone " + this);
        }
    }

}
