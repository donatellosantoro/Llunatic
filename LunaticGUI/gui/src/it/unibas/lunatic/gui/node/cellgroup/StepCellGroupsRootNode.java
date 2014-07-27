/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupCategoryFilter;
import it.unibas.lunatic.gui.node.cellgroup.filters.ICellGroupValueFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;

/**
 *
 * @author Antonio Galotta
 */
public class StepCellGroupsRootNode extends AbstractNode {

    private final ChildFactory factory;

    public StepCellGroupsRootNode(StepCellGroupsCategoryLoaderFactory factory) {
        super(Children.create(factory, true));
        setName("cellGroupsRoot");
        this.factory = factory;
    }

    public StepCellGroupsRootNode(ChaseStepNode node, ICellGroupValueFilter valueFilter, ICellGroupCategoryFilter categoryFilter) {
        this(new StepCellGroupsCategoryLoaderFactory(node, categoryFilter, valueFilter));
    }
}
