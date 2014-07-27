package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.database.IValue;
import java.util.HashMap;
import java.util.Map;

// a group of target cells with equal values for conclusion variables that need to be changed to solve a conflict
public class TargetCellsToChange {

    // all target cells with equal value to change for a forward repair
    private CellGroup cellGroupForForwardRepair;
    // all witness cells from the originating tuples (to be changed for backward repairs)
    private Map<BackwardAttribute, CellGroup> cellGroupsForBackwardAttributes = new HashMap<BackwardAttribute, CellGroup>();
    private boolean suspicious;

    public TargetCellsToChange(IValue value) {
        this.cellGroupForForwardRepair = new CellGroup(value, true);
    }

    public CellGroup getCellGroupForForwardRepair() {
        return cellGroupForForwardRepair;
    }

    public CellGroup getCellGroupForBackwardAttribute(BackwardAttribute attribute, IValue value) {
        CellGroup cellGroup = cellGroupsForBackwardAttributes.get(attribute);
        if (cellGroup == null) {
            cellGroup = new CellGroup(value, true);
            cellGroupsForBackwardAttributes.put(attribute, cellGroup);
        }
        return cellGroup;
    }

    public Map<BackwardAttribute, CellGroup> getCellGroupsForBackwardAttributes() {
        return cellGroupsForBackwardAttributes;
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
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.cellGroupForForwardRepair != null ? this.cellGroupForForwardRepair.hashCode() : 0);
        hash = 61 * hash + (this.cellGroupsForBackwardAttributes != null ? this.cellGroupsForBackwardAttributes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return this.toString().equals(obj.toString());
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        if (getClass() != obj.getClass()) return false;
//        final TupleGroup other = (TupleGroup) obj;
//        if (this.conclusionGroup != other.conclusionGroup && (this.conclusionGroup == null || !this.conclusionGroup.equals(other.conclusionGroup))) return false;
//        if (this.premiseGroups != other.premiseGroups && (this.premiseGroups == null || !this.premiseGroups.equals(other.premiseGroups))) return false;
//        return true;
//    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell group for forward repair:\n\t").append(cellGroupForForwardRepair).append("\n");
        sb.append("Cell groups for backward repairs:\n");
        for (BackwardAttribute backwardAttribute : cellGroupsForBackwardAttributes.keySet()) {
            sb.append(backwardAttribute).append("\t").append(cellGroupsForBackwardAttributes.get(backwardAttribute)).append("\n");
        }
        sb.append((suspicious ? "Suspicious" : ""));
        return sb.toString();
    }
}
