package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.ITaskListener;
import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;

@NbBundle.Messages({
    "MSG_ChaseScenario=Scenario chase in progress",
    "MSG_ChaseSuccessful=Chase complete",
    "MSG_ChaseFailed=Chase failed",
    "MSG_ChaseStopping=stopping ",
    "MSG_ChaseCancelled=Chase cancelled",
    "MSG_ChaseInProgress= chase"})
public class ChaseTaskListener implements ITaskListener<ChaseTask> {

    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private Log logger = LogFactory.getLog(getClass());
    private StatusDisplayer status = StatusDisplayer.getDefault();
    private DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
    private ProgressHandle progress;
    private String outputWindowName;

    @Override
    public void onTaskStarted(ChaseTask task) {
        this.progress = ProgressHandleFactory.createHandle(task.getScenario().getDataObject().getName().concat(Bundle.MSG_ChaseInProgress()), task);
        this.progress.start();
        this.outputWindowName = "Chase Execution " + df.format(new Date().getTime());
        redirectOutput();
        IOProvider.getDefault().getIO(outputWindowName, false).select();
    }

    @Override
    public void onTaskCompleted(ChaseTask task) {
        progress.finish();
        if (!task.isCancelled()) {
            try {
                IChaseResult result = task.get();
                LoadedScenario scenario = task.getScenario();
                scenario.put(R.BeanProperty.CHASE_RESULT, result);
                String statusText = Bundle.MSG_ChaseSuccessful();
                Long exTime = ChaseStats.getInstance().getStat(ChaseStats.CHASE_TIME);
                if (exTime != null && exTime > 0) {
                    statusText += " in " + exTime + " ms";
                }
                status.setStatusText(statusText);
                for (String windowName : result.getWindowsToOpen()) {
                    view.show(windowName);
                }
                view.show(result.getWindowsToOpen().get(0));
                if (scenario.getScenario().isMCScenario()) {
                    view.show(R.Window.CELL_GROUP_EXPLORER);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                if (logger.isDebugEnabled()) ex.printStackTrace();
                status.setStatusText(Bundle.MSG_ChaseFailed() + ": " + ex.getLocalizedMessage());
                Exceptions.printStackTrace(ex);
            }
            IOProvider.getDefault().getIO(outputWindowName, false).select();
            resetOutput();
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

    private void redirectOutput() {
        LunaticConfiguration.setPrintSteps(true);
        OutputStream out = new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                IOProvider.getDefault().getIO(outputWindowName, false).getOut().print(String.valueOf((char) i));
            }

            @Override
            public void write(byte[] bytes) throws IOException {
                IOProvider.getDefault().getIO(outputWindowName, false).getOut().print(new String(bytes));
            }

            @Override
            public void write(byte[] bytes, int off, int len) throws IOException {
                IOProvider.getDefault().getIO(outputWindowName, false).getOut().print(new String(bytes, off, len));
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void resetOutput() {
        System.setOut(new PrintStream(System.out, true));
        System.setErr(new PrintStream(System.err, true));
    }

}
