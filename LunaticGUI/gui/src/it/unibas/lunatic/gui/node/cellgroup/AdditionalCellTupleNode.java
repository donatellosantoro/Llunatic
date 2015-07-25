package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.database.AttributeRef;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

class AdditionalCellTupleNode extends AbstractNode {
    
    private final StepCellGroupNode stepCellGroupNode;
    private final AttributeRef attributeRef;
    private final CellGroupCell cell;
    
    public AdditionalCellTupleNode(StepCellGroupNode stepCellGroupNode, AttributeRef key, CellGroupCell cell) {
        super(Children.LEAF);
        this.stepCellGroupNode = stepCellGroupNode;
        this.attributeRef = key;
        this.cell = cell;
        setName(cell.toStringWithOIDAndAlias() + " [" + cell.getOriginalValue() + "]");
        setDisplayName(cell.toString());
    }
    
    public CellGroupCell getCell() {
        return cell;
    }
    
    public StepCellGroupNode getStepCellGroupNode() {
        return stepCellGroupNode;
    }
    
    public AttributeRef getAttributeRef() {
        return attributeRef;
    }
}
