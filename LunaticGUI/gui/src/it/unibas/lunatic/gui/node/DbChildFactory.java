package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.IDatabase;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class DbChildFactory extends ChildFactory<String> {

    private IDatabase db;
    private DeltaChaseStep chaseStep;
    private Scenario scenario;

    public DbChildFactory(IDatabase db, Scenario scenario) {
        this.db = db;
        this.scenario = scenario;
    }

    public DbChildFactory(IDatabase db, DeltaChaseStep chaseStep, Scenario scenario) {
        this(db, scenario);
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
            node = new TableNode(db.getTable(key), db, chaseStep, scenario);
        } else {
            node = new TableNode(db.getTable(key), db, scenario);
        }
        return node;
    }
}
