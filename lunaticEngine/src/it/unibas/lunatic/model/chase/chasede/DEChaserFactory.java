package it.unibas.lunatic.model.chase.chasede;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDEScenario;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDEScenarioProxy;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.ChaseSQLSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLDEOccurrenceHandlerWithCache;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryDEOccurrenceHandler;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;

public class DEChaserFactory {
    
    public static IDEChaser getChaser(Scenario scenario) {
        String deChaserStrategy = scenario.getConfiguration().getDeChaser();
        if (deChaserStrategy.equals(LunaticConstants.CLASSIC_DE_CHASER)) {
            return getClassicDEChaser(scenario);
        }
        if (deChaserStrategy.equals(LunaticConstants.PROXY_MC_CHASER)) {
            return getProxyMCChaser(scenario);
        }
        throw new IllegalArgumentException("DE Chaser " + deChaserStrategy + " is not supported");
    }

    private static IDEChaser getProxyMCChaser(Scenario scenario) {
        return new ChaseDEScenarioProxy();
    }

    private static IDEChaser getClassicDEChaser(Scenario scenario) {
        IChaseSTTGDs stChaser;
        IInsertFromSelectNaive naiveInsert;
        IRemoveDuplicates duplicateRemover;
        IValueOccurrenceHandlerDE valueOccurrenceHandler;
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
        IUpdateCell cellUpdater = OperatorFactory.getInstance().getCellUpdater(scenario);
        if (scenario.isMainMemory()) {
            stChaser = new ChaseMainMemorySTTGDs();
            naiveInsert = new MainMemoryInsertFromSelectNaive();
            duplicateRemover = new MainMemoryRemoveDuplicates();
            valueOccurrenceHandler = new MainMemoryDEOccurrenceHandler();
        } else if (scenario.isDBMS()) {
            stChaser = new ChaseSQLSTTGDs();
            naiveInsert = new SQLInsertFromSelectNaive();
            duplicateRemover = new SQLRemoveDuplicates();
            valueOccurrenceHandler = new SQLDEOccurrenceHandlerWithCache();
        } else {
            throw new IllegalArgumentException("Scenario is not supported");
        }
        return new ChaseDEScenario(stChaser, naiveInsert, duplicateRemover, valueOccurrenceHandler, queryRunner, cellUpdater);
    }
}
