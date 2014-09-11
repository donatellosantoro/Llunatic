package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.ChaseSQLSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLDEOccurrenceHandlerWithCache;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryDEOccurrenceHandler;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryUpdateCell;
import it.unibas.lunatic.model.chase.chaseded.dbms.SQLDatabaseManager;
import it.unibas.lunatic.model.chase.chaseded.mainmemory.MainMemoryDatabaseManager;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;

public class DEDChaserFactory {

    public static IDEDChaser getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
        IDEChaser deChaser = DEChaserFactory.getChaser(scenario);
        IDatabaseManager databaseManager = OperatorFactory.getInstance().getDatabaseManager(scenario);
        return new ChaseDEDScenarioGreedy(stChaser, deChaser, databaseManager);
    }
}
