package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.dependencies.StepDepRootNode;
import java.util.Collection;

public class ChaseStepDependenciesListener extends AbstractSelectionListener<ChaseStepNode> {

    private ExplorerTopComponent provider;

    @Override
    protected void onChange(Collection<? extends ChaseStepNode> beans) {
        ChaseStepNode stepNode = getBean(beans);
        if (stepNode != null) {
            StepDepRootNode stepDependenciesNode = stepNode.getStepDependenciesNode();            
            provider.setRootContext(stepDependenciesNode);
            provider.setDisplayName(stepDependenciesNode.getDisplayName());
        }
    }

    public void register(ExplorerTopComponent p) {
        this.provider = p;
        super.registerBean(ChaseStepNode.class);
    }
}
