package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.filesystems.FileChangeListener;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

public class ScenarioCloseHelper implements ActionListener {

//    private static ScenarioCloseHelper instance;
//
//    private ScenarioCloseHelper() {
//    }
//
//    public static ScenarioCloseHelper getInstance() {
//        if (instance == null) {
//            instance = new ScenarioCloseHelper();
//        }
//        return instance;
//    }
//    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
//    private FileChangeListener scenarioFileChangeListener = new ScenarioFileChangeListener();
//
//    public void closeScenario() {
//    }
//
    @Override
    public void actionPerformed(ActionEvent e) {
//        LoadedScenario loadedScenario = app.get(R.Bean.LOADED_SCENARIO, LoadedScenario.class);
//        if (loadedScenario != null) {
//            closeScenario(loadedScenario);
//        }
    }
//
//    public void closeScenario(LoadedScenario loadedScenario) {
//        loadedScenario.getDataObject().getPrimaryFile().removeFileChangeListener(scenarioFileChangeListener);
//        DataObject partialOrder = loadedScenario.get(R.BeanProperty.PARTIAL_ORDER_SCRIPT, DataObject.class);
//        if (partialOrder != null) {
//            partialOrder.getPrimaryFile().removeFileChangeListener(scenarioFileChangeListener);
//        }
//        app.remove(R.Bean.LOADED_SCENARIO, loadedScenario);
//    }
//
//    public FileChangeListener getScenarioFileChangeListener() {
//        return scenarioFileChangeListener;
//    }
}
