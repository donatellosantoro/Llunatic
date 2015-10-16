package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import java.util.Set;

public class CellGroupScore implements Comparable<CellGroupScore> {

    private CellGroup cellGroup;
    private Set violations;

    public CellGroupScore(CellGroup cellGroup, Set violations) {
        assert (cellGroup.getOccurrences().size() > 0) : "Occurrences cannot be null";
        this.cellGroup = cellGroup;
        this.violations = violations;
    }

    public CellGroup getCellGroup() {
        return cellGroup;
    }

    public Set getViolationContexts() {
        return violations;
    }

    public double getScore() {
        int violationSize = violations.size();
        int occurrenceSize = cellGroup.getOccurrences().size();
        return violationSize / (double) occurrenceSize;
    }

    public int compareTo(CellGroupScore other) {
        if (this.getScore() == other.getScore()) {
            return other.getCellGroup().toString().compareTo(this.getCellGroup().toString());
        }
        if (this.getScore() < other.getScore()) {
            return 1;
        } else { //(this.getScore() > other.getScore())
            return -1;
        }
    }

    @Override
    public String toString() {
        return "CellGroupScore: " + cellGroup + "Violations: \n" + violations + '}';
    }

}
