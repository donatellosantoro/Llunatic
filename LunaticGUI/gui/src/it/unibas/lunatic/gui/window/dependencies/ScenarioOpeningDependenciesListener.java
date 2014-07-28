package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.dependencies.DepRootNode;
import it.unibas.lunatic.gui.node.scenario.ScenarioNode;

public class ScenarioOpeningDependenciesListener extends AbstractListener<LoadedScenario> {

    private ExplorerTopComponent provider;

    @Override
    public void onChange(IModel model, LoadedScenario loadedScenario) {
        if (loadedScenario != null) {
            ScenarioNode scenarioNode = loadedScenario.getNode();
            DepRootNode dependenciesNode = scenarioNode.getDependenciesNode();
            provider.setRootContext(dependenciesNode);
            provider.setDisplayName(dependenciesNode.getDisplayName());
        }
    }

    public void register(ExplorerTopComponent p) {
        this.provider = p;
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }
}
