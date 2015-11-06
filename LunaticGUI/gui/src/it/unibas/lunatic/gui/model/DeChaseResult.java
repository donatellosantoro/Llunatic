package it.unibas.lunatic.gui.model;

import it.unibas.lunatic.gui.R;
import java.util.Arrays;
import java.util.List;
import speedy.model.database.IDatabase;

public class DeChaseResult implements IChaseResult {

    private final LoadedScenario scenario;
    private final IDatabase result;

    public DeChaseResult(LoadedScenario scenario, IDatabase result) {
        this.scenario = scenario;
        this.result = result;
    }

    public IDatabase getResult() {
        return result;
    }

    @Override
    public LoadedScenario getLoadedScenario() {
        return scenario;
    }

    @Override
    public List<String> getWindowsToOpen() {
        return Arrays.asList(new String[]{R.Window.DE_CHASE_RESULT});
    }

    @Override
    public boolean IsDataExchange() {
        return true;
    }
}
