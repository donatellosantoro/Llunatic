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
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.dbms.SQLDatabaseManager;
import speedy.persistence.relational.QueryStatManager;
import speedy.utility.DBMSUtility;

public class StandardChase implements IChaseOperator {

    private Log logger = LogFactory.getLog(getClass());

    @Override
    public IChaseResult chase(LoadedScenario loadedScenario) {
        reset(loadedScenario);
        IChaseState chaseState = loadedScenario.get(R.BeanProperty.CHASE_STATE, IChaseState.class);
        if (logger.isDebugEnabled()) logger.debug("Executing chase with configuration\n" + loadedScenario.getScenario().getConfiguration());
        IChaseResult result;
        if (loadedScenario.getScenario().isDEDScenario()) {
            result = chaseDEDScenario(loadedScenario, chaseState);
        } else if (loadedScenario.getScenario().isDEScenario()) {
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

    private IChaseResult chaseDEDScenario(LoadedScenario loadedScenario, IChaseState chaseState) {
        Scenario scenario = loadedScenario.getScenario();
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario, chaseState);
        return new DeChaseResult(loadedScenario, result);
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
        if (scenario.isDBMS() && !scenario.getSTTgds().isEmpty()) {
            //Scenario has STTGDs. Need to clean target in order to avoid interaction btw sequential runs
            if (logger.isDebugEnabled()) logger.debug("Cleaning target");
            DBMSDB source = (DBMSDB) scenario.getSource();
            DBMSDB target = (DBMSDB) scenario.getTarget();
            DBMSUtility.removeSchema(target.getAccessConfiguration().getSchemaName(), source.getAccessConfiguration());
            target.reset();
            target.initDBMS();

        }
    }
}
