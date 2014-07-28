package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.DeChaseResult;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;

public class StandardChase implements IChaseOperator {

    @Override
    public IChaseResult chase(LoadedScenario loadedScenario) {
        IChaseState chaseState = loadedScenario.get(R.BeanProperty.CHASE_STATE, IChaseState.class);
        IChaseResult result;
        if (loadedScenario.getScenario().isDEScenario()) {
            result = chaseDEScenario(loadedScenario, chaseState);
        } else {
            result = chaseMCScenario(loadedScenario, chaseState);
        }
        return result;
    }

    private IChaseResult chaseDEScenario(LoadedScenario ls, IChaseState chaseState) {
        Scenario scenario = ls.getScenario();
        IDEChaser chaser = DEChaserFactory.getChaser(scenario);
        IDatabase result = chaser.doChase(scenario, chaseState);
        return new DeChaseResult(ls, result);
    }

    private IChaseResult chaseMCScenario(LoadedScenario ls, IChaseState chaseState) {
        Scenario scenario = ls.getScenario();
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario, chaseState);
        return new McChaseResult(ls, result);
    }
}
