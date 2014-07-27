/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chasemc.CellGroup;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

/**
 *
 * @author Antonio Galotta
 */
@NbBundle.Messages({
    "NODE_AllCellGroupsCategory=Step cell groups",
    "NODE_ChangedCellGroupsCategory=Changed in this step",
    "NODE_UnchangedCellGroupsCategory=Others"})
public class StepCellGroupCategoryNode extends AbstractNode {

    public static final int ALL = 0;
    public static final int CHANGED = 1;
    public static final int OTHERS = 2;

    public StepCellGroupCategoryNode(ChaseStepNode node, List<CellGroup> cellGroups, int category, ICellGroupValueFilter valueFilter) {
        super(Children.create(new StepCellGroupsFactory(node, cellGroups, valueFilter), false));
        init(category);
    }

    private void init(int category) {
        setName("CellGroupType:" + category);
        switch (category) {
            case 2:
                setDisplayName(Bundle.NODE_UnchangedCellGroupsCategory());
                setIconBaseWithExtension("it/unibas/lunatic/icons/cg-cat-others.png");
                break;
            case 1:
                setDisplayName(Bundle.NODE_ChangedCellGroupsCategory());
                setIconBaseWithExtension("it/unibas/lunatic/icons/cg-cat-new.png");
                break;
            case 0:
                setDisplayName(Bundle.NODE_AllCellGroupsCategory());
                break;
        }
    }
}
