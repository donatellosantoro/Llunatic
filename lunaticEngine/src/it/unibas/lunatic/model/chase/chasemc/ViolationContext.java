package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;

public class ViolationContext {

    private CellGroup cellGroup;
    private String chaseMode;
    private List<CellGroup> witnessCellGroups = new ArrayList<CellGroup>();

    public ViolationContext(CellGroup cellGroup, String chaseMode, List<CellGroup> witnessCellGroups) {
        this.cellGroup = cellGroup;
        this.chaseMode = chaseMode;
        this.witnessCellGroups = witnessCellGroups;
    }

    public CellGroup getCellGroup() {
        return cellGroup;
    }

    public String getChaseMode() {
        return chaseMode;
    }

    public List<CellGroup> getWitnessCellGroups() {
        return witnessCellGroups;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ViolationContext other = (ViolationContext) obj;
        if (this.cellGroup != other.cellGroup && (this.cellGroup == null || !this.cellGroup.equals(other.cellGroup))) return false;
        if ((this.chaseMode == null) ? (other.chaseMode != null) : !this.chaseMode.equals(other.chaseMode)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "" + cellGroup + " (chaseMode=" + chaseMode + ")";
    }

    public String toLongString() {
        return super.toString() + " Witness cell groups:\n" + LunaticUtility.printCollection(witnessCellGroups);
    }
}
