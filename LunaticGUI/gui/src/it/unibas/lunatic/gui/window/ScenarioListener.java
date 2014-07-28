package it.unibas.lunatic.gui.window;

import it.unibas.lunatic.AbstractListener;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.scenario.ScenarioNode;

public class ScenarioListener extends AbstractListener<LoadedScenario> {

    private ExplorerTopComponent provider;

    @Override
    public void onChange(IModel m, LoadedScenario ls) {
        if (ls != null) {
            provider.setRootContext(ls.getNode());
        } else {
            provider.removeRootContext();
        }
        logger.debug("Scenario explorer updated");
    }

    public void register(ExplorerTopComponent p) {
        this.provider = p;
        super.registerBean(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
    }
}
