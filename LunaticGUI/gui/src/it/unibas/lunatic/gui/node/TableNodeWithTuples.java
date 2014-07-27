/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node;

import org.openide.nodes.FilterNode;

/**
 *
 * @author Antonio Galotta
 */
public class TableNodeWithTuples extends FilterNode {

    public TableNodeWithTuples(TableNode tableNode, org.openide.nodes.Children children) {
        super(tableNode, children);
    }

    @Override
    public TableNode getOriginal() {
        return (TableNode) super.getOriginal();
    }
}
