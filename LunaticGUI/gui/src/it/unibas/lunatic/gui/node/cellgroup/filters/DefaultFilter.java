package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupCategoryNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Node;

public class DefaultFilter implements ICellGroupValueFilter, ICellGroupCategoryFilter {

    @Override
    public Node[] createNodesForKey(ChaseStepNode node, StepCellGroups cellGroups, ICellGroupValueFilter valueFilter) {
        List<Node> nodes = new ArrayList<Node>();
        for (CellGroup cg : cellGroups.getAll()) {
            if (valueFilter.accept(cg)) {
                nodes.add(new StepCellGroupNode(cg, node));
            }
        }
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }

    @Override
    public boolean accept(CellGroup cg) {
        return true;
    }
}
