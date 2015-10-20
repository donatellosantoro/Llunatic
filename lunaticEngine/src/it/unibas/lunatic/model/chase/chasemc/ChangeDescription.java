package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashSet;
import java.util.Set;
import speedy.model.database.Cell;

public class ChangeDescription {

    private CellGroup cellGroup;
    private String chaseMode;
    private Set<Cell> witnessCells = new HashSet<Cell>();

    public ChangeDescription(CellGroup cellGroup, String chaseMode) {
        this.cellGroup = cellGroup;
        this.chaseMode = chaseMode;
    }

    public ChangeDescription(CellGroup cellGroup, String chaseMode, Set<Cell> witnessCells) {
        this(cellGroup, chaseMode);
        this.witnessCells = witnessCells;
    }

    public CellGroup getCellGroup() {
        return cellGroup;
    }

    public String getChaseMode() {
        return chaseMode;
    }

    public Set<Cell> getWitnessCells() {
        return witnessCells;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ChangeDescription other = (ChangeDescription) obj;
        if (this.cellGroup != other.cellGroup && (this.cellGroup == null || !this.cellGroup.equals(other.cellGroup))) return false;
        if ((this.chaseMode == null) ? (other.chaseMode != null) : !this.chaseMode.equals(other.chaseMode)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "" + cellGroup + " (chaseMode=" + chaseMode + ")";
    }

    public String toLongString() {
        return super.toString() + " Witness cell groups:\n" + LunaticUtility.printCollection(witnessCells);
    }
}
