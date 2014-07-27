package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.NullValue;
import java.util.List;

public class ValueAggregateFunction implements IAggregateFunction {
    
    private AttributeRef attributeRef;

    public ValueAggregateFunction(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue evaluate(List<Tuple> tuples) {
        if (tuples.isEmpty()) {
            return new NullValue(LunaticConstants.NULL_VALUE);
        }
        if (!checkValues(tuples, attributeRef)) {
            throw new ChaseException("Trying to extract aggregate value " + attributeRef + " from tuples with different values " + tuples);
        }
        return tuples.get(0).getCell(attributeRef).getValue();        
    }

    private boolean checkValues(List<Tuple> tuples, AttributeRef attribute) {
        IValue first = tuples.get(0).getCell(attribute).getValue();
        for (Tuple tuple : tuples) {
            IValue value = tuples.get(0).getCell(attribute).getValue();
            if (!value.equals(first)) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return "value";
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public String toString() {
        return attributeRef.toString();
    }
    
    
}
