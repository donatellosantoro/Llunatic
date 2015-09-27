package it.unibas.lunatic.gui.node.cellgroup;

import speedy.model.database.AttributeRef;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class AdditionalCellChildFactory extends ChildFactory<AttributeRef> {

    private final StepCellGroupNode stepCellGroupNode;
    private static Log logger = LogFactory.getLog(AdditionalCellChildFactory.class.getName());

    public AdditionalCellChildFactory(StepCellGroupNode stepCellGroupNode) {
        this.stepCellGroupNode = stepCellGroupNode;
    }

    @Override
    protected boolean createKeys(List<AttributeRef> toPopulate) {
        if (logger.isDebugEnabled()) logger.debug("Analizing addition cells for cell group " + stepCellGroupNode.getCellGroup().toStringWithAdditionalCells());
        if (logger.isDebugEnabled()) logger.debug("############ Additional cells: " + stepCellGroupNode.getCellGroup().getAdditionalCells().keySet().size());
        for (AttributeRef key : stepCellGroupNode.getCellGroup().getAdditionalCells().keySet()) {
            if (logger.isDebugEnabled()) logger.debug("Creating key " + key + "  for cellgroup " + stepCellGroupNode.getCellGroup());
            toPopulate.add(key);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(AttributeRef key) {
        return new AdditionalCellsChildNode(stepCellGroupNode, key);
    }
}
