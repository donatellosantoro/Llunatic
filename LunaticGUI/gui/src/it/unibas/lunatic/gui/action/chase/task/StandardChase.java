package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.DeChaseResult;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.persistence.relational.QueryStatManager;

public class StandardChase implements IChaseOperator {

    private Log logger = LogFactory.getLog(getClass());

    @Override
    public IChaseResult chase(LoadedScenario loadedScenario) {
        reset(loadedScenario);
        IChaseState chaseState = loadedScenario.get(R.BeanProperty.CHASE_STATE, IChaseState.class);
        if (logger.isDebugEnabled()) logger.debug("Executing chase with configuration\n" + loadedScenario.getScenario().getConfiguration());
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
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        ChaseTree result = chaser.doChase(scenario, chaseState);
        return new McChaseResult(ls, result);
    }

    private void reset(LoadedScenario loadedScenario) {
        IntegerOIDGenerator.resetCounter();
        IntegerOIDGenerator.clearCache();
        CellGroupIDGenerator.resetCounter();
        OperatorFactory.getInstance().reset();
        ChaseStats.getInstance().resetStatistics();
        QueryStatManager.getInstance().resetStatistics();
        Scenario scenario = loadedScenario.getScenario();
        if (scenario.getSource() != null && (scenario.getSource() instanceof DBMSDB)) {
            ((DBMSDB) scenario.getSource()).reset();
        }
        if (scenario.getTarget() instanceof DBMSDB) {
            ((DBMSDB) scenario.getTarget()).reset();
        }
    }
}
