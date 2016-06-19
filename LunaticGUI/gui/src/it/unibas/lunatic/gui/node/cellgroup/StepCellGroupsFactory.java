package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class StepCellGroupsFactory extends ChildFactory<CellGroup> {

    private final List<CellGroup> cellGroups;
    private final ChaseStepNode chaseStepNode;
    private final ICellGroupValueFilter valueFilter;

    public StepCellGroupsFactory(ChaseStepNode node, List<CellGroup> cellGroups, ICellGroupValueFilter valueFilter) {
        this.cellGroups = cellGroups;
        this.chaseStepNode = node;
        this.valueFilter = valueFilter;
    }

    @Override
    protected boolean createKeys(List<CellGroup> toPopulate) {
        for (CellGroup cg : cellGroups) {
            if (Thread.interrupted()) {
                return false;
            }
            if (valueFilter.accept(cg)) {
                toPopulate.add(cg);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(CellGroup key) {
        return new StepCellGroupNode(key, chaseStepNode);
    }
}
