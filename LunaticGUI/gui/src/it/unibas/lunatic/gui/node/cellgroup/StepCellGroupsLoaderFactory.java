package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class StepCellGroupsLoaderFactory extends ChildFactory<ChaseStepNode> {

    private final ChaseStepNode chaseStepNode;
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();
    private final ICellGroupValueFilter valueFilter;

    public StepCellGroupsLoaderFactory(ChaseStepNode chaseStepNode, ICellGroupValueFilter valueFilter) {
        this.chaseStepNode = chaseStepNode;
        this.valueFilter = valueFilter;
    }

    @Override
    protected boolean createKeys(List<ChaseStepNode> toPopulate) {
        toPopulate.add(chaseStepNode);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(ChaseStepNode key) {
        List<CellGroup> cellGroups = retrieveCellGroups();
        List<Node> nodes = new ArrayList<Node>(cellGroups.size());
        for (CellGroup cg : cellGroups) {
            if (valueFilter.accept(cg)) {
                nodes.add(new StepCellGroupNode(cg, key));
            }
        }
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }

    public List<CellGroup> retrieveCellGroups() {
        List<CellGroup> cellGroups;
        if (!chaseStepNode.hasCellGroupsLoaded()) {
            StepCellGroups stepCellGroups = cgHelper.retrieveStepCellGroups(chaseStepNode.getScenario(), chaseStepNode.getChaseStep());
            chaseStepNode.cacheCellGroups(stepCellGroups);
            cellGroups = stepCellGroups.getAll();
        } else {
            cellGroups = chaseStepNode.getCellGroups().getAll();
        }
        return cellGroups;
    }
}
