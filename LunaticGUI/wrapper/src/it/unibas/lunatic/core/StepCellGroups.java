package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.List;

public class StepCellGroups {

    private final List<CellGroup> cellGroups;
    private final List<CellGroup> changedCellGroups;

    public StepCellGroups(List<CellGroup> cellGroups, List<CellGroup> changedCellGroups) {
        this.cellGroups = cellGroups;
        this.changedCellGroups = changedCellGroups;
    }

    public List<CellGroup> getAll() {
        return cellGroups;
    }

    public List<CellGroup> getChangedCellGroups() {
        return changedCellGroups;
    }
}
