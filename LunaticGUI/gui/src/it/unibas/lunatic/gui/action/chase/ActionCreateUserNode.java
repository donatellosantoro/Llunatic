package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Run",
        id = R.ActionId.CREATE_USER_NODE)
@ActionRegistration(
        displayName = "#CTL_ActionCreateUserNode")
@Messages({
    "CTL_ActionCreateUserNode=Create user node",
    "MSG_UserNodeLimit=This step already has a user node",
    "MSG_InvalidParent=An invalid step cannot have a user node",
})
public final class ActionCreateUserNode implements ActionListener {

    private NotifyDescriptor.Message limitDialog = new NotifyDescriptor.Message(Bundle.MSG_UserNodeLimit());
    private NotifyDescriptor.Message invalidDialog = new NotifyDescriptor.Message(Bundle.MSG_InvalidParent());
    private DialogDisplayer dialogDisplayer = DialogDisplayer.getDefault();
    private final ChaseStepNode context;
    private OperatorFactory operatorFactory = OperatorFactory.getInstance();

    public ActionCreateUserNode(ChaseStepNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (hasUserNode(context)) {
            dialogDisplayer.notify(limitDialog);
        } else if (context.getChaseStep().isInvalid()) {
            dialogDisplayer.notify(invalidDialog);
        } else {
            Scenario scenario = context.getScenario();
            AddUserNode addUserNode = operatorFactory.getUserNodeCreator(scenario);
            DeltaChaseStep userChaseStep = addUserNode.addUserNode(context.getChaseStep(), scenario);
            context.addUserNode(userChaseStep);
        }
    }

    private boolean hasUserNode(ChaseStepNode context) {
        for (Node n : context.getChildren().getNodes()) {
            if (n instanceof ChaseStepNode) {
                ChaseStepNode child = (ChaseStepNode) n;
                if (child.getChaseStep().isEditedByUser()) {
                    return true;
                }
            }
        }
        return false;
    }
}
