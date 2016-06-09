package it.unibas.lunatic;

import it.unibas.lunatic.model.chase.chasede.operators.ChangeCellDE;
import it.unibas.lunatic.model.chase.chasede.operators.ChangeCellDEProxy;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDeltaEGDs;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.OccurrenceHandlerDEProxy;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryUpdateCell;
import it.unibas.lunatic.model.chase.chaseded.IDEDDatabaseManager;
import it.unibas.lunatic.model.chase.chaseded.dbms.SQLDEDDatabaseManager;
import it.unibas.lunatic.model.chase.chaseded.mainmemory.MainMemoryDEDDatabaseManager;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import it.unibas.lunatic.model.chase.chasemc.operators.ChangeCell;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeToString;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckUnsatisfiedDependencies;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChangeCell;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.IExportSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.GreedyJCSCacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.GreedySingleStepJCSCacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLExportSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryOIDGenerator;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.persistence.relational.GenerateModifiedCells;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.IBatchInsert;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.mainmemory.MainMemoryBatchInsert;
import speedy.model.algebra.operators.mainmemory.MainMemoryDelete;
import speedy.model.algebra.operators.mainmemory.MainMemoryInsertTuple;
import speedy.model.algebra.operators.sql.SQLBatchInsert;
import speedy.model.algebra.operators.sql.SQLDelete;
import speedy.model.algebra.operators.sql.SQLInsertTuple;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.IRunQuery;
import speedy.model.database.operators.dbms.SQLDatabaseManager;
import speedy.model.database.operators.dbms.SQLRunQuery;
import speedy.model.database.operators.mainmemory.MainMemoryDatabaseManager;
import speedy.model.database.operators.mainmemory.MainMemoryRunQuery;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.IReplaceDatabase;
import it.unibas.lunatic.model.chase.chasede.operators.OccurrenceHandlerDE;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.BuildSQLDBForChaseStepDE;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.BuildSQLDeltaDBDE;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.ChaseSQLSTTGDsWithThreads;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLReplaceDatabase;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.BuildMainMemoryDBForChaseStepDE;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.BuildMainMemoryDeltaDBDE;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryReplaceDatabase;
import speedy.model.database.operators.IAnalyzeDatabase;
import speedy.model.database.operators.dbms.SQLAnalyzeDatabase;
import speedy.model.database.operators.mainmemory.MainMemoryAnalyzeDatabase;

public class OperatorFactory {

    private static Logger logger = LoggerFactory.getLogger(OperatorFactory.class);
    private static OperatorFactory singleton = new OperatorFactory();
    //
    private IRunQuery mainMemoryQueryRunner = new MainMemoryRunQuery();
    private IRunQuery sqlQueryRunner = new SQLRunQuery();
    //
    private IInsertTuple mainMemoryInsertTuple = new MainMemoryInsertTuple();
    private IInsertTuple sqlInsertTuple = new SQLInsertTuple();
    //
    private IBatchInsert mainMemoryBatchInsertOperator = new MainMemoryBatchInsert();
    private IBatchInsert sqlBatchInsertOperator = new SQLBatchInsert();
    //
    private IDelete mainMemoryDeleteOperator = new MainMemoryDelete();
    private IDelete sqlDeleteOperator = new SQLDelete();
    //
    private IUpdateCell mainMemoryCellUpdater = new MainMemoryUpdateCell();
    private IUpdateCell sqlCellUpdater = new SQLUpdateCell();
    //
    private IBuildDeltaDB mainMemoryDeltaBuilder = new BuildMainMemoryDeltaDB();
    private IBuildDeltaDB sqlDeltaBuilder = new BuildSQLDeltaDB();
    //
    private IBuildDeltaDB mainMemoryDeltaBuilderDE = new BuildMainMemoryDeltaDBDE();
    private IBuildDeltaDB sqlDeltaBuilderDE = new BuildSQLDeltaDBDE();
    //
    private IChaseSTTGDs mainMemorySTTGDsChaser = new ChaseMainMemorySTTGDs();
    private IChaseSTTGDs sqlSTTGDsChaser = new ChaseSQLSTTGDsWithThreads();
//    private IChaseSTTGDs sqlSTTGDsChaser = new ChaseSQLSTTGDs();
    //
    private IOIDGenerator mainMemoryOIDGenerator = new MainMemoryOIDGenerator();
    private IOIDGenerator sqlOIDGenerator = SQLOIDGenerator.getInstance();
    //
    private IDatabaseManager mainMemoryDatabaseManager = new MainMemoryDatabaseManager();
    private IDatabaseManager sqlDatabaseManager = new SQLDatabaseManager();
    //
    private IDEDDatabaseManager mainMemoryDEDDatabaseManager = new MainMemoryDEDDatabaseManager();
    private IDEDDatabaseManager sqlDEDDatabaseManager = new SQLDEDDatabaseManager();
    //
    private IExportSolution sqlSolutionExporter = new SQLExportSolution();
    //
    private Map<Scenario, IOccurrenceHandler> occurrenceHandlerMap = new HashMap<Scenario, IOccurrenceHandler>();

