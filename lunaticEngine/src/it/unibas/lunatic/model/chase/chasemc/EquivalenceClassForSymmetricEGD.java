package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.utility.SpeedyUtility;
import speedy.utility.comparator.StringComparator;

public class EquivalenceClassForSymmetricEGD {
    
    private Dependency egd;
    private AttributeRef conclusionAttribute;
    private List<BackwardAttribute> attributesToChangeForBackwardChasing;
    private List<EGDEquivalenceClassTuple> allTupleCells = new ArrayList<EGDEquivalenceClassTuple>();
    private Map<IValue, List<EGDEquivalenceClassTuple>> tupleCellsWithSameConclusionValue = new HashMap<IValue, List<EGDEquivalenceClassTuple>>();
    private Map<Cell, List<EGDEquivalenceClassTuple>> tupleCellsForCell = new HashMap<Cell, List<EGDEquivalenceClassTuple>>();
    
    public EquivalenceClassForSymmetricEGD(Dependency egd, AttributeRef conclusionAttribute,
            List<BackwardAttribute> attributesForBackwardChasing) {
        this.egd = egd;
        this.conclusionAttribute = conclusionAttribute;
        this.attributesToChangeForBackwardChasing = attributesForBackwardChasing;
    }
    
    public Dependency getEGD() {
        return egd;
    }
    
    public AttributeRef getConclusionAttribute() {
        return conclusionAttribute;
    }
    
    public List<BackwardAttribute> getAttributesToChangeForBackwardChasing() {
        return attributesToChangeForBackwardChasing;
    }
    
    public Map<IValue, List<EGDEquivalenceClassTuple>> getTupleGroupsWithSameConclusionValue() {
        return tupleCellsWithSameConclusionValue;
    }
    
    public List<EGDEquivalenceClassTuple> getAllTupleCells() {
        return allTupleCells;
    }
    
    public void addTupleCells(EGDEquivalenceClassTuple tupleCells) {
        this.allTupleCells.add(tupleCells);
    }
    
    public List<EGDEquivalenceClassTuple> getTuplesWithConclusionValue(IValue conclusionValue) {
        return this.tupleCellsWithSameConclusionValue.get(conclusionValue);
    }
    
    public void addTupleCellsForValue(IValue value, EGDEquivalenceClassTuple tupleGroup) {
        List<EGDEquivalenceClassTuple> listTupleCellsForValue = tupleCellsWithSameConclusionValue.get(value);
        if (listTupleCellsForValue == null) {
            listTupleCellsForValue = new ArrayList<EGDEquivalenceClassTuple>();
            tupleCellsWithSameConclusionValue.put(value, listTupleCellsForValue);
        }
        listTupleCellsForValue.add(tupleGroup);
    }
    
    public void indexTupleCellsForCell(Cell cell, EGDEquivalenceClassTuple tupleGroup) {
        List<EGDEquivalenceClassTuple> listTupleCellsForCell = tupleCellsForCell.get(cell);
        if (listTupleCellsForCell == null) {
            listTupleCellsForCell = new ArrayList<EGDEquivalenceClassTuple>();
            tupleCellsForCell.put(cell, listTupleCellsForCell);
        }
        listTupleCellsForCell.add(tupleGroup);
    }
    
    public List<EGDEquivalenceClassTuple> getTupleCellsForCell(Cell cell) {
        return this.tupleCellsForCell.get(cell);
    }
    
    public Set<IValue> getAllConclusionValues(){
        return this.tupleCellsWithSameConclusionValue.keySet();
    }
    
    public boolean isEmpty() {
        return this.allTupleCells.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EquivalenceClass for egd=" + egd.getId());
        sb.append("\nConclusion Attribute: ").append(conclusionAttribute);
        sb.append("\nAttributes For Backward Chasing\n");
        for (BackwardAttribute backwardAttribute : attributesToChangeForBackwardChasing) {
            sb.append("\t").append(backwardAttribute).append("\n");
        }
        sb.append("Tuple Groups With Same Conclusion Value: \n");
        List<IValue> keys = new ArrayList<IValue>(tupleCellsWithSameConclusionValue.keySet());
        Collections.sort(keys, new StringComparator());
        for (IValue key : keys) {
            sb.append("\tValue: ").append(key).append("\n");
            sb.append(SpeedyUtility.printCollection(tupleCellsWithSameConclusionValue.get(key), "\t\t")).append("\n");
        }
        return sb.toString();
    }
    
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append("Tuple Cells for Cell: \n");
        List<Cell> keys = new ArrayList<Cell>(tupleCellsForCell.keySet());
        Collections.sort(keys, new StringComparator());
        for (Cell key : keys) {
            sb.append("\tValue: ").append(key).append("\n");
            sb.append(SpeedyUtility.printCollection(tupleCellsForCell.get(key), "\t\t")).append("\n");
        }
        return sb.toString();
    }
}
