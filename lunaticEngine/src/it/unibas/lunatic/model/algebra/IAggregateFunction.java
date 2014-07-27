package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import java.util.List;

public interface IAggregateFunction {
    
    public IValue evaluate(List<Tuple> tuples);
    
    public String getName();
    
    public AttributeRef getAttributeRef();

}
