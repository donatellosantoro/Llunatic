package it.unibas.lunatic.gui.window.utils;

import it.unibas.lunatic.gui.node.chase.mc.ChaseTreeRoot;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;

public class StatefulView extends BeanTreeView {

    private Log logger = LogFactory.getLog(getClass());
    private Node root;

    public void scrollToNode(final Node n) {
        // has to be delayed to be sure that events for Visualizers
        // were processed and TreeNodes are already in hierarchy
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreeNode tn = Visualizer.findVisualizer(n);
                if (tn == null) {
                    return;
                }
                TreeModel model = tree.getModel();
                if (!(model instanceof DefaultTreeModel)) {
                    return;
                }
                TreePath path = new TreePath(((DefaultTreeModel) model).getPathToRoot(tn));
                Rectangle r = tree.getPathBounds(path);
                if (r != null) {
                    tree.scrollRectToVisible(r);
                }
            }
        });
    }

    public List<String[]> getExpandedPaths() {
        List<String[]> result = new ArrayList<String[]>();
        if (getRootNode() != null) {
            TreeNode rtn = Visualizer.findVisualizer(getRootNode());
            TreePath tp = new TreePath(rtn); // Get the root
            Enumeration exPaths = tree.getExpandedDescendants(tp);
            while (exPaths != null && exPaths.hasMoreElements()) {
                TreePath ep = (TreePath) exPaths.nextElement();
                Node en = Visualizer.findNode(ep.getLastPathComponent());
                String[] path = NodeOp.createPath(en, getRootNode());
                result.add(path);
                if (logger.isTraceEnabled()) logger.trace("Expanded path: " + LoggingUtils.printArray(path));
            }
        }
        return result;

    }

    /**
     * Expands all the paths, when exists
     */
    public void expandNodes(List<String[]> exPaths) {
        for (final String[] sp : exPaths) {
//            logger.trace( "{0}: expanding {1}", new Object[]{id, Arrays.asList(sp)});
            Node n;
            try {
                n = NodeOp.findPath(getRootNode(), sp);
            } catch (NodeNotFoundException e) {
//                logger.trace( "got {0}", e.toString());
                n = e.getClosestNode();
            }
            if (n == null) { // #54832: it seems that sometimes we get unparented node
//                logger.trace( "nothing from {0} via {1}", new Object[]{getRootNode(), Arrays.toString(sp)});
                continue;
            }
            final Node leafNode = n;
            EventQueue.invokeLater(new Runnable() {
                public @Override
                void run() {
                    TreeNode tns[] = new TreeNode[sp.length + 1];
                    Node n = leafNode;
                    for (int i = sp.length; i >= 0; i--) {
                        if (n == null) {
//                            logger.trace( "lost parent node at #{0} from {1}", new Object[]{i, leafNode});
                            return;
                        }
                        tns[i] = Visualizer.findVisualizer(n);
                        n = n.getParentNode();
                    }
                    showPath(new TreePath(tns));
                }
            });
        }
    }

    public void setRootNode(ExplorerManager em, Node newRoot) {
        List<String[]> expandedPaths = new ArrayList<String[]>();
        if (this.root != null) {
            expandedPaths = getExpandedPaths();
        }
        setRootNode(em, newRoot, expandedPaths);
    }

    public void setRootNode(ExplorerManager em, Node newRoot, List<String[]> expandedPaths) {
        this.root = newRoot;
        em.setRootContext(newRoot);
        expandNodes(expandedPaths);
    }

    public Node getRootNode() {
        return root;
    }
}
