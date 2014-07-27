/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.utils;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author Antonio Galotta
 */
public class ExpandedView extends BeanTreeView implements TreeModelListener {
    
    private Log logger = LogFactory.getLog(getClass());
    
    public ExpandedView() {
        logger.warn("Mock implementation");
        tree.getModel().addTreeModelListener(this);
    }
    
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
//        logger.trace("Changed: " + e);
    }

    //TODO: workaround. expanAdll must be replaced
    @Override
    public void treeNodesInserted(final TreeModelEvent e) {
//        logger.trace("Inserted: " + e);
//        showPath(e.getTreePath());
//        Node n = Visualizer.findNode(e.getTreePath().getLastPathComponent());
//        logger.trace("Node: " + n);
//        expandNode(n);
//        expandAll();
//        showPath(e.getTreePath());
//        showPath(e.getTreePath().getParentPath());
    }
    
    //chiamato quando viene rimosso il wait node
    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        logger.trace("Removed: " + e);
        expandAll();
    }
    
    @Override
    public void treeStructureChanged(TreeModelEvent e) {
//        logger.trace("Structure: " + e);
    }
}
