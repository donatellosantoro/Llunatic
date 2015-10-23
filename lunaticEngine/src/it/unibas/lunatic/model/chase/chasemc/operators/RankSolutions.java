package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankSolutions {

    public void rankSolutions(ChaseTree chaseTree) {
        List<DeltaChaseStep> solutions = extractSolutions(chaseTree);
        double numberCellsWeight = chaseTree.getScenario().getConfiguration().getNumberCellsWeightForRanking();
        for (DeltaChaseStep solution : solutions) {
            assert (solution.getCellGroupStats() != null) : "No CellGroupStats in solution " + solution.toLongString();
            double rank = computeScore(solution, numberCellsWeight);
            solution.setScore(rank);
        }
        Collections.sort(solutions, new RankSolutionComparator());
        chaseTree.setRankedSolutions(solutions);
    }

    private List<DeltaChaseStep> extractSolutions(ChaseTree chaseTree) {
        List<DeltaChaseStep> solutions = new ArrayList<DeltaChaseStep>();
        visitForSolutions(chaseTree.getRoot(), solutions);
        return solutions;
    }

    private void visitForSolutions(DeltaChaseStep step, List<DeltaChaseStep> solutions) {
        if (step.isSolution()) {
            solutions.add(step);
            return;
        }
        for (DeltaChaseStep children : step.getChildren()) {
            visitForSolutions(children, solutions);
        }
    }

    private double computeScore(DeltaChaseStep solution, double numberCellsWeight) {
        int numberOfChangedCells = solution.getCellGroupStats().changedCells;
        int numberOfLluns = solution.getCellGroupStats().nullCellGroups;
        return numberOfChangedCells * numberCellsWeight + numberOfLluns * (1.0 - numberCellsWeight);
    }

}

class RankSolutionComparator implements Comparator<DeltaChaseStep> {

    public int compare(DeltaChaseStep o1, DeltaChaseStep o2) {
        return new Double(o1.getScore()).compareTo(new Double(o2.getScore()));
    }
}
