package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;

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
        SkolemFunctionGenerator clone = null;
        try {
            clone = (SkolemFunctionGenerator) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return null;
    }

}

