package it.unibas.lunatic.model.chase.chasemc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.utility.comparator.StringComparator;

// a group of target cells with equal values for conclusion variables that need to be changed to solve a conflict
public class EGDEquivalenceClassCells {

    // all target cells with equal value to change for a forward repair. Initially incomplete
    private CellGroup cellGroupForForwardRepair;
    // all witness cells from the originating tuples (to be changed for backward repairs)
    private Map<BackwardAttribute, Set<Cell>> witnessCells = new HashMap<BackwardAttribute, Set<Cell>>();
    // witness cells generate sets of cell groups
    private Map<BackwardAttribute, Set<CellGroup>> cellGroupsForBackwardRepair = new HashMap<BackwardAttribute, Set<CellGroup>>();
    private boolean suspicious;

    public EGDEquivalenceClassCells(IValue value) {
        this.cellGroupForForwardRepair = new CellGroup(value, true);
    }

    public CellGroup getCellGroupForForwardRepair() {
        return cellGroupForForwardRepair;
    }

    public Set<Cell> getOrCreateCellsForBackwardRepair(BackwardAttribute attribute) {
        Set<Cell> cells = witnessCells.get(attribute);
        if (cells == null) {
            cells = new HashSet<Cell>();
            witnessCells.put(attribute, cells);
        }
        return cells;
    }

    public void addCellGroupsForBackwardRepair(BackwardAttribute attribute, Set<CellGroup> cellGroups) {
        this.cellGroupsForBackwardRepair.put(attribute, cellGroups);
    }

    public Map<BackwardAttribute, Set<Cell>> getWitnessCells() {
        return witnessCells;
    }

    public void setCellGroupForForwardRepair(CellGroup cellGroupForForwardRepair) {
        this.cellGroupForForwardRepair = cellGroupForForwardRepair;
    }

    public void setCellsForBackwardRepair(BackwardAttribute backwardAttribute, Set<Cell> cells) {
        this.witnessCells.put(backwardAttribute, cells);
    }

    public Map<BackwardAttribute, Set<CellGroup>> getCellGroupsForBackwardRepair() {
        return cellGroupsForBackwardRepair;
    }

    public void setCellGroupsForBackwardRepair(Map<BackwardAttribute, Set<CellGroup>> cellGroupsForBackwardRepair) {
        this.cellGroupsForBackwardRepair = cellGroupsForBackwardRepair;
    }

    public int getOccurrenceSize() {
        return cellGroupForForwardRepair.getOccurrences().size();
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return this.toString("");
    }

    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Cell group for fwd repair: ").append(cellGroupForForwardRepair).append("\n");
        sb.append(indent).append("Cells for bkw repairs:").append((suspicious ? " (Suspicious) " : "")).append("\n");
        List<BackwardAttribute> keys = new ArrayList<BackwardAttribute>(witnessCells.keySet());
        Collections.sort(keys, new StringComparator());
        for (BackwardAttribute backwardAttribute : keys) {
            sb.append(indent).append(backwardAttribute).append("\t").append(witnessCells.get(backwardAttribute)).append("\n");
        }
        return sb.toString();
    }

}
