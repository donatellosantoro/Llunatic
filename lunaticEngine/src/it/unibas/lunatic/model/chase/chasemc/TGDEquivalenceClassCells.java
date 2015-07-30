package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.database.IValue;
import java.util.HashSet;
import java.util.Set;

public class TGDEquivalenceClassCells implements Cloneable {

    private CellGroup cellGroup;
    private Set<CellGroupCell> newCells = new HashSet<CellGroupCell>();

    public TGDEquivalenceClassCells(IValue value) {
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
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TGDEquivalenceClassCells other = (TGDEquivalenceClassCells) obj;
        boolean result = this.toShortString().equals(other.toShortString());
        return result;
    }

    public TGDEquivalenceClassCells clone() {
        try {
            TGDEquivalenceClassCells clone = (TGDEquivalenceClassCells) super.clone();
            clone.cellGroup = this.cellGroup.clone();
            clone.newCells = new HashSet<CellGroupCell>();
            for (CellGroupCell newCell : this.newCells) {
                clone.newCells.add((CellGroupCell) newCell.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone target cells to insert " + this);
        }
    }
    
    @Override
    public String toString() {
        return toShortString();
//        return "TargetCellsToInsertForTGD{" + "cellGroup=" + cellGroup.toLongString() + ", newCells=\n" + LunaticUtility.printCollection(newCells, "\t") + '}';
    }

    public String toShortString() {
        return cellGroup + " - " + newCells;
    }
}
