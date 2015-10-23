package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.text.DecimalFormat;

public class PrintRankedSolutions {

    private final DecimalFormat df = new DecimalFormat("#.##");

    public String toString(ChaseTree chaseTree) {
        return printRankedSolutions(chaseTree, false);
    }

    public String toLongString(ChaseTree chaseTree) {
        return printRankedSolutions(chaseTree, true);
    }

    private String printRankedSolutions(ChaseTree chaseTree, boolean longString) {
        if(!chaseTree.getScenario().getConfiguration().isRemoveDuplicates()){
            return "*** Ranking not available. Please enable removeDuplicates in configuration *** ";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("+++++ RANKED SOLUTIONS +++++").append("\n");
        for (DeltaChaseStep rankedSolution : chaseTree.getRankedSolutions()) {
            sb.append("Solution: ").append(rankedSolution.getId());
            sb.append(" Score: ").append(df.format(rankedSolution.getScore()));
            sb.append(getAdditionalInfo(rankedSolution)).append("\n");
            if (longString) {
                sb.append(OperatorFactory.getInstance().getChaseTreeToString(chaseTree.getScenario()).printStep(rankedSolution, true, false));
            }
        }
        return sb.toString();
    }

    private String getAdditionalInfo(DeltaChaseStep rankedSolution) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ChangedCells: ").append(rankedSolution.getCellGroupStats().changedCells);
        sb.append(" LlunCellGroups: ").append(rankedSolution.getCellGroupStats().llunCellGroups);
        return sb.toString();
    }

}
