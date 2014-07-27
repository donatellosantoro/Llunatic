package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
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

public class DEDChaserFactory {

    public static IDEDChaser getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser;
        IInsertFromSelectNaive naiveInsert;
        IRemoveDuplicates duplicateRemover;
        IValueOccurrenceHandlerDE valueOccurrenceHandler;
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
        IUpdateCell cellUpdater = OperatorFactory.getInstance().getCellUpdater(scenario);
        IDatabaseManager databaseManager;
        if (scenario.isMainMemory()) {
            stChaser = new ChaseMainMemorySTTGDs();
            naiveInsert = new MainMemoryInsertFromSelectNaive();
            duplicateRemover = new MainMemoryRemoveDuplicates();
            valueOccurrenceHandler = new MainMemoryDEOccurrenceHandler();
            cellUpdater = new MainMemoryUpdateCell();
            databaseManager = new MainMemoryDatabaseManager();
        } else if (scenario.isDBMS()) {
            stChaser = new ChaseSQLSTTGDs();
            naiveInsert = new SQLInsertFromSelectNaive();
            duplicateRemover = new SQLRemoveDuplicates();
            valueOccurrenceHandler = new SQLDEOccurrenceHandlerWithCache();
            cellUpdater = new SQLUpdateCell();
            databaseManager = new SQLDatabaseManager();
        } else {
            throw new IllegalArgumentException("Scenario is not supported");
        }
        return new ChaseDEDScenario(stChaser, naiveInsert, duplicateRemover, valueOccurrenceHandler, queryRunner, cellUpdater, databaseManager);
    }
}
