package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StepCellGroups {

    private final Set<CellGroup> cellGroups;
    private final Set<CellGroup> changedCellGroups;

    public StepCellGroups(List<CellGroup> cellGroups, List<CellGroup> changedCellGroups) {
        this.cellGroups = new HashSet<CellGroup>(cellGroups);
        this.changedCellGroups = new HashSet<CellGroup>(changedCellGroups);
    }

    public Set<CellGroup> getAll() {
        return cellGroups;
    }

    public Set<CellGroup> getChangedCellGroups() {
        return changedCellGroups;
    }
}
