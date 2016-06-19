package it.unibas.lunatic.gui.action.cellgroups;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.EDIT_STEP_CELL_GROUPS)
@ActionRegistration(
        displayName = "#CTL_ActionEditStepCellGroups",
        asynchronous = true)
@Messages("CTL_ActionEditStepCellGroups=Edit cell groups")
public final class ActionEditStepCellGroups implements ActionListener {

    private RetrieveCellGroupsAction retrieveCellGroups = new RetrieveCellGroupsAction();
    private ChaseStepNode node;

    public ActionEditStepCellGroups(ChaseStepNode node) {
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        retrieveCellGroups.retrieve(node);
    }
}
