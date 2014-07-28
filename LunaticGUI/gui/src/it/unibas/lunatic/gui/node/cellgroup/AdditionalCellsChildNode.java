
package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.database.AttributeRef;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

class AdditionalCellsChildNode extends AbstractNode {

    public AdditionalCellsChildNode(StepCellGroupNode stepCellGroupNode, AttributeRef key) {
        super(Children.create(new AdditionalCellTupleFactory(stepCellGroupNode,key), true));
        setName(key.toString());
        setDisplayName(key.toString());
    }

}
