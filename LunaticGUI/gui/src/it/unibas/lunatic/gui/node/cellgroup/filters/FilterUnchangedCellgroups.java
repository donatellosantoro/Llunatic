/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupCategoryNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chasemc.CellGroup;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
public class FilterUnchangedCellgroups implements ICellGroupCategoryFilter {

    @Override
    public Node[] createNodesForKey(ChaseStepNode node, StepCellGroups cellGroups, ICellGroupValueFilter valueFilter) {
        List<CellGroup> allCellGroups = new ArrayList<CellGroup>(cellGroups.getAll());
        allCellGroups.removeAll(cellGroups.getChangedCellGroups());
        List<Node> nodes = new ArrayList<Node>(allCellGroups.size());
        for (CellGroup cg : allCellGroups) {
            if (valueFilter.accept(cg)) {
                nodes.add(new StepCellGroupNode(cg, node));
            }
        }
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }
}
