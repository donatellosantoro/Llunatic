package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class DbChildFactory extends ChildFactory<String> {

    private IDatabase db;
    private ChaseStepNode chaseStepNode;
    private DeltaChaseStep chaseStep;
    private Scenario scenario;

    public DbChildFactory(IDatabase db, Scenario scenario) {
        this.db = db;
        this.scenario = scenario;
    }

    public DbChildFactory(IDatabase db, ChaseStepNode chaseStepNode, DeltaChaseStep chaseStep, Scenario scenario) {
        this(db, scenario);
        this.chaseStepNode = chaseStepNode;
        this.chaseStep = chaseStep;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.addAll(db.getTableNames());
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        Node node;
        if (chaseStep != null) {
            node = new TableNode(db.getTable(key), db, chaseStepNode, chaseStep, scenario);
        } else {
            node = new TableNode(db.getTable(key), db, scenario);
        }
        return node;
    }
}