    private OperatorFactory() {
    }

    public static OperatorFactory getInstance() {
        return singleton;
    }

    public void reset() {
        if (logger.isDebugEnabled()) logger.debug("Resetting occurrence handler map...");
        this.occurrenceHandlerMap.clear();
        SimilarityFactory.getInstance().reset();
    }

    public IRunQuery getQueryRunner(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryQueryRunner;
        }
        return sqlQueryRunner;
    }

    public IInsertTuple getInsertTuple(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryInsertTuple;
        }
        return sqlInsertTuple;
    }

    public IBatchInsert getSingletonBatchInsertOperator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryBatchInsertOperator;
        }
        return sqlBatchInsertOperator;
    }

    public IBuildDatabaseForChaseStep getDatabaseBuilder(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new BuildMainMemoryDBForChaseStep(scenario.getConfiguration().isCheckConsistencyOfDBs());
        }
        return new BuildSQLDBForChaseStep(scenario.getConfiguration().isCheckConsistencyOfDBs());
    }

    public IBuildDatabaseForChaseStep getDatabaseBuilderDE(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new BuildMainMemoryDBForChaseStepDE(scenario.getConfiguration().isCheckConsistencyOfDBs());
        }
        return new BuildSQLDBForChaseStepDE(scenario.getConfiguration().isCheckConsistencyOfDBs());
    }

    public IDelete getDeleteOperator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDeleteOperator;
        }
        return sqlDeleteOperator;
    }

    public IUpdateCell getCellUpdater(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryCellUpdater;
        }
        return sqlCellUpdater;
    }

    public IBuildDeltaDB getDeltaDBBuilder(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDeltaBuilder;
        }
        return sqlDeltaBuilder;
    }

    public IBuildDeltaDB getDeltaDBBuilderDE(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDeltaBuilderDE;
        }
        return sqlDeltaBuilderDE;
    }

    public IChaseSTTGDs getSTChaser(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemorySTTGDsChaser;
        }
        return sqlSTTGDsChaser;
    }

    public IOIDGenerator getOIDGenerator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryOIDGenerator;
        }
        return sqlOIDGenerator;
    }

    public IDatabaseManager getDatabaseManager(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDatabaseManager;
        }
        return sqlDatabaseManager;
    }

    public IDEDDatabaseManager getDEDDatabaseManager(Scenario scenario) {
        if (!scenario.isDEDScenario()) {
            throw new IllegalArgumentException("DED DatabaseManager is for DED scenarios only");
        }
        if (scenario.isMainMemory()) {
            return mainMemoryDEDDatabaseManager;
        }
        return sqlDEDDatabaseManager;
    }

    public IOccurrenceHandler getOccurrenceHandler(Scenario scenario) {
        IOccurrenceHandler occurrenceHandler = occurrenceHandlerMap.get(scenario);
        if (occurrenceHandler != null) {
            return occurrenceHandler;
        }
        String cacheType = scenario.getConfiguration().getCacheType();
        ICacheManager cacheManager;
        if (cacheType.equals(LunaticConstants.GREEDY_JCS)) {
            cacheManager = new GreedyJCSCacheManager(getQueryRunner(scenario));
        } else if (cacheType.equals(LunaticConstants.GREEDY_SINGLESTEP_JCS_CACHE)) {
            cacheManager = new GreedySingleStepJCSCacheManager(getQueryRunner(scenario));
        } else {
            throw new IllegalArgumentException("Cache manager " + cacheType + " not supported.");
        }
        if (scenario.isDEScenario()) {
            occurrenceHandler = new OccurrenceHandlerDEProxy(getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), cacheManager);
        } else {
            occurrenceHandler = new OccurrenceHandlerMC(getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), cacheManager);
        }
        occurrenceHandlerMap.put(scenario, occurrenceHandler);
        return occurrenceHandler;
    }

    public IChaseDeltaExtTGDs getExtTgdChaser(Scenario scenario) {
        if (scenario.isDEScenario()) {
            throw new IllegalArgumentException("Incompatible operator type for DE Scenario");
        }
        return new ChaseDeltaExtTGDs(getQueryRunner(scenario), getDatabaseBuilder(scenario),
                getOccurrenceHandler(scenario), getOIDGenerator(scenario), getCellChanger(scenario));
    }

    public CheckUnsatisfiedDependencies getUnsatisfiedDependenciesChecker(Scenario scenario) {
        return new CheckUnsatisfiedDependencies(getDatabaseBuilder(scenario), getOccurrenceHandler(scenario), getQueryRunner(scenario));
    }

    public CheckSolution getSolutionChecker(Scenario scenario) {
        return new CheckSolution(getUnsatisfiedDependenciesChecker(scenario), getOccurrenceHandler(scenario), getQueryRunner(scenario), getDatabaseBuilder(scenario));
    }

    public ChaseDeltaExtEGDs getDeltaExtEGDChaser(Scenario scenario) {
        return new ChaseDeltaExtEGDs(getDatabaseBuilder(scenario), getQueryRunner(scenario),
                getInsertTuple(scenario), getSingletonBatchInsertOperator(scenario), getCellChanger(scenario),
                getOccurrenceHandler(scenario), getUnsatisfiedDependenciesChecker(scenario));
    }

    public ChaseDeltaEGDs getDeltaEGDChaser(Scenario scenario) {
        return new ChaseDeltaEGDs(getDatabaseBuilderDE(scenario), getQueryRunner(scenario),
                getInsertTuple(scenario), getSingletonBatchInsertOperator(scenario), getCellChangerDE(scenario),
                getOccurrenceHandlerDE(scenario), getUnsatisfiedDependenciesChecker(scenario));
    }

    public IChangeCell getCellChanger(Scenario scenario) {
        if (scenario.isDEScenario()) {
            return new ChangeCellDEProxy(getInsertTuple(scenario), getSingletonBatchInsertOperator(scenario), getOccurrenceHandler(scenario));
        }
        return new ChangeCell(getInsertTuple(scenario), getSingletonBatchInsertOperator(scenario), getOccurrenceHandler(scenario));
    }

    public IChangeCell getCellChangerDE(Scenario scenario) {
        return new ChangeCellDE(getInsertTuple(scenario), getSingletonBatchInsertOperator(scenario), getOccurrenceHandlerDE(scenario));
    }

    public IOccurrenceHandler getOccurrenceHandlerDE(Scenario scenario) {
        IOccurrenceHandler occurrenceHandler = occurrenceHandlerMap.get(scenario);
        if (occurrenceHandler != null) {
            return occurrenceHandler;
        }
        String cacheType = scenario.getConfiguration().getCacheType();
        ICacheManager cacheManager;
        if (cacheType.equals(LunaticConstants.GREEDY_JCS)) {
            cacheManager = new GreedyJCSCacheManager(getQueryRunner(scenario));
        } else if (cacheType.equals(LunaticConstants.GREEDY_SINGLESTEP_JCS_CACHE)) {
            cacheManager = new GreedySingleStepJCSCacheManager(getQueryRunner(scenario));
        } else {
            throw new IllegalArgumentException("Cache manager " + cacheType + " not supported.");
        }
        occurrenceHandler = new OccurrenceHandlerDE(getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), cacheManager);
        occurrenceHandlerMap.put(scenario, occurrenceHandler);
        return occurrenceHandler;
    }

    public AddUserNode getUserNodeCreator(Scenario scenario) {
        return new AddUserNode(getCellChanger(scenario), getOccurrenceHandler(scenario));
    }

    public ChaseTreeToString getChaseTreeToString(Scenario scenario) {
        return new ChaseTreeToString(getDatabaseBuilder(scenario), getOccurrenceHandler(scenario));
    }

    public IInsertFromSelectNaive getInsertFromSelectNaive(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryInsertFromSelectNaive();
        }
        return new SQLInsertFromSelectNaive();
    }

    public IRemoveDuplicates getDuplicateRemover(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryRemoveDuplicates();
        }
        return new SQLRemoveDuplicates();
    }

    public IAnalyzeDatabase getDatabaseAnalyzer(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryAnalyzeDatabase();
        }
        return new SQLAnalyzeDatabase();
    }

    public IReplaceDatabase getDatabaseReplacer(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryReplaceDatabase();
        }
        return new SQLReplaceDatabase();
    }

    public IExportSolution getSolutionExporter(Scenario scenario) {
        if (scenario.isMainMemory()) {
            throw new IllegalArgumentException("Not supported yet");
        }
        return sqlSolutionExporter;
    }

    public GenerateModifiedCells getGenerateModifiedCells(Scenario scenario) {
        return new GenerateModifiedCells(getOccurrenceHandler(scenario));
    }
}
