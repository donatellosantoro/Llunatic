/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.DbNode;
import java.util.ArrayList;
import java.util.List;
import org.openide.loaders.DataObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Antonio Galotta
 */
@NbBundle.Messages({"sourceNode=SOURCE", "targetNode=TARGET"})
public class ScenarioChildFactory extends ChildFactory<LoadedScenario> {

    private LoadedScenario scenario;

    public ScenarioChildFactory(LoadedScenario scenario) {
        this.scenario = scenario;
    }

    @Override
    protected boolean createKeys(List<LoadedScenario> toPopulate) {
        toPopulate.add(scenario);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(LoadedScenario key) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(new DbNode(key.getScenario(), key.getScenario().getSource(), Bundle.sourceNode()));
        nodes.add(new DbNode(key.getScenario(), key.getScenario().getTarget(), Bundle.targetNode()));
        DataObject partialOrder = getPartialOrderScript(key);
        if (partialOrder != null) {
            nodes.add(new PartialOrderNode(partialOrder.getNodeDelegate()));
        }
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }

    private DataObject getPartialOrderScript(LoadedScenario ls) {
        return ls.get(R.BeanProperty.PARTIAL_ORDER_SCRIPT, DataObject.class);
    }
}
