package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.database.IValue;
import java.util.HashSet;
import java.util.Set;

public class TargetCellsToInsertForTGD {

    private CellGroup cellGroup;
    private Set<CellGroupCell> newCells = new HashSet<CellGroupCell>();

    public TargetCellsToInsertForTGD(IValue value) {
        this.cellGroup = new CellGroup(value, true);
    }

    public void setCellGroup(CellGroup cellGroup) {
        this.cellGroup = cellGroup;
    }

    public void addNewCell(CellGroupCell newCell) {
        this.newCells.add(newCell);
    }

    public CellGroup getCellGroup() {
        return cellGroup;
    }

    public Set<CellGroupCell> getNewCells() {
        return newCells;
    }

    public boolean hasNewCells() {
        return !this.newCells.isEmpty();
    }

    @Override
    public String toString() {
        return toShortString();
//        return "TargetCellsToInsertForTGD{" + "cellGroup=" + cellGroup.toLongString() + ", newCells=\n" + LunaticUtility.printCollection(newCells, "\t") + '}';
    }

    public String toShortString() {
        return cellGroup + " - " + newCells;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TargetCellsToInsertForTGD other = (TargetCellsToInsertForTGD) obj;
        boolean result = this.toShortString().equals(other.toShortString());
        return result;
    }

}
