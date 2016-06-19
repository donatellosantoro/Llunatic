package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.node.dependencies.DepRootNode;
import it.unibas.lunatic.gui.node.scenario.ScenarioNode;
import java.util.Collection;

public class ScenarioNodeDependenciesListener extends AbstractSelectionListener<ScenarioNode> {

    private ExplorerTopComponent provider;

    @Override
    protected void onChange(Collection<? extends ScenarioNode> beans) {
        ScenarioNode scenarioNode = getBean(beans);
        if (scenarioNode != null) {
            DepRootNode dependenciesNode = scenarioNode.getDependenciesNode();
            provider.setRootContext(dependenciesNode);
            provider.setDisplayName(dependenciesNode.getDisplayName());
        }
    }

    public void register(ExplorerTopComponent p) {
        this.provider = p;
        super.registerBean(ScenarioNode.class);
    }
}
