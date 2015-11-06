package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class RankedSolutionsTreeFactory extends ChildFactory<DeltaChaseStep> {

    private ChaseTree chaseTree;
    private final Scenario scenario;

    public RankedSolutionsTreeFactory(ChaseTree chaseTree, Scenario s) {
        this.chaseTree = chaseTree;
        this.scenario = s;
    }

    @Override
    protected boolean createKeys(List<DeltaChaseStep> toPopulate) {
        if (chaseTree.getRankedSolutions() == null) {
            return true;
        }
        for (DeltaChaseStep rankedSolution : chaseTree.getRankedSolutions()) {
            toPopulate.add(rankedSolution);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DeltaChaseStep key) {
        return new FlatChaseTreeLeaf(key, scenario, true);
    }
}
