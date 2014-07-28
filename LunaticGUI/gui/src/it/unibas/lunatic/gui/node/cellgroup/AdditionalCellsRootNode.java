
package it.unibas.lunatic.gui.node.cellgroup;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class AdditionalCellsRootNode extends AbstractNode{


    public AdditionalCellsRootNode(StepCellGroupNode stepCellGroupNode) {
        super(Children.create(new AdditionalCellChildFactory(stepCellGroupNode), true));
    }
    
}
