/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario.task;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.ITaskListener;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.action.scenario.PartialOrderFileChangeListener;
import it.unibas.lunatic.gui.action.scenario.ScenarioFileChangeListener;
import it.unibas.lunatic.gui.data.ScenarioDataObject;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import it.unibas.lunatic.persistence.DAOMCScenario;
import java.io.File;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Antonio Galotta
 */
@NbBundle.Messages({
    "MSG_LoadError=Loading failed:"
})
public class LoadScenarioTask extends SwingWorker<LoadedScenario, Object> {

    private Log logger = LogFactory.getLog(getClass());
    private DAOMCScenario daoScenario = new DAOMCScenario();
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private final ScenarioDataObject scenarioData;
    private final ITaskListener<LoadScenarioTask> listener;
    private StatusDisplayer status = StatusDisplayer.getDefault();

    public LoadScenarioTask(ScenarioDataObject scenarioData, ITaskListener<LoadScenarioTask> listener) {
        this.listener = listener;
        this.scenarioData = scenarioData;
    }

    public LoadedScenario loadScenario(ScenarioDataObject scenarioData) {
        try {
            Scenario scenario = daoScenario.loadScenario(scenarioData.getPrimaryEntry().getFile().getPath());
            LoadedScenario loadedScenario = new LoadedScenario(scenarioData, scenario, R.Bean.LOADED_SCENARIO);
            enableScenarioChangeListener(loadedScenario);
            DataObject partialOrderScript = loadPartialOrderScript(scenario);
            if (partialOrderScript != null) {
                enablePartialOrderFileChangeListener(loadedScenario, partialOrderScript);
                loadedScenario.put(R.BeanProperty.PARTIAL_ORDER_SCRIPT, partialOrderScript);
            }
            return loadedScenario;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.trace(e);
            status.setStatusText(Bundle.MSG_LoadError().concat(e.getLocalizedMessage()));
        }
        return null;
    }

    private DataObject loadPartialOrderScript(Scenario scenario) {
        if (scenario.getScriptPartialOrder() != null) {
            try {
                File partialOrderFile = new File(scenario.getScriptPartialOrder().getScriptFile());
                FileObject pofo = FileUtil.toFileObject(partialOrderFile);
                DataObject javascript = DataObject.find(pofo);
                return javascript;
            } catch (DataObjectNotFoundException e) {
                logger.warn(e.getMessage());
                logger.trace(e);
                status.setStatusText(Bundle.MSG_LoadError().concat(e.getLocalizedMessage()));
            }
        }
        return null;
    }

    public void enablePartialOrderFileChangeListener(LoadedScenario ls, DataObject partialOrderScript) {
        PartialOrderFileChangeListener partialOrderFileChangeListener = new PartialOrderFileChangeListener(ls.getDataObject());
        partialOrderScript.getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(partialOrderFileChangeListener, partialOrderScript));
        ls.setPartialOrderFileChangeListener(partialOrderFileChangeListener);
    }

    public void enableScenarioChangeListener(LoadedScenario ls) {
        ScenarioFileChangeListener scenarioChangeListener = new ScenarioFileChangeListener(ls.getDataObject());
        ls.getDataObject().getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(scenarioChangeListener,ls.getDataObject()));
        ls.setScenarioChangeListener(scenarioChangeListener);
    }

    @Override
    protected LoadedScenario doInBackground() throws Exception {
        return loadScenario(scenarioData);
    }

    @Override
    protected void done() {
        listener.onTaskCompleted(this);
    }
}