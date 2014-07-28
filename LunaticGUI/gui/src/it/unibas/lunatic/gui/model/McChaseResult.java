package it.unibas.lunatic.gui.model;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseTreeRoot;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;

public class McChaseResult implements IChaseResult {

    private final LoadedScenario scenario;
    private final DeltaChaseStep result;

    public McChaseResult(LoadedScenario scenario, DeltaChaseStep result) {
        this.scenario = scenario;
        this.result = result;
    }

    public DeltaChaseStep getResult() {
        return result;
    }

    @Override
    public LoadedScenario getLoadedScenario() {
        return scenario;
    }

    @Override
    public String getWindowName() {
        return R.Window.MC_CHASE_RESULT;
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
