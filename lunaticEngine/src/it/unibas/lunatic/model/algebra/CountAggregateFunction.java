package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import java.util.List;

public class CountAggregateFunction implements IAggregateFunction {

    private AttributeRef attributeRef;

    public CountAggregateFunction(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue evaluate(List<Tuple> tuples) {
        return new ConstantValue(tuples.size());
    }

    public String getName() {
        return "count";
    }

    public String toString() {
        return "count(*)";
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }
}
