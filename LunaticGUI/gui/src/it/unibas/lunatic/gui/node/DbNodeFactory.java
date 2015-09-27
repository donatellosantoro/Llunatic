package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class DbNodeFactory extends ChildFactory<IDatabase> {

    private final IDatabase db;
    private String id;
    private Scenario scenario;

    DbNodeFactory(Scenario scenario, IDatabase db, String id) {
        this.db = db;
        this.id = id;
        this.scenario = scenario;
    }

    @Override
    protected boolean createKeys(List<IDatabase> toPopulate) {
        toPopulate.add(db);
        return true;
    }

    @Override
    protected Node createNodeForKey(IDatabase key) {
        return new DbNode(scenario, db, id);
    }
}
