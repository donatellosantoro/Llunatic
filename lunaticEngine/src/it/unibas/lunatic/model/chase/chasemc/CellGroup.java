package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;

public class CellGroup implements Cloneable, Serializable {

    private IValue value;
    private IValue id;
    private Set<CellGroupCell> occurrences = new HashSet<CellGroupCell>();
    private Set<CellGroupCell> justifications = new HashSet<CellGroupCell>();
    private Set<CellGroupCell> userCells = new HashSet<CellGroupCell>();
    private CellGroupCell invalidCell;
    private Map<AttributeRef, Set<CellGroupCell>> additionalCells = new HashMap<AttributeRef, Set<CellGroupCell>>();

    public CellGroup(IValue value, boolean newCellGroup) {
        if (newCellGroup) {
            setValue(value);
        } else {
            setId(value);
        }
    }

    public void addOccurrenceCell(CellGroupCell cell) {
        this.occurrences.add(cell);
    }

    public void addUserCell(CellGroupCell cell) {
        this.userCells.add(cell);
    }

    public IValue getValue() {
        return value;
    }

    public IValue getId() {
        return this.id;
    }

    public Set<CellGroupCell> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<CellGroupCell> occurrences) {
        this.occurrences = occurrences;
    }

    public Set<CellGroupCell> getJustifications() {
        return justifications;
    }

    public void setJustifications(Set<CellGroupCell> justifications) {
        this.justifications = justifications;
    }

    public void addJustificationCell(CellGroupCell cell) {
        this.justifications.add(cell);
    }

    public Set<CellGroupCell> getUserCells() {
        return userCells;
    }

    public void setUserCells(Set<CellGroupCell> userCells) {
        this.userCells = userCells;
    }

    public void setInvalidCell(CellGroupCell invalidCell) {
        this.invalidCell = invalidCell;
    }

    public boolean hasInvalidCell() {
        return this.invalidCell != null;
    }

    public Map<AttributeRef, Set<CellGroupCell>> getAdditionalCells() {
        return additionalCells;
    }

    public void setAdditionalCells(Map<AttributeRef, Set<CellGroupCell>> additionalCells) {
        this.additionalCells = additionalCells;
    }

    public final void setId(IValue value) {
        assert (value instanceof LLUNValue || value instanceof NullValue || value.toString().contains(SpeedyConstants.VALUE_LABEL)) : "Trying to build a cell group with value in place of id: " + value + " (" + value.getClass().getName() + ")";
        this.id = value;
        this.value = CellGroupIDGenerator.getCellGroupValueFromGroupID(value);
    }

    public final void setValue(IValue value) {
        assert (!value.toString().contains(SpeedyConstants.VALUE_LABEL)) : "Trying to build a cell group with id in place of value: " + value;
        this.value = value;
        this.id = CellGroupIDGenerator.generateNewId(value);
    }

    public void addAdditionalCell(AttributeRef additionalAttribute, CellGroupCell additionalCell) {
        Set<CellGroupCell> cells = this.additionalCells.get(additionalAttribute);
        if (cells == null) {
            cells = new HashSet<CellGroupCell>();
            this.additionalCells.put(additionalAttribute, cells);
        }
        cells.add(additionalCell);
    }

    public void addAllAdditionalCells(Map<AttributeRef, Set<CellGroupCell>> additionalCells) {
        for (AttributeRef attributeRef : additionalCells.keySet()) {
            for (CellGroupCell cell : additionalCells.get(attributeRef)) {
                this.addAdditionalCell(attributeRef, cell);
            }
        }
    }

    public Set<CellGroupCell> getAuthoritativeJustifications() {
        Set<CellGroupCell> result = new HashSet<CellGroupCell>();
        for (CellGroupCell justification : this.justifications) {
            if (justification.isAuthoritative()) {
                result.add(justification);
            }
        }
        return result;
    }

    public Set<CellGroupCell> getNonAuthoritativeJustifications() {
        Set<CellGroupCell> result = new HashSet<CellGroupCell>();
        for (CellGroupCell justification : this.justifications) {
            if (!justification.isAuthoritative()) {
                result.add(justification);
            }
        }
        return result;
    }

    public CellGroupCell getInvalidCell() {
        return this.invalidCell;
    }

    public Set<CellGroupCell> getAllCells() {
        Set<CellGroupCell> result = new HashSet<CellGroupCell>();
        result.addAll(this.occurrences);
        result.addAll(this.justifications);
        result.addAll(this.userCells);
        if (hasInvalidCell()) {
            result.add(this.invalidCell);
        }
        return result;
    }

    @Override
    public CellGroup clone() {
        try {
            CellGroup c = (CellGroup) super.clone();
            c.occurrences = new HashSet<CellGroupCell>();
            for (CellGroupCell occurrence : occurrences) {
                c.occurrences.add((CellGroupCell) occurrence.clone());
            }
            c.justifications = new HashSet<CellGroupCell>();
            for (CellGroupCell just : justifications) {
                c.justifications.add((CellGroupCell) just.clone());
            }
            c.userCells = new HashSet<CellGroupCell>();
            for (CellGroupCell user : userCells) {
                c.userCells.add((CellGroupCell) user.clone());
            }
            if (this.hasInvalidCell()) {
                c.invalidCell = (CellGroupCell) this.invalidCell.clone();
            }
            c.additionalCells = new HashMap<AttributeRef, Set<CellGroupCell>>();
            for (AttributeRef key : this.additionalCells.keySet()) {
                Set<CellGroupCell> additionalCellsForAttribute = new HashSet<CellGroupCell>();
                for (CellGroupCell additionalCell : this.additionalCells.get(key)) {
                    additionalCellsForAttribute.add((CellGroupCell) additionalCell.clone());
                }
                c.additionalCells.put(key, additionalCellsForAttribute);
            }
            return c;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 23 * hash + (this.occurrences != null ? this.occurrences.hashCode() : 0);
        hash = 23 * hash + (this.justifications != null ? this.justifications.hashCode() : 0);
        hash = 23 * hash + (this.userCells != null ? this.userCells.hashCode() : 0);
//        hash = 23 * hash + (this.invalidCell != null ? this.invalidCell.hashCode() : 0);
        hash = 23 * hash + (this.invalidCell != null ? LunaticConstants.TYPE_INVALID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CellGroup other = (CellGroup) obj;
        if (this.value != other.value && (this.value == null || other.value == null)) return false;
        if (!this.value.getType().equals(other.value.getType())) return false;
        if (this.value instanceof ConstantValue && !this.value.equals(other.value)) return false;
        if (this.occurrences != other.occurrences && (this.occurrences == null || !this.occurrences.equals(other.occurrences))) return false;
        if (this.justifications != other.justifications && (this.justifications == null || !this.justifications.equals(other.justifications))) return false;
        if (this.userCells != other.userCells && (this.userCells == null || !this.userCells.equals(other.userCells))) return false;
//        if (this.invalidCell != other.invalidCell && (this.invalidCell == null || !this.invalidCell.equals(other.invalidCell))) return false;
        if (this.hasInvalidCell() != other.hasInvalidCell()) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + "v=").append(value);
        if (!occurrences.isEmpty()) {
            sb.append(" occ:").append(occurrences);
        }
        if (!justifications.isEmpty()) {
            sb.append(" just:").append(justifications);
        }
        if (!userCells.isEmpty()) {
            sb.append(" user:").append(userCells);
        }
        if (hasInvalidCell()) {
            sb.append(" ").append(LunaticConstants.TYPE_INVALID);
        }
        sb.append('>');
        return sb.toString();
    }

    public String toStringWithAdditionalCells() {
        StringBuilder sb = new StringBuilder();
        sb.append(toLongString());
        if (!additionalCells.isEmpty()) {
            sb.append("\n\tadditional:\n");
            for (AttributeRef attributeRef : additionalCells.keySet()) {
                sb.append("\t\t").append(attributeRef).append(": ").append(additionalCells.get(attributeRef)).append("\n");
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + "v=").append(value);
        sb.append(" id=").append(id);
        sb.append(" occ: [").append((occurrences.isEmpty() ? "" : "\n"));
        for (CellGroupCell occurrence : occurrences) {
            sb.append("\t").append(occurrence.toLongString()).append("\n");
        }
        sb.append("]").append((occurrences.isEmpty() ? "" : "\n"));
        sb.append(" just: [").append((justifications.isEmpty() ? "" : "\n"));
        for (CellGroupCell just : justifications) {
            sb.append("\t").append(just.toLongString()).append("\n");
        }
        sb.append("]").append((justifications.isEmpty() ? "" : "\n"));
        sb.append(" user: [").append((userCells.isEmpty() ? "" : "\n"));
        for (CellGroupCell user : userCells) {
            sb.append("\t").append(user.toLongString()).append("\n");
        }
        sb.append("]").append((userCells.isEmpty() ? "" : "\n"));
        if (hasInvalidCell()) {
            sb.append(" invalid");
        }
        sb.append('>');
        return sb.toString();
    }

}
