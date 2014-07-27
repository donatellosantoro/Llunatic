/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.lazyloading.ITupleLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Antonio Galotta
 */
public class BatchTupleFactory extends Children.Keys<ITupleLoader> implements ITupleFactory {

    private static Log logger = LogFactory.getLog(BatchTupleFactory.class);
    private TableNode tableNode;
    private Thread asyncThread;
    private Node waitNode;
    private final Thread viewThread;

    public BatchTupleFactory(TableNode table) {
        this.tableNode = table;
        waitNode = createWaitNode();
        viewThread = Thread.currentThread();
    }

    public TableNode getTableNode() {
        return tableNode;
    }

    @Override
    protected void addNotify() {
        logger.debug("Initialized");
        super.add(new Node[]{waitNode});
        if (asyncThread == null) {
            asyncThread = new Thread(getKeyCreator(), "Async table cerator thread");
            asyncThread.start();
        }
    }

    @Override
    protected void removeNotify() {
        logger.debug("Removed");
        setKeys(Collections.EMPTY_SET);
    }

    @Override
    protected Node[] createNodes(ITupleLoader key) {
        TableTupleLoaderNode node = createNodeForKey(key);
        return new Node[]{node};
    }

    protected TableTupleLoaderNode createNodeForKey(ITupleLoader key) {
        if (tableNode.isMcResultNode()) {
            return new TableTupleLoaderNode(key, tableNode.getTable(), tableNode.getDb(), tableNode.getChaseStep());
        }
        return new TableTupleLoaderNode(key, tableNode.getTable(), tableNode.getDb());
    }

    private Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF) {
            public @Override
            Action[] getActions(boolean context) {
                return new Action[0];
            }
        };
        n.setIconBaseWithExtension("org/openide/nodes/wait.gif"); //NOI18N
        n.setDisplayName(NbBundle.getMessage(ChildFactory.class, "LBL_WAIT")); //NOI18N
        return n;
    }

    public Runnable getKeyCreator() {
        return new KeyCreator();
    }

    @Override
    public void interrupt() {
        if (asyncThread != null) {
            asyncThread.interrupt();
            logger.info("Interrupt async thread");
        }
    }

    @Override
    public Node createTuples() {
        return new TableNodeWithTuples(tableNode, this);
    }

    class KeyCreator implements Runnable {

        @Override
        public void run() {
            ArrayList<ITupleLoader> keys = new ArrayList<ITupleLoader>();
            ITable table = tableNode.getTable();
            Iterator<ITupleLoader> iterator = table.getTupleLoaderIterator();
            boolean threadInterrupted = isInterrupted();
            while (iterator.hasNext() && !viewThread.isInterrupted() && !threadInterrupted) {
                keys.add(iterator.next());
                threadInterrupted = isInterrupted();
            }
            remove(new Node[]{waitNode});
            if (!viewThread.isInterrupted() && !threadInterrupted) {
                setKeys(keys);
            }
            asyncThread = null;
            if (viewThread.isInterrupted()) {
                logger.info("Factory interrupted: view thread");
            }
            if (threadInterrupted) {
                logger.debug("Factory interrupted: async thread");
            }
        }

        private boolean isInterrupted() {
            boolean interrupted = Thread.currentThread().isInterrupted();
            logger.trace("Thread interrupted: " + interrupted);
            return interrupted;
        }
    }
}
