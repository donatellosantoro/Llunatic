/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.ITaskListener;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.action.scenario.task.LoadScenarioTask;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.data.ScenarioDataObject;
import it.unibas.lunatic.gui.model.LoadedScenario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = R.ActionId.LOAD_SCENARIO)
@ActionRegistration(
        displayName = "#CTL_ActionLoadScenario")
@NbBundle.Messages({
    "CTL_ActionLoadScenario=Load scenario",
    "MSG_Loaded= loaded",
    "MSG_Loading=loading ",
    "MSG_LoadError=Loading failed:"
})
public final class ActionLoadScenario implements ActionListener, ITaskListener<LoadScenarioTask> {

    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private ScenarioDataObject scenarioData;
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);
    private StatusDisplayer status = StatusDisplayer.getDefault();
    private Log logger = LogFactory.getLog(getClass());
    private ProgressHandle progres;

    public ActionLoadScenario(ScenarioDataObject context) {
        this.scenarioData = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        execute();
    }

    public void execute() {
        progres = ProgressHandleFactory.createHandle(Bundle.MSG_Loading().concat(scenarioData.getName()));
        progres.start();
        LoadScenarioTask loadTask = new LoadScenarioTask(scenarioData, this);
        loadTask.execute();
    }

    @Override
    public void onTaskCompleted(LoadScenarioTask task) {
        try {
            LoadedScenario scenario = task.get();
            if (scenario != null) {
                app.remove(R.Bean.LOADED_SCENARIO);
                app.put(R.Bean.LOADED_SCENARIO, scenario);
                status.setStatusText(scenarioData.getName() + Bundle.MSG_Loaded());
                view.show(R.Window.SCENARIO);
            }
        } catch (Exception ex) {
            logger.error(ex);
            status.setStatusText(Bundle.MSG_LoadError().concat(ex.getLocalizedMessage()));
        }
        progres.finish();
    }

    @Override
    public void onTaskKilled(LoadScenarioTask task) {
    }

    @Override
    public void onTaskStarted(LoadScenarioTask task) {
    }
}
