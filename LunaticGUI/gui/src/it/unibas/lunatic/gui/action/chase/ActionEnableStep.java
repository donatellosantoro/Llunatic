package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Run",
        id = R.ActionId.ENABLE_CHASE_STEP)
@ActionRegistration(
        displayName = "#CTL_ActionEnableStep")
@Messages("CTL_ActionEnableStep=Mark as valid")
public final class ActionEnableStep implements ActionListener {

    private final List<ChaseStepNode> context;

    public ActionEnableStep(List<ChaseStepNode> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (ChaseStepNode node : context) {
            node.setValid();
        }
    }
}
