package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViolationContext {

    private int id;
    private Map<VariableEquivalenceClass, Set<CellGroup>> witnessCells = new HashMap<VariableEquivalenceClass, Set<CellGroup>>();
    private Map<VariableEquivalenceClass, CellGroup> conclusionCells = new HashMap<VariableEquivalenceClass, CellGroup>();

    public ViolationContext(int id) {
        this.id = id;
    }

    public Set<CellGroup> getWitnessCellsForVariable(VariableEquivalenceClass variable) {
        return this.witnessCells.get(variable);
    }

    public Set<VariableEquivalenceClass> getWitnessVariables() {
        return witnessCells.keySet();
    }

    public CellGroup getCellGroupForConclusionVariable(VariableEquivalenceClass variable) {
        return this.conclusionCells.get(variable);
    }

    public Set<VariableEquivalenceClass> getConclusionVariables() {
        return conclusionCells.keySet();
    }

    public void setCellGroupsForWitnessVariable(VariableEquivalenceClass variable, Set<CellGroup> cells) {
        this.witnessCells.put(variable, cells);
    }

    public void setCellGroupForConclusionVariable(VariableEquivalenceClass variable, CellGroup cellGroup) {
        this.conclusionCells.put(variable, cellGroup);
    }

    public Collection<CellGroup> getAllConclusionGroups() {
        return this.conclusionCells.values();
    }

    public Set<CellGroup> getAllWitnessCellGroups() {
        Set<CellGroup> result = new HashSet<CellGroup>();
        for (Set<CellGroup> cellGroups : witnessCells.values()) {
            result.addAll(cellGroups);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ViolationContext other = (ViolationContext) obj;
        if (this.id != other.id) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ViolationContext: " + id
                + "\n\tWitness Cells: " + witnessCells
                + "\n\tConclusion Cells: " + conclusionCells;
    }

    public String toShortString() {
        return "" + id;
    }

}
