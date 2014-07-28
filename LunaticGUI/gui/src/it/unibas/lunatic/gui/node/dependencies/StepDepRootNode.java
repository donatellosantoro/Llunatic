package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

@NbBundle.Messages("NODE_StepDependencies=Unsatisfied dependencies for step ")
public class StepDepRootNode extends AbstractNode {

    public StepDepRootNode(ChaseStepNode chaseStep) {
        super(Children.create(new StepDependenciesCategoryFactory(chaseStep), true));
        setDisplayName(Bundle.NODE_StepDependencies().concat(chaseStep.getChaseStep().getId()));
    }
}
