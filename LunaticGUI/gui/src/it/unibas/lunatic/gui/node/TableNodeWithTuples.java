package it.unibas.lunatic.gui.node;

import org.openide.nodes.FilterNode;

public class TableNodeWithTuples extends FilterNode {

    public TableNodeWithTuples(TableNode tableNode, org.openide.nodes.Children children) {
        super(tableNode, children);
    }

    @Override
    public TableNode getOriginal() {
        return (TableNode) super.getOriginal();
    }
}
