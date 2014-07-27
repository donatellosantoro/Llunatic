/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.model;

import it.unibas.lunatic.BeansModel;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.scenario.PartialOrderFileChangeListener;
import it.unibas.lunatic.gui.action.scenario.ScenarioFileChangeListener;
import it.unibas.lunatic.gui.data.ScenarioDataObject;
import it.unibas.lunatic.gui.node.scenario.ScenarioNode;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
public class LoadedScenario extends BeansModel {

    private ScenarioDataObject dataObject;
    private Scenario scenario;
    private ScenarioFileChangeListener scenarioChangeListener;
    private PartialOrderFileChangeListener partialOrderFileChangeListener;

    public LoadedScenario(ScenarioDataObject dataObject, Scenario scenario, String id) {
        super(id);
        this.dataObject = dataObject;
        this.scenario = scenario;
    }

    public void setPartialOrderFileChangeListener(PartialOrderFileChangeListener partialOrderFileChangeListener) {
        this.partialOrderFileChangeListener = partialOrderFileChangeListener;
    }

    public void setScenarioChangeListener(ScenarioFileChangeListener scenarioChangeListener) {
        this.scenarioChangeListener = scenarioChangeListener;
    }

    public ScenarioDataObject getDataObject() {
        return dataObject;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public DataObject getPartialOrder() {
        return super.get(R.BeanProperty.PARTIAL_ORDER_SCRIPT, DataObject.class);
    }
    private ScenarioNode node;

    public ScenarioNode getNode() {
        if (node == null) {
            node = new ScenarioNode(this);
        }
        return node;
    }
}
