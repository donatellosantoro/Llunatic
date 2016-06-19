package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@ConvertAsProperties(
        dtd = "-//it.unibas.lunatic.gui.window//Deps//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = R.Window.DEPENDENCIES,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "bottomSlidingSide", openAtStartup = true)
//@ActionID(category = "Window", id = R.Window.DEPENDENCIES)
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
//@TopComponent.OpenActionRegistration(
//        displayName = "#CTL_DepsAction",
//        preferredID = R.Window.DEPENDENCIES)
@NbBundle.Messages({
    "CTL_DepsAction=Dependencies",
    "CTL_DepsTopComponent=Dependencies",
    "HINT_DepsTopComponent=Shows the list of dependencies"
})
public class DependenciesTopComponent extends DependenciesView implements ScenarioChangeListener.Target {

    private ScenarioChangeListener scenarioChangeListener = new ScenarioChangeListener();
    private ScenarioDependenciesListener scenarioListener = new ScenarioDependenciesListener();

    public DependenciesTopComponent() {
        super();
        setName(R.Window.DEPENDENCIES);
        setDisplayName(Bundle.CTL_DepsTopComponent());
        setToolTipText(Bundle.HINT_DepsTopComponent());

    }

    @Override
    public void componentOpened() {
        scenarioListener.register(this);
        scenarioChangeListener.register(this);
    }

    @Override
    public void componentClosed() {
        scenarioListener.remove();
        scenarioChangeListener.remove();
    }

    @Override
    public void onScenarioChange(LoadedScenario oldScenario, LoadedScenario newScenario) {
        removeRootContext();
    }

    @Override
    public void onScenarioClose(LoadedScenario scenario) {
        removeRootContext();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.3");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
