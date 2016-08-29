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
        id = R.ActionId.RUN_CHECK_CONFLICTS)
@ActionRegistration(
        displayName = "#CTL_ActionCheckConflicts",
        lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Run", position = 0),
    //    @ActionReference(path = "Toolbars/Run", position = 0)
})
@Messages({
    "CTL_ActionCheckConflicts=Check Conflicts"
})
public final class ActionCheckConflicts extends ContextAwareActionProxy<ChaseState> {

    private ChaseTaskListener chaseListener = new ChaseTaskListener();
    private StandardChase standardChase = new StandardChase(true);

    public ActionCheckConflicts() {
//        super.putValue(Action.SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/run-project.24.png"));
        super.putValue(Action.NAME, Bundle.CTL_ActionCheckConflicts());
    }

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_STATE, ChaseState.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        LoadedScenario scenario = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
        ChaseState chaseState = new ChaseState();
        scenario.put(R.BeanProperty.CHASE_STATE, chaseState);
        ChaseTask chaseTask = new ChaseTask(scenario, standardChase, chaseListener, chaseState);
        chaseTask.execute();
        update();
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
