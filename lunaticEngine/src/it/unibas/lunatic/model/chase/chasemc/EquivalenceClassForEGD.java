package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.utility.SpeedyUtility;
import speedy.utility.comparator.StringComparator;

public class EquivalenceClassForEGD {

    private DependencyVariables dependencyVariables;
    private List<ViolationContext> violationContexts = new ArrayList<ViolationContext>();
    private Map<Cell, List<ViolationContext>> violationContextsForCell = new HashMap<Cell, List<ViolationContext>>();
    private Map<IValue, Set<CellGroup>> cellGroupsForValue = new HashMap<IValue, Set<CellGroup>>();

    public EquivalenceClassForEGD(DependencyVariables dependencyVariables) {
        this.dependencyVariables = dependencyVariables;
    }

    public Dependency getEGD() {
        return dependencyVariables.getEgd();
    }

    public DependencyVariables getDependencyVariables() {
        return dependencyVariables;
    }

    public List<ViolationContext> getViolationContexts() {
        return violationContexts;
    }

    public void addViolationContext(ViolationContext violationContext) {
        this.violationContexts.add(violationContext);
    }

    public void addViolationContextForCell(Cell cell, ViolationContext violationContext) {
        List<ViolationContext> contexts = violationContextsForCell.get(cell);
        if (contexts == null) {
            contexts = new ArrayList<ViolationContext>();
            violationContextsForCell.put(cell, contexts);
        }
        contexts.add(violationContext);
    }

    public List<ViolationContext> getViolationContextsForCell(Cell cell) {
        return violationContextsForCell.get(cell);
    }

    public void addCellGroupForValue(IValue value, CellGroup cell) {
        Set<CellGroup> cells = cellGroupsForValue.get(value);
        if (cells == null) {
            cells = new HashSet<CellGroup>();
            cellGroupsForValue.put(value, cells);
        }
        cells.add(cell);
    }

    public Set<CellGroup> getCellGroupsForValue(IValue value) {
        return cellGroupsForValue.get(value);
    }

    public List<CellGroup> getAllConclusionCellGroups() {
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (Set<CellGroup> value : cellGroupsForValue.values()) {
            result.addAll(value);
        }
        return result;
    }

    public Set<IValue> getAllConclusionValues() {
        return this.cellGroupsForValue.keySet();
    }

    public boolean isEmpty() {
        return this.violationContexts.isEmpty();
    }

    public int getSize() {
        return this.violationContexts.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EquivalenceClass for egd ").append(dependencyVariables).append(":\n");
        sb.append(SpeedyUtility.printCollection(violationContexts)).append("\n");
        sb.append("Cell Index: \n");
        List<Cell> sortedCells = new ArrayList<Cell>(this.violationContextsForCell.keySet());
        Collections.sort(sortedCells, new StringComparator());
        for (Cell cell : sortedCells) {
            sb.append("\t").append(cell).append(": ");
            for (ViolationContext context : this.violationContextsForCell.get(cell)) {
                sb.append(context.toShortString()).append(" ");
            }
            sb.append("\n");
        }
        sb.append("Value Index:\n");
        List<IValue> sortedValues = new ArrayList<IValue>(this.cellGroupsForValue.keySet());
        Collections.sort(sortedValues, new StringComparator());
        for (IValue value : sortedValues) {
            sb.append("\t").append(value).append(": ");
            for (CellGroup cellGroup : this.cellGroupsForValue.get(value)) {
                sb.append(cellGroup.toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
