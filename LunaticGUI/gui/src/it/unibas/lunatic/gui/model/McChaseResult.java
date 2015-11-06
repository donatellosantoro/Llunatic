package it.unibas.lunatic.gui.model;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseTreeRoot;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import java.util.Arrays;
import java.util.List;

public class McChaseResult implements IChaseResult {

    private final LoadedScenario scenario;
    private final ChaseTree result;

    public McChaseResult(LoadedScenario scenario, ChaseTree result) {
        this.scenario = scenario;
        this.result = result;
    }

    public ChaseTree getResult() {
        return result;
    }

    @Override
    public LoadedScenario getLoadedScenario() {
        return scenario;
    }

    @Override
    public List<String> getWindowsToOpen() {
        return Arrays.asList(new String[]{R.Window.MC_CHASE_RESULT, R.Window.MC_CHASE_RESULT_RANKED_SOLUTIONS});
    }

    @Override
    public boolean IsDataExchange() {
        return false;
    }

    private ChaseTreeRoot treeNode;

    public ChaseTreeRoot getNode() {
        if (treeNode == null) {
            treeNode = new ChaseTreeRoot(this);
        }
        return treeNode;
    }
}
