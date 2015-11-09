package it.unibas.lunatic.gui.window.cellgroup;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupsRootNode;
import it.unibas.lunatic.gui.node.cellgroup.UserStepCellGroupsRootNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.cellgroup.filters.DefaultFilter;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupCategoryFilter;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.Collection;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "TITLE_allCellGroups=Step cell groups - ",
    "TITLE_editCellGroups=Edit cell groups - ",
    "TITLE_noChaseStepSelected=No Chase Step Selected"
})
public class BindSelectedChaseStepGroups extends AbstractSelectionListener<ChaseStepNode> {
//implements NodeListener {

    private ExplorerTopComponent window;
    private ChaseStepNode node;
    private DefaultFilter defaultFilter = new DefaultFilter();
    private ICellGroupCategoryFilter categoryFilter = defaultFilter;
    private ICellGroupValueFilter valueFilter = defaultFilter;

    @Override
    protected void onChange(Collection<? extends ChaseStepNode> beans) {
        ChaseStepNode mynode = getBean(beans);
        logger.debug("Selected step: " + mynode);
        if (mynode != null) {
            this.node = mynode;
//            addNodeListener(mynode);
            updateComponent(mynode);
        }
    }

    private void updateComponent(ChaseStepNode node) {
        if (node == null) {
            window.removeRootContext();
            window.setDisplayName(Bundle.TITLE_noChaseStepSelected());
            return;
        }
        DeltaChaseStep chaseStep = node.getChaseStep();
        if (chaseStep.isEditedByUser()) {
            window.setRootContext(new UserStepCellGroupsRootNode(node, valueFilter));
            updateWindowTitle(node, Bundle.TITLE_editCellGroups());
        } else {
            window.setRootContext(new StepCellGroupsRootNode(node, valueFilter, categoryFilter));
            updateWindowTitle(node, Bundle.TITLE_allCellGroups());
        }
    }

    private void updateWindowTitle(ChaseStepNode info, String title) {
        String result = title.concat(info.getChaseStep().getId());
        window.setDisplayName(result);
        window.setToolTipText(result);
    }

    public void filter() {
        if (node != null) {
            updateComponent(node);
        }
    }

    void setValueFilter(ICellGroupValueFilter valueFilter) {
        this.valueFilter = valueFilter;
    }

    void setCategoryFilter(ICellGroupCategoryFilter categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public void register(CellGroupExplorerTopComponent tc) {
        this.window = tc;
        super.registerBean(ChaseStepNode.class);
    }
}
