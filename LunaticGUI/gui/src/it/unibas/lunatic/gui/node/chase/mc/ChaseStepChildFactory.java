/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.mc;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.DbExtractor;
import it.unibas.lunatic.gui.node.DbNode;
import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.IDatabase;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
public class ChaseStepChildFactory extends ChildFactory<DeltaChaseStep> {

    private DeltaChaseStep chaseStep;
    private DbExtractor dbHelper = new DbExtractor();
    private final Scenario scenario;

    public ChaseStepChildFactory(DeltaChaseStep result, Scenario s) {
        this.chaseStep = result;
        this.scenario = s;
    }

    @Override
    protected boolean createKeys(List<DeltaChaseStep> toPopulate) {
        toPopulate.add(chaseStep);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(DeltaChaseStep key) {
        ArrayList<Node> nodes = new ArrayList<Node>(chaseStep.getNumberOfNodes() + 1);
        if (!key.isRoot()) {
            IDatabase db = dbHelper.extractDb(key);
            nodes.add(new DbNode(scenario, db, key, false));
        }        
        for (DeltaChaseStep child : key.getChildren()) {
            nodes.add(new ChaseStepNode(child, scenario));
        }
        Node[] children = new Node[nodes.size()];
        return nodes.toArray(children);
    }
}
