package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

class AdditionalCellTupleNode extends AbstractNode {
    
    private final StepCellGroupNode stepCellGroupNode;
    private final AttributeRef attributeRef;
    private final Cell cell;
    
    public AdditionalCellTupleNode(StepCellGroupNode stepCellGroupNode, AttributeRef key, Cell cell) {
        super(Children.LEAF);
        this.stepCellGroupNode = stepCellGroupNode;
        this.attributeRef = key;
        this.cell = cell;
        setName(cell.toStringWithOIDAndAlias());
        setDisplayName(cell.toString());
    }
    
    public Cell getCell() {
        return cell;
    }
    
    public StepCellGroupNode getStepCellGroupNode() {
        return stepCellGroupNode;
    }
    
    public AttributeRef getAttributeRef() {
        return attributeRef;
    }
}
