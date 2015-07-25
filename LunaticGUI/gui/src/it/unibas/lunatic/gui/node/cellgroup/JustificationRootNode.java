package it.unibas.lunatic.gui.node.cellgroup;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class JustificationRootNode extends AbstractNode {

    public JustificationRootNode(StepCellGroupNode stepCellGroupNode) {
        super(Children.create(new JustificationTupleFactory(stepCellGroupNode,stepCellGroupNode.getCellGroup().getJustifications()), true));
    }
}
