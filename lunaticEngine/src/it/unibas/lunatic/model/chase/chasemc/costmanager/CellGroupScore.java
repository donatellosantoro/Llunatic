package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Set;

public class CellGroupScore implements Comparable<CellGroupScore> {

    private CellGroup cellGroup;
    private Set violations;
    private Set<Dependency> affectedDependencies;

    public CellGroupScore(CellGroup cellGroup, Set violations, Set<Dependency> affectedDependencies) {
        assert (cellGroup.getOccurrences().size() > 0) : "Occurrences cannot be null";
        this.cellGroup = cellGroup;
        this.violations = violations;
        this.affectedDependencies = affectedDependencies;
    }

    public CellGroup getCellGroup() {
        return cellGroup;
    }

    public Set getViolationContexts() {
        return violations;
    }

    private int getNumberOfAffectedDependencies() {
        return this.affectedDependencies.size();
    }

    public double getRepairedViolationsPerOccurrence() {
        int violationSize = violations.size();
        int occurrenceSize = cellGroup.getOccurrences().size();
        return violationSize / (double) occurrenceSize;
    }

    public int compareTo(CellGroupScore other) {
        //First average number of repaired violations per occurrence
        if (this.getRepairedViolationsPerOccurrence() < other.getRepairedViolationsPerOccurrence()) {
            return 1;
        }
        if (this.getRepairedViolationsPerOccurrence() > other.getRepairedViolationsPerOccurrence()) {
            return -1;
        }
        //Second number of affected dependencies
        if (this.getNumberOfAffectedDependencies() > other.getNumberOfAffectedDependencies()) {
            return 1;
        }
        if (this.getNumberOfAffectedDependencies() < other.getNumberOfAffectedDependencies()) {
            return -1;
        }
        //Finally toString to make this deterministic
        return other.getCellGroup().toString().compareTo(this.getCellGroup().toString());
    }

    @Override
    public String toString() {
        return "CellGroupScore: " + cellGroup + "Violations: \n" + violations + '}';
    }

}
