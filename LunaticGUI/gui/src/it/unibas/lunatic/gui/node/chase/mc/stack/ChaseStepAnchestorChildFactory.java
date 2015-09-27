package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.DbExtractor;
import it.unibas.lunatic.gui.node.DbNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class ChaseStepAnchestorChildFactory extends ChildFactory<DeltaChaseStep> {

    private DeltaChaseStep chaseStep;
    private DbExtractor dbHelper = new DbExtractor();
    private final Scenario scenario;

    ChaseStepAnchestorChildFactory(DeltaChaseStep result, Scenario s) {
        this.chaseStep = result;
        this.scenario = s;
    }

    @Override
    protected boolean createKeys(List<DeltaChaseStep> toPopulate) {
        toPopulate.add(chaseStep);
        return true;
    }

    @Override
    protected Node createNodeForKey(DeltaChaseStep key) {
        IDatabase db = dbHelper.extractDb(key);
        return new DbNode(scenario, db, key.getId(), false);
    }
}
