package it.unibas.lunatic.gui.node.chase.de;

import it.unibas.lunatic.gui.model.DeChaseResult;
import it.unibas.lunatic.gui.node.DbNode;
import speedy.model.database.IDatabase;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

class DeChaseResultChildFactory extends ChildFactory<IDatabase> {

    private final DeChaseResult chase;

    public DeChaseResultChildFactory(DeChaseResult chaseResult) {
        this.chase = chaseResult;
    }

    @Override
    protected boolean createKeys(List<IDatabase> toPopulate) {
        toPopulate.add(chase.getResult());
        return true;
    }

    @Override
    protected Node createNodeForKey(IDatabase key) {
        return new DbNode(chase.getLoadedScenario().getScenario(), key, chase.getLoadedScenario().getDataObject().getName());
    }
}
