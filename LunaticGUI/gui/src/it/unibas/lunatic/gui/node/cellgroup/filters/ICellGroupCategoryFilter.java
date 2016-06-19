package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public interface ICellGroupCategoryFilter {

    public Node[] createNodesForKey(ChaseStepNode node, StepCellGroups cellGroups, ICellGroupValueFilter valueFilter);
    

}
