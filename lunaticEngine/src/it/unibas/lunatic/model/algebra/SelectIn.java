package it.unibas.lunatic.model.algebra;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.database.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectIn extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(SelectIn.class);

    private List<AttributeRef> attributes;
    private IAlgebraOperator selectionOperator;

    public SelectIn(List<AttributeRef> attributes, IAlgebraOperator selectionOperator) {
        this.attributes = attributes;
        this.selectionOperator = selectionOperator;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelectIn(this);
    }

    public String getName() {
        return "SELECT" + attributes + " IN (\n" + selectionOperator.toString(LunaticConstants.INDENT + LunaticConstants.INDENT) + LunaticConstants.INDENT + LunaticConstants.INDENT + ")";
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        if (attributes.size() != selectionOperator.getAttributes(source, target).size()) {
            throw new IllegalArgumentException("Attribute sizes are different: " + attributes + " - " + selectionOperator.getAttributes(source, target));
        }
        Map<AttributeRef, Set<IValue>> valueMap = materializeInnerOperator(source, target);
        SelectInTupleIterator tupleIterator = new SelectInTupleIterator(children.get(0).execute(source, target), valueMap);
        if (logger.isDebugEnabled()) logger.debug("Executing SelectIn: " + getName() + " in attributes\n" + attributes + "Map:\n" + LunaticUtility.printMap(valueMap) + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        if (logger.isDebugEnabled()) logger.debug("Result: " + LunaticUtility.printTupleIterator(tupleIterator));
        if (logger.isDebugEnabled()) tupleIterator.reset();
        return tupleIterator;
    }

    private Map<AttributeRef, Set<IValue>> materializeInnerOperator(IDatabase source, IDatabase target) {
        Map<AttributeRef, Set<IValue>> result = new HashMap<AttributeRef, Set<IValue>>();
        ITupleIterator tuples = selectionOperator.execute(source, target);
        while (tuples.hasNext()) {
            Tuple tuple = tuples.next();
            int i = 0;
            for (Cell cell : tuple.getCells()) {
                if (cell.isOID()) {
                    continue;
                }
                IValue value = cell.getValue();
                Set<IValue> attributeSet = getAttributeSet(result, attributes.get(i));
                attributeSet.add(value);
                i++;
            }
        }
        return result;
    }

    private Set<IValue> getAttributeSet(Map<AttributeRef, Set<IValue>> map, AttributeRef attribute) {
        Set<IValue> result = map.get(attribute);
        if (result == null) {
            result = new HashSet<IValue>();
            map.put(attribute, result);
        }
        return result;
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return attributes;
    }

    public IAlgebraOperator getSelectionOperator() {
        return selectionOperator;
    }

    @Override
    public IAlgebraOperator clone() {
        SelectIn clone = (SelectIn) super.clone();
        clone.selectionOperator = (Scan) selectionOperator.clone();
        return clone;
    }

    class SelectInTupleIterator implements ITupleIterator {

        private ITupleIterator tableIterator;
        private Tuple nextTuple;
        private Map<AttributeRef, Set<IValue>> valueMap;

        public SelectInTupleIterator(ITupleIterator tableIterator, Map<AttributeRef, Set<IValue>> valueMap) {
            this.valueMap = valueMap;
            this.tableIterator = tableIterator;
        }

        public boolean hasNext() {
            if (nextTuple != null) {
                return true;
            } else {
                loadNextTuple();
                return nextTuple != null;
            }
        }

        private void loadNextTuple() {
            while (tableIterator.hasNext()) {
                Tuple tuple = tableIterator.next();
                if (conditionsAreTrue(tuple)) {
                    nextTuple = tuple;
                    return;
                }
            }
            nextTuple = null;
        }

        private boolean conditionsAreTrue(Tuple tuple) {
            if (valueMap.keySet().isEmpty()) {
                return false;
            }
            for (AttributeRef attributeRef : valueMap.keySet()) {
                IValue valueToCheck = tuple.getCell(attributeRef).getValue();
                Set<IValue> valueToPick = valueMap.get(attributeRef);
                if (!valueToPick.contains(valueToCheck)) {
                    return false;
                }
            }
            return true;
        }

        public Tuple next() {
            if (nextTuple != null) {
                Tuple result = nextTuple;
                nextTuple = null;
                return result;
            }
            return null;
        }

        public void reset() {
            this.tableIterator.reset();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void close() {
            tableIterator.close();
        }
    }
}
