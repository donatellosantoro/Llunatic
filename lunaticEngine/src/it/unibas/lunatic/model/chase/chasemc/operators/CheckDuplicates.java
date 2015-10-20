package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.NodeClustering;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckDuplicates {

    private static Logger logger = LoggerFactory.getLogger(CheckDuplicates.class);

    public void findDuplicates(DeltaChaseStep chaseStep, Scenario scenario) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Finding duplicates for chase step: " + chaseStep.getId());
        if (!scenario.getConfiguration().isRemoveDuplicates()) {
            return;
        }
        if (chaseStep.getCellGroupStats() == null) {
            if (logger.isDebugEnabled()) logger.debug("No cellGroup stats, returning...");
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Adding node to clusters...");
        ChaseTree chaseTree = chaseStep.getChaseTree();
        NodeClustering clusters = chaseTree.getClusters();
        clusters.addChaseStep(chaseStep);
        chaseStep.setDuplicate(isDuplicate(chaseStep, chaseTree));
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DUPLICATE_TIME, end - start);
    }
    
    public boolean isDuplicate(DeltaChaseStep chaseStep, ChaseTree chaseTree) {
        if (logger.isDebugEnabled()) logger.debug("Checking if " + chaseStep.getId() + " is duplicate...");
        if (logger.isDebugEnabled()) logger.debug("Cluster: " + LunaticUtility.printNodeIds(chaseStep.getDuplicateNodes()));
        List<DeltaChaseStep> duplicates = chaseStep.getDuplicateNodes();
        int position = duplicates.indexOf(chaseStep);
        boolean duplicate = duplicates.size() > 1 && position > 0;
        if (logger.isDebugEnabled()) logger.debug("Result: " + duplicate);
        return duplicate;
    }
}
