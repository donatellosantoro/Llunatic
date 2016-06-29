package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class ChaseStepAncestorFactory extends ChildFactory<DeltaChaseStep> {

    private DeltaChaseStep selectedStep;
    private final Scenario scenario;

    public ChaseStepAncestorFactory(DeltaChaseStep node, Scenario s) {
        this.selectedStep = node;
        this.scenario = s;
    }

    @Override
    protected boolean createKeys(List<DeltaChaseStep> toPopulate) {
        toPopulate.add(selectedStep);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(DeltaChaseStep key) {
        List<Node> stack = new ArrayList<Node>();
        DeltaChaseStep parent = key.getFather();
        if (parent == null) {
            return new Node[0];
        }
        while (!parent.isRoot()) {
            Node n = new ChaseStepAnchestorNode(parent, scenario);
            stack.add(n);
            parent = parent.getFather();
        }
        Node[] result = new Node[stack.size()];
        return stack.toArray(result);
    }
}
