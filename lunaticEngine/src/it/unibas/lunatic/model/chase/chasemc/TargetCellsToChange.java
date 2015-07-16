package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.database.IValue;
import java.util.HashMap;
import java.util.Map;

// a group of target cells with equal values for conclusion variables that need to be changed to solve a conflict
public class TargetCellsToChange {

    // all target cells with equal value to change for a forward repair. Initially incomplete
    private CellGroup cellGroupForForwardRepair;
    // all witness cells from the originating tuples (to be changed for backward repairs). Initially incomplete
    private Map<BackwardAttribute, CellGroup> cellGroupsForBackwardRepairs = new HashMap<BackwardAttribute, CellGroup>();
    private boolean suspicious;

    public TargetCellsToChange(IValue value) {
        this.cellGroupForForwardRepair = new CellGroup(value, true);
    }

    public CellGroup getCellGroupForForwardRepair() {
        return cellGroupForForwardRepair;
    }

    public CellGroup getOrCreateCellGroupForBackwardRepair(BackwardAttribute attribute, IValue value) {
        CellGroup cellGroup = cellGroupsForBackwardRepairs.get(attribute);
        if (cellGroup == null) {
            cellGroup = new CellGroup(value, true);
            cellGroupsForBackwardRepairs.put(attribute, cellGroup);
        }
        return cellGroup;
    }

    public Map<BackwardAttribute, CellGroup> getCellGroupsForBackwardRepairs() {
        return cellGroupsForBackwardRepairs;
    }

    public void setCellGroupForForwardRepair(CellGroup cellGroupForForwardRepair) {
        this.cellGroupForForwardRepair = cellGroupForForwardRepair;
    }

    public void setCellGroupForBackwardRepair(BackwardAttribute backwardAttribute, CellGroup cellGroup){
        this.cellGroupsForBackwardRepairs.put(backwardAttribute, cellGroup);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Cell group for forward repair:\n\t").append(cellGroupForForwardRepair).append("\n");
        sb.append("Cell groups for backward repairs:\n");
        for (BackwardAttribute backwardAttribute : cellGroupsForBackwardRepairs.keySet()) {
            sb.append(backwardAttribute).append("\t").append(cellGroupsForBackwardRepairs.get(backwardAttribute)).append("\n");
        }
        sb.append((suspicious ? "Suspicious" : ""));
        return sb.toString();
    }

}
