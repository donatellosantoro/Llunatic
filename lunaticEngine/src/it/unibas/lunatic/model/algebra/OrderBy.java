package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.exceptions.AlgebraException;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.operators.ListTupleIterator;
import it.unibas.lunatic.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBy extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(OrderBy.class);

    private List<AttributeRef> attributes;

    public OrderBy(List<AttributeRef> attributes) {
        if(attributes.isEmpty()){
            throw new AlgebraException("Unable to create an OrderBy without attributes");
        }
        this.attributes = attributes;
    }

    public String getName() {
        return "ORDER BY-" + attributes;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitOrderBy(this);
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator originalTuples = children.get(0).execute(source, target);
        materializeResult(originalTuples, result);
        if (logger.isDebugEnabled()) logger.debug("Executing OrderBy: " + getName() + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        if (logger.isDebugEnabled()) logger.debug(getName() + " - Result: \n" + LunaticUtility.printCollection(result));
        originalTuples.close();
        return new ListTupleIterator(result);
    }

    private void materializeResult(ITupleIterator originalTuples, List<Tuple> result) {
        while (originalTuples.hasNext()) {
            Tuple originalTuple = originalTuples.next();
            result.add(originalTuple);
        }
        Collections.sort(result, new TupleOrderByComparator(attributes));
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.attributes;
    }

    private boolean containsAttribute(Tuple tuple, AttributeRef attribute) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}


class TupleOrderByComparator implements Comparator<Tuple> {

    private List<AttributeRef> attributes;

    public TupleOrderByComparator(List<AttributeRef> attributes) {
        this.attributes = attributes;
    }
        
    public int compare(Tuple t1, Tuple t2) {
        String s1 = buildTupleString(t1);
        String s2 = buildTupleString(t2);
        return s1.compareTo(s2);
    }
    
    private String buildTupleString(Tuple tuple) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (AttributeRef attribute : attributes) {
            Cell cell = findCell(attribute, tuple);
            result.append(cell.getValue()).append("|");
        }
        result.append("]");
        return result.toString();
    }

    private Cell findCell(AttributeRef attribute, Tuple tuple) {
        for (Cell cell : tuple.getCells()) {
//            if (ChaseUtility.unAlias(cell.getAttributeRef()).equals(attribute)) {
            if (ChaseUtility.unAlias(cell.getAttributeRef()).equals(ChaseUtility.unAlias(attribute))) {
                return cell;
            }
        }
        throw new IllegalArgumentException("Unable to find alias for attribute " + attribute + " in tuple " + tuple);
    }
}