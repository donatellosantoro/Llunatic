package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;

public class InteractiveChase implements IChaseOperator {

    @Override
    public IChaseResult chase(LoadedScenario loadedScenario) {
        assert loadedScenario.getScenario().isMCScenario();
        McChaseResult prevoiusStep = loadedScenario.get(R.BeanProperty.CHASE_RESULT, McChaseResult.class);
        IChaseState chaseState = loadedScenario.get(R.BeanProperty.CHASE_STATE, IChaseState.class);
        Scenario scenario = loadedScenario.getScenario();
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(prevoiusStep.getResult(), scenario, chaseState);
        return new McChaseResult(loadedScenario, result);
    }
}
