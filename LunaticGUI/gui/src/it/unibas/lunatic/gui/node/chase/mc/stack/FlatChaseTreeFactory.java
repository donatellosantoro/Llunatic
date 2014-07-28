package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class FlatChaseTreeFactory extends ChildFactory<DeltaChaseStep> {

    private DeltaChaseStep root;
    private final Scenario scenario;

    public FlatChaseTreeFactory(DeltaChaseStep root, Scenario s) {
        this.root = root;
        this.scenario = s;
    }

    @Override
    protected boolean createKeys(List<DeltaChaseStep> toPopulate) {
        createKeys(root, toPopulate);
        return true;
    }

    private void createKeys(DeltaChaseStep step, List<DeltaChaseStep> toPopulate) {
        if (step.isLeaf()) {
            toPopulate.add(step);
        } else {
            for (DeltaChaseStep child : step.getChildren()) {
                createKeys(child, toPopulate);
            }
        }
    }

    @Override
    protected Node createNodeForKey(DeltaChaseStep key) {
        return new FlatChaseTreeLeaf(key, scenario);
    }
}
