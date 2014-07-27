/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.exceptions.NodeNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;

/**
 *
 * @author Antonio Galotta
 */
public class TableFinder {

    public List<TableNode> findTables(Node node) {
        List<TableNode> tables = new ArrayList<TableNode>();
        DbNode dbNode = findDbNode(node);
        Node[] dbChildren = dbNode.getChildren().getNodes();
        for (int i = 0; i < dbChildren.length; i++) {
            if (dbChildren[i] instanceof TableNode) {
                tables.add((TableNode) dbChildren[i]);
            }
        }
        return tables;
    }

    public TableNode findByName(Node node, String tableName) {
        DbNode dbNode = findDbNode(node);
        return (TableNode) NodeOp.findChild(dbNode, tableName);
    }

    public DbNode findDbNode(Node node) {
        Node[] stepChildren = node.getChildren().getNodes();
        for (int i = 0; i < stepChildren.length; i++) {
            if (stepChildren[i] instanceof DbNode) {
                return (DbNode) stepChildren[i];
            }
        }
        throw new NodeNotFoundException("DbNode child not found in " + node.toString());
    }
}
