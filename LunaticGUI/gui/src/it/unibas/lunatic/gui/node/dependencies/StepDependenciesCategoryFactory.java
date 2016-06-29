package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckUnsatisfiedDependenciesMC;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

class StepDependenciesCategoryFactory extends ChildFactory<ChaseStepNode> {

    private final ChaseStepNode chaseStep;
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();

    public StepDependenciesCategoryFactory(ChaseStepNode chaseStep) {
        this.chaseStep = chaseStep;
    }

    @Override
    protected boolean createKeys(List<ChaseStepNode> toPopulate) {
        toPopulate.add(chaseStep);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(ChaseStepNode context) {
        CheckUnsatisfiedDependenciesMC checker = cgHelper.getUnsatisfiedDependencyChecker(context.getScenario());
        List<Dependency> unsatisfiedDependencies = checker.findUnsatisfiedDependencies(context.getChaseStep(), context.getChaseStep().getDeltaDB(), context.getScenario());
        List<Node> result = new ArrayList<Node>();
        Node[] nodes = new DepListNode(unsatisfiedDependencies, "unsatisfied", false).getChildren().getNodes();
        for (int i = 0; i < nodes.length; i++) {
            result.add(nodes[i].cloneNode());
        }
        return result.toArray(nodes);
    }
}
