package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupCategoryFilter;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class StepCellGroupsCategoryLoaderFactory extends ChildFactory<ChaseStepNode> {

    private final ChaseStepNode info;
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();
    private final ICellGroupValueFilter valueFilter;
    private final ICellGroupCategoryFilter categoryFilter;

    public StepCellGroupsCategoryLoaderFactory(ChaseStepNode node, ICellGroupCategoryFilter categoryFilter, ICellGroupValueFilter valueFilter) {
        this.info = node;
        this.valueFilter = valueFilter;
        this.categoryFilter = categoryFilter;
    }

    @Override
    protected boolean createKeys(List<ChaseStepNode> toPopulate) {
        toPopulate.add(info);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(ChaseStepNode node) {
        StepCellGroups cellGroups = retrieveFromNode(node);
        return categoryFilter.createNodesForKey(node,cellGroups,valueFilter);
    }

    private StepCellGroups retrieveFromNode(ChaseStepNode chaseStepNode) {
        StepCellGroups stepCellGroups;
        if (!chaseStepNode.hasCellGroupsLoaded()) {
            stepCellGroups = cgHelper.retrieveStepCellGroups(chaseStepNode.getScenario(), chaseStepNode.getChaseStep());
            chaseStepNode.cacheCellGroups(stepCellGroups);
        } else {
            stepCellGroups = chaseStepNode.getCellGroups();
        }
        return stepCellGroups;
    }
}
