package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.ITaskListener;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.model.chase.commons.control.ChaseState;
import java.util.Observable;
import java.util.Observer;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;

public class ChaseTask extends SwingWorker<IChaseResult, Object> implements Cancellable, Observer {

    protected Log logger = LogFactory.getLog(ChaseTask.class);
    protected LoadedScenario loadedScenario;
    protected ITaskListener<ChaseTask> listener;
    protected IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    protected final ChaseState chaseState;
    private final IChaseOperator chaser;

    public ChaseTask(LoadedScenario scenario, IChaseOperator chaser, ITaskListener<ChaseTask> listener, ChaseState chaseState) {
        this.loadedScenario = scenario;
        this.listener = listener;
        this.chaser = chaser;
        this.chaseState = chaseState;
    }

    @Override
    protected final IChaseResult doInBackground() throws Exception {
        fireChaseStarted();
        chaseState.addObserver(this);
        return chaser.chase(loadedScenario);
    }

    @Override
    protected void done() {
        listener.onTaskCompleted(this);
    }

    @Override
    public boolean cancel() {
        super.cancel(false);
        chaseState.cancel();
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (chaseState.isValid()) {
            return;
        }
        fireChaseKilled();
    }

    private void fireChaseKilled() {
        view.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.onTaskKilled(ChaseTask.this);
            }
        });
    }

    private void fireChaseStarted() {
        view.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.onTaskStarted(ChaseTask.this);
            }
        });
    }

    public LoadedScenario getScenario() {
        return loadedScenario;
    }

    public ChaseState getChaseState() {
        return chaseState;
    }
}
