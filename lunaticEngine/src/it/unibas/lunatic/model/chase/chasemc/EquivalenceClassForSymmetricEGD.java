package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.utility.SpeedyUtility;
import speedy.utility.comparator.StringComparator;

public class EquivalenceClassForSymmetricEGD {
    
    private Dependency egd;
    private AttributeRef conclusionAttribute;
    private List<BackwardAttribute> attributesToChangeForBackwardChasing;
    private List<EGDEquivalenceClassTupleCells> allTupleCells = new ArrayList<EGDEquivalenceClassTupleCells>();
    private Map<IValue, List<EGDEquivalenceClassTupleCells>> tupleCellsWithSameConclusionValue = new HashMap<IValue, List<EGDEquivalenceClassTupleCells>>();
    private Map<Cell, List<EGDEquivalenceClassTupleCells>> tupleCellsForCell = new HashMap<Cell, List<EGDEquivalenceClassTupleCells>>();
    
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
    
    public Map<IValue, List<EGDEquivalenceClassTupleCells>> getTupleGroupsWithSameConclusionValue() {
        return tupleCellsWithSameConclusionValue;
    }
    
    public List<EGDEquivalenceClassTupleCells> getAllTupleCells() {
        return allTupleCells;
    }
    
    public void addTupleCells(EGDEquivalenceClassTupleCells tupleCells) {
        this.allTupleCells.add(tupleCells);
    }
    
    public List<EGDEquivalenceClassTupleCells> getTuplesWithConclusionValue(IValue conclusionValue) {
        return this.tupleCellsWithSameConclusionValue.get(conclusionValue);
    }
    
    public void addTupleCellsForValue(IValue value, EGDEquivalenceClassTupleCells tupleGroup) {
        List<EGDEquivalenceClassTupleCells> listTupleCellsForValue = tupleCellsWithSameConclusionValue.get(value);
        if (listTupleCellsForValue == null) {
            listTupleCellsForValue = new ArrayList<EGDEquivalenceClassTupleCells>();
            tupleCellsWithSameConclusionValue.put(value, listTupleCellsForValue);
        }
        listTupleCellsForValue.add(tupleGroup);
    }
    
    public void indexTupleCellsForCell(Cell cell, EGDEquivalenceClassTupleCells tupleGroup) {
        List<EGDEquivalenceClassTupleCells> listTupleCellsForCell = tupleCellsForCell.get(cell);
        if (listTupleCellsForCell == null) {
            listTupleCellsForCell = new ArrayList<EGDEquivalenceClassTupleCells>();
            tupleCellsForCell.put(cell, listTupleCellsForCell);
        }
        listTupleCellsForCell.add(tupleGroup);
    }
    
    public List<EGDEquivalenceClassTupleCells> getTupleCellsForCell(Cell cell) {
        return this.tupleCellsForCell.get(cell);
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
