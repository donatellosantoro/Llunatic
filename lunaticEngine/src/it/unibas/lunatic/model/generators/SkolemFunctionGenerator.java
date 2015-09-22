package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;

public class SkolemFunctionGenerator implements IValueGenerator {

    private ISkolemPart skolemFunction;

    public SkolemFunctionGenerator(ISkolemPart skolemFunction) {
        this.skolemFunction = skolemFunction;
    }

    public IValue generateValue(Tuple sourceTuple) {
        return new NullValue(this.skolemFunction.getValue(sourceTuple));
    }

    @Override
    public String toString() {
        return this.skolemFunction.toString();
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
