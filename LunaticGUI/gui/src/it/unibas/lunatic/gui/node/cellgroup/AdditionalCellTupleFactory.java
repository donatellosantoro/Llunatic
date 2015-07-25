package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.database.AttributeRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class AdditionalCellTupleFactory extends ChildFactory<AttributeRef> {

    private final AttributeRef key;
    private final StepCellGroupNode stepCellGroupNode;

    public AdditionalCellTupleFactory(StepCellGroupNode stepCellGroupNode, AttributeRef key) {
        this.stepCellGroupNode = stepCellGroupNode;
        this.key = key;
    }

    @Override
    protected boolean createKeys(List<AttributeRef> toPopulate) {
        toPopulate.add(key);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(AttributeRef key) {
        List<Node> nodes = new ArrayList<Node>();
        Set<CellGroupCell> cells = stepCellGroupNode.getCellGroup().getAdditionalCells().get(key);
        for(CellGroupCell c:cells){
            nodes.add(new AdditionalCellTupleNode(stepCellGroupNode,key,c));
        }
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }
    
    
}
