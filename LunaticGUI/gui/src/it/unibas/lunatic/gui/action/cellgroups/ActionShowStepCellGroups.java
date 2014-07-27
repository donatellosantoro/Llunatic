/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.cellgroups.RetrieveCellGroupsAction;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_STEP_CELL_GROUPS_ALL)
@ActionRegistration(
        displayName = "#CTL_ActionShowAllStepCellGroups",
        asynchronous = true)
@Messages("CTL_ActionShowAllStepCellGroups=View cell groups")
public final class ActionShowStepCellGroups implements ActionListener {

    private RetrieveCellGroupsAction retrieveCellGroups = new RetrieveCellGroupsAction();
    private ChaseStepNode node;

    public ActionShowStepCellGroups(ChaseStepNode node) {
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        retrieveCellGroups.retrieve(node);
    }
}
