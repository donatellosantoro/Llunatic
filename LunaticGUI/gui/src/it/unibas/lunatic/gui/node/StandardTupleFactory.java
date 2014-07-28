package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.model.database.lazyloading.ITupleLoader;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class StandardTupleFactory extends ChildFactory<ITupleLoader> implements ITupleFactory {

    private final TableNode tableNode;

    public StandardTupleFactory(TableNode table) {
        this.tableNode = table;
    }

    @Override
    protected boolean createKeys(List<ITupleLoader> toPopulate) {
        Iterator<ITupleLoader> iterator = tableNode.getTable().getTupleLoaderIterator();
        while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
            toPopulate.add(iterator.next());
        }
        return true;
    }

    @Override
    protected TableTupleLoaderNode createNodeForKey(ITupleLoader key) {
        if (tableNode.isMcResultNode()) {
            return new TableTupleLoaderNode(key, tableNode.getTable(), tableNode.getDb(), tableNode.getChaseStep());
        }
        return new TableTupleLoaderNode(key, tableNode.getTable(), tableNode.getDb());
    }

    @Override
    public void interrupt() {
    }

    @Override
    public Node createTuples() {
        return new TableNodeWithTuples(tableNode, Children.create(this, true));
    }
}
