package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@ConvertAsProperties(
        dtd = "-//it.unibas.lunatic.gui.window//StepDeps//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = R.Window.UNSATISFIED_DEPENDENCIES,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "bottomSlidingSide", openAtStartup = false)
@NbBundle.Messages({
    "CTL_StepDepsTopComponent=Unsatisfied dependencies",})
public class UnsatisfiedDependenciesTopComponent extends DependenciesView implements ScenarioChangeListener.Target {

    public UnsatisfiedDependenciesTopComponent() {
        super();
        setName(R.Window.UNSATISFIED_DEPENDENCIES);
        setDisplayName(Bundle.CTL_StepDepsTopComponent());
        setToolTipText(Bundle.CTL_StepDepsTopComponent());
    }
    private ScenarioChangeListener scenarioChangeListener = new ScenarioChangeListener();
    private ChaseStepDependenciesListener stepSelectionListener = new ChaseStepDependenciesListener();

    @Override
    public void componentOpened() {
        stepSelectionListener.register(this);
        scenarioChangeListener.register(this);
    }

    @Override
    public void componentClosed() {
        stepSelectionListener.remove();
        scenarioChangeListener.remove();
    }

    @Override
    public void onScenarioChange(LoadedScenario oldScenario, LoadedScenario newScenario) {
        removeRootContext();
        close();
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

    @Override
    public void onScenarioClose(LoadedScenario scenario) {
        removeRootContext();
        this.close();
    }
}
