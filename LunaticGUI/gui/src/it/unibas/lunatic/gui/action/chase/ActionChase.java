package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.gui.action.chase.task.ChaseTaskListener;
import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.chase.task.ChaseTask;
import it.unibas.lunatic.gui.action.chase.task.StandardChase;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.model.chase.commons.ChaseState;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Run",
        id = R.ActionId.RUN_CHASE)
@ActionRegistration(
        displayName = "#CTL_ActionChase",
        lazy = false)
@ActionReferences({
    //@ActionReference(path = "Menu/Run", position = 0),
    @ActionReference(path = "Toolbars/Run", position = 0)
})
@Messages({
    "CTL_ActionChase=Run",
    "DIALOG_ResetChaseQuestion=Chase has already been executed. Every change made will be lost."
    + "\nConfirm to continue."
})
public final class ActionChase extends ContextAwareActionProxy<ChaseState> {

    private NotifyDescriptor.Confirmation resetChase = new NotifyDescriptor.Confirmation(Bundle.DIALOG_ResetChaseQuestion(), NotifyDescriptor.OK_CANCEL_OPTION);
    private DialogDisplayer dialogDisplayer = DialogDisplayer.getDefault();
    private ChaseTaskListener chaseListener = new ChaseTaskListener();
    private StandardChase standardChase = new StandardChase();

    public ActionChase() {
        super.putValue(Action.SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/run-project.24.png"));
        super.putValue(Action.NAME, Bundle.CTL_ActionChase());
    }

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_STATE, ChaseState.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        LoadedScenario scenario = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        IChaseResult result = scenario.get(R.BeanProperty.CHASE_RESULT, IChaseResult.class);
        boolean execute = true;
        if (result != null) {
            Object choice = dialogDisplayer.notify(resetChase);
            if (!choice.equals(NotifyDescriptor.OK_OPTION)) {
                execute = false;
            }
        }
        if (execute) {
            ChaseState chaseState = new ChaseState();
            scenario.put(R.BeanProperty.CHASE_STATE, chaseState);
            ChaseTask chaseTask = new ChaseTask(scenario, standardChase, chaseListener, chaseState);
            chaseTask.execute();
            update();
        }
    }

    @Override
    public void onChange(IModel model, ChaseState chaseState) {
        if (model != null && chaseState == null) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
}
