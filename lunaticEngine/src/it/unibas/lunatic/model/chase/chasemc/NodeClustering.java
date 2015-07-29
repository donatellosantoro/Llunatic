package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeClustering {

    private Map<CellGroupStats, List<DeltaChaseStep>> stepMap = new HashMap<CellGroupStats, List<DeltaChaseStep>>();

    public void addChaseStep(DeltaChaseStep step) {
        if (step.getCellGroupStats() == null) {
            return;
        }
        List<DeltaChaseStep> cluster = this.stepMap.get(step.getCellGroupStats());
        if (cluster == null) {
            cluster = new ArrayList<DeltaChaseStep>();
            this.stepMap.put(step.getCellGroupStats(), cluster);
        }
        LunaticUtility.addIfNotContained(cluster, step);
        if (containsAncestor(cluster)) {
            throw new IllegalArgumentException("Unable to set ancestor and descentant as duplicate. \nCluster: " + cluster + "\nStep:" + step);
        }
        step.setDuplicateNodes(cluster);
    }

    public List<DeltaChaseStep> getCluster(DeltaChaseStep step) {
        return this.stepMap.get(step.getCellGroupStats());
    }

    @Override
    public String toString() {
        return LunaticUtility.printMap(stepMap);
    }

    private boolean containsAncestor(List<DeltaChaseStep> cluster) {
        for (int i = 0; i < cluster.size(); i++) {
            DeltaChaseStep step1 = cluster.get(i);
            for (int j = (i + 1); j < cluster.size(); j++) {
                DeltaChaseStep step2 = cluster.get(j);
                if (step1.getId().startsWith(step2.getId()) || step2.getId().startsWith(step1.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

}
