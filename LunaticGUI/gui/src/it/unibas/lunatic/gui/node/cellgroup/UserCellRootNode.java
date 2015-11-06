package it.unibas.lunatic.gui.node.cellgroup;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class UserCellRootNode extends AbstractNode {

    public UserCellRootNode(StepCellGroupNode stepCellGroupNode) {
        super(Children.create(new UserCellTupleFactory(stepCellGroupNode,stepCellGroupNode.getCellGroup().getUserCells()), true));
    }
}
