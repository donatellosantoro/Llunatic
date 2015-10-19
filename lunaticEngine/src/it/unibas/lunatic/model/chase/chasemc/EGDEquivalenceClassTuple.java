package it.unibas.lunatic.model.chase.chasemc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.Cell;
import speedy.model.database.TupleOID;
import speedy.utility.comparator.StringComparator;

public class EGDEquivalenceClassTuple {

    private CellGroup conclusionGroup;
    private Map<BackwardAttribute, CellGroup> backwardCellGroups = new HashMap<BackwardAttribute, CellGroup>();

    public EGDEquivalenceClassTuple(CellGroup conclusionGroup) {
        this.conclusionGroup = conclusionGroup;
    }

    public CellGroup getConclusionGroup() {
        return conclusionGroup;
    }

    public void setCellGroupForBackwardAttribute(BackwardAttribute attribute, CellGroup cellGroup) {
        this.backwardCellGroups.put(attribute, cellGroup);
    }

    public CellGroup getCellGroupForBackwardAttribute(BackwardAttribute attribute) {
        return this.backwardCellGroups.get(attribute);
    }

    public Set<Cell> getAllCells() {
        Set<Cell> result = new HashSet<Cell>();
        result.addAll(conclusionGroup.getAllCells());
        for (CellGroup backwardGroup : backwardCellGroups.values()) {
            result.addAll(backwardGroup.getAllCells());
        }
        return result;
    }

    public Collection<CellGroup> getBackwardCellGroups() {
        return this.backwardCellGroups.values();
    }

    public List<TupleOID> getConclusionTupleOIDs() {
        Set<TupleOID> tupleOIDs = new HashSet<TupleOID>();
        for (CellGroupCell occurrence : conclusionGroup.getOccurrences()) {
            tupleOIDs.add(occurrence.getTupleOID());
        }
        List<TupleOID> sortedTupleOIDs = new ArrayList<TupleOID>(tupleOIDs);
        Collections.sort(sortedTupleOIDs, new StringComparator());
        return sortedTupleOIDs;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final EGDEquivalenceClassTuple other = (EGDEquivalenceClassTuple) obj;
        return this.toString().equals(other.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConclusionGroup: ").append(conclusionGroup).append("\n");
        List<BackwardAttribute> sortedAttributes = new ArrayList<BackwardAttribute>(backwardCellGroups.keySet());
        Collections.sort(sortedAttributes, new StringComparator());
        sb.append("\t\tBackward cell groups: ");
        for (int i = 0; i < sortedAttributes.size(); i++) {
            BackwardAttribute backwardAttribute = sortedAttributes.get(i);
            if (i != 0) sb.append("\n\t\t\t");
            sb.append(backwardAttribute).append(": ").append(backwardCellGroups.get(backwardAttribute));
        }
        return sb.toString();
    }

}
