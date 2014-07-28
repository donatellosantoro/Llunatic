package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.ITaskListener;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "MSG_ChaseScenario=Scenario chase in progress",
    "MSG_ChaseSuccessful=Chase successful",
    "MSG_ChaseFailed=Chase failed",
    "MSG_ChaseStopping=stopping ",
    "MSG_ChaseCancelled=Chase cancelled",
    "MSG_ChaseInProgress= chase"})
public class ChaseTaskListener implements ITaskListener<ChaseTask> {

    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private Log logger = LogFactory.getLog(getClass());
    private StatusDisplayer status = StatusDisplayer.getDefault();
    private ProgressHandle progress;

    @Override
    public void onTaskStarted(ChaseTask task) {
        this.progress = ProgressHandleFactory.createHandle(task.getScenario().getDataObject().getName().concat(Bundle.MSG_ChaseInProgress()), task);
        this.progress.start();
    }

    @Override
    public void onTaskCompleted(ChaseTask task) {
        progress.finish();
        if (!task.isCancelled()) {
            try {
                IChaseResult result = task.get();
                LoadedScenario scenario = task.getScenario();
                scenario.put(R.BeanProperty.CHASE_RESULT, result);
                status.setStatusText(Bundle.MSG_ChaseSuccessful());
                view.show(result.getWindowName());
                if (scenario.getScenario().isMCScenario()) {
                    view.show(R.Window.CELL_GROUP_EXPLORER);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                if (logger.isDebugEnabled()) ex.printStackTrace();
                status.setStatusText(Bundle.MSG_ChaseFailed() + ": " + ex.getLocalizedMessage());
            }
            task.getScenario().remove(R.BeanProperty.CHASE_STATE);
        } else {
            this.progress = ProgressHandleFactory.createHandle(Bundle.MSG_ChaseStopping());
            this.progress.start();
        }
    }

    @Override
    public void onTaskKilled(ChaseTask task) {
        progress.finish();
        status.setStatusText(Bundle.MSG_ChaseCancelled());
        task.getScenario().remove(R.BeanProperty.CHASE_STATE);
    }
}
