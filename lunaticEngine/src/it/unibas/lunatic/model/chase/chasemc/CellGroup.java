package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CellGroup implements Cloneable, Serializable {

    private IValue value;
    private IValue id;
//    private Set<CellRef> occurrences = Collections.newSetFromMap(new ConcurrentHashMap<CellRef, Boolean>());
//    private Set<Cell> provenances = Collections.newSetFromMap(new ConcurrentHashMap<Cell, Boolean>());
//    private Map<AttributeRef, Set<Cell>> additionalCells = new ConcurrentHashMap<AttributeRef, Set<Cell>>();
    private Set<CellRef> occurrences = new HashSet<CellRef>();
    private Set<Cell> provenances = new HashSet<Cell>();
    private Map<AttributeRef, Set<Cell>> additionalCells = new HashMap<AttributeRef, Set<Cell>>();

    public CellGroup(IValue value, boolean newCellGroup) {
        if (newCellGroup) {
            setValue(value);
        } else {
            setId(value);
        }
    }

    public void addOccurrenceCell(CellRef cellRef) {
        this.occurrences.add(cellRef);
    }

    public IValue getValue() {
        return value;
    }

    public IValue getId() {
        return this.id;
    }

    public final void setId(IValue value) {
        assert (value instanceof LLUNValue || value instanceof NullValue || value.toString().contains(LunaticConstants.VALUE_LABEL)) : "Trying to build a cell group with value in place of id: " + value;
        this.id = value;
        this.value = CellGroupIDGenerator.getValue(value);
    }

    public final void setValue(IValue value) {
        assert (!value.toString().contains(LunaticConstants.VALUE_LABEL)) : "Trying to build a cell group with id in place of value: " + value;
        this.value = value;
        this.id = CellGroupIDGenerator.generateNewId(value);
    }

    public Set<CellRef> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<CellRef> occurrences) {
        this.occurrences = occurrences;
    }

    public Set<Cell> getProvenances() {
        return provenances;
    }

    public void setProvenances(Set<Cell> provenances) {
        this.provenances = provenances;
    }

    public void addProvenanceCell(Cell cell) {
        this.provenances.add(cell);
    }

    public Map<AttributeRef, Set<Cell>> getAdditionalCells() {
        return additionalCells;
    }

    public void addAdditionalCell(AttributeRef additionalAttribute, Cell additionalCell) {
        Set<Cell> cells = this.additionalCells.get(additionalAttribute);
        if (cells == null) {
            cells = new HashSet<Cell>();
            this.additionalCells.put(additionalAttribute, cells);
        }
        cells.add(additionalCell);
    }

    public void addAllAdditionalCells(Map<AttributeRef, Set<Cell>> additionalCells) {
        for (AttributeRef attributeRef : additionalCells.keySet()) {
            for (Cell cell : additionalCells.get(attributeRef)) {
                this.addAdditionalCell(attributeRef, cell);
            }
        }
    }

    @Override
    public CellGroup clone() {
        try {
            CellGroup c = (CellGroup) super.clone();
            c.occurrences = new HashSet<CellRef>(this.occurrences);
            c.provenances = new HashSet<Cell>(this.provenances);
            c.additionalCells = new HashMap<AttributeRef, Set<Cell>>();
            for (AttributeRef key : this.additionalCells.keySet()) {
                c.additionalCells.put(key, new HashSet<Cell>(this.additionalCells.get(key)));
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
        hash = 23 * hash + (this.provenances != null ? this.provenances.hashCode() : 0);
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
        if (this.provenances != other.provenances && (this.provenances == null || !this.provenances.equals(other.provenances))) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + "v=").append(value);
        if (!occurrences.isEmpty()) {
//            sb.append("\n\tocc=").append(occurrences);
            sb.append(" occ:").append(occurrences);
        }
        if (!provenances.isEmpty()) {
//            sb.append("\n\tprov=").append(provenances);
            sb.append(" just:").append(provenances);
        }
        sb.append('>');
        return sb.toString();
    }

    public String toStringWithAdditionalCells() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        if (!additionalCells.isEmpty()) {
            sb.append("\n\tadditional:\n");
            for (AttributeRef attributeRef : additionalCells.keySet()) {
                sb.append("\t\t").append(attributeRef).append(": ").append(additionalCells.get(attributeRef)).append("\n");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
