package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class UserStepCellGroupsRootNode extends AbstractNode {

    public UserStepCellGroupsRootNode(ChaseStepNode chaseStepNode, ICellGroupValueFilter valueFilter) {
        super(Children.create(new StepCellGroupsLoaderFactory(chaseStepNode, valueFilter), true));
        setName("cellGroupsRoot");
    }
}
