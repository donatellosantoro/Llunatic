package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.DbExtractor;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.chase.task.ChaseTaskListener;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import it.unibas.lunatic.model.chase.commons.control.ChaseState;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
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
    "MSG_InvalidParent=An invalid step cannot have a user node",})
public final class ActionCreateUserNode extends ContextAwareActionProxy<ChaseState> implements ActionListener {
//public final class ActionCreateUserNode implements ActionListener {

    private NotifyDescriptor.Message limitDialog = new NotifyDescriptor.Message(Bundle.MSG_UserNodeLimit());
    private NotifyDescriptor.Message invalidDialog = new NotifyDescriptor.Message(Bundle.MSG_InvalidParent());
    private DialogDisplayer dialogDisplayer = DialogDisplayer.getDefault();
    private OperatorFactory operatorFactory = OperatorFactory.getInstance();
    private final ChaseStepNode context;

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
            context.refreshCellGroups();
            LoadedScenario loadedScenario = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
            loadedScenario.put(R.BeanProperty.CHASE_STATE, new ChaseState());
            loadedScenario.remove(R.BeanProperty.CHASE_STATE);
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

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_STATE, ChaseState.class);
    }

    @Override
    public void onChange(IModel model, ChaseState bean) {
    }
}
