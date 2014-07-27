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
        step.setDuplicateNodes(cluster);
    }
    
    public List<DeltaChaseStep> getCluster(DeltaChaseStep step) {
        return this.stepMap.get(step.getCellGroupStats());
    }

    @Override
    public String toString() {
        return LunaticUtility.printMap(stepMap);
    }
    
}
