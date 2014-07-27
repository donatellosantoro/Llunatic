package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.gui.action.chase.task.ChaseTaskListener;
import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.chase.task.ChaseTask;
import it.unibas.lunatic.gui.action.chase.task.InteractiveChase;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.model.chase.control.ChaseState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Run",
        id = R.ActionId.CONTINUE_CHASE)
@ActionRegistration(
        displayName = "#CTL_ActionChaseContinue",
        lazy = false)
@ActionReferences({
    //@ActionReference(path = "Menu/Run", position = 0),
    @ActionReference(path = "Toolbars/Run", position = 1)
})
@Messages({"CTL_ActionChaseContinue=Continue chase"})
public final class ActionChaseContinue extends ContextAwareActionProxy<ChaseState> implements ActionListener {

    private ChaseTaskListener chaseListener = new ChaseTaskListener();
    private InteractiveChase interactiveChase = new InteractiveChase();

    public ActionChaseContinue() {
        super.putValue(Action.SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/continue-chase.24.gif"));
        super.putValue(Action.NAME, Bundle.CTL_ActionChaseContinue());
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
        ChaseTask chaserTask = new ChaseTask(scenario, interactiveChase, chaseListener, chaseState);
        chaserTask.execute();
        update();
    }

    @Override
    public void onChange(IModel model, ChaseState chaseState) {
        LoadedScenario scenario = (LoadedScenario) model;
        if (scenario != null
                && scenario.get(R.BeanProperty.CHASE_STATE, ChaseState.class) == null
                && scenario.getScenario().isMCScenario()
                && scenario.get(R.BeanProperty.CHASE_RESULT, IChaseResult.class) != null
                && scenario.getScenario().getUserManager() != null) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
}
