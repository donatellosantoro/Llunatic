package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaserResult;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.commons.ImmutableChaseState;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.AnalyzeDependencies;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.operators.IRunQuery;
import speedy.utility.PrintUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.operators.IAnalyzeDatabase;

public class ChaseDEScenario implements IDEChaser {

    public static final int ITERATION_LIMIT = 10;
    private final static Logger logger = LoggerFactory.getLogger(ChaseDEScenario.class);
    private final AnalyzeDependencies dependencyAnalyzer = new AnalyzeDependencies();
    private final ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();
    private final BuildAlgebraTreeForEGD treeBuilderForEGD = new BuildAlgebraTreeForEGD();
    private final ComputeDatabaseSize databaseSizeCalculator = new ComputeDatabaseSize();
    private final IAnalyzeDatabase databaseAnalyzer;
    private final ExecuteFinalQueries finalQueryExecutor;
    private final IBuildDeltaDB deltaBuilder;
    private final IBuildDatabaseForChaseStep databaseBuilder;
    private final IChaseSTTGDs stChaser;
    private final ChaseTargetTGDs tgdChaser;
    private final ChaseDeltaEGDs egdChaser;
    private final ChaseDCs dChaser;

    public ChaseDEScenario(IChaseSTTGDs stChaser, ChaseDeltaEGDs egdChaser, IRunQuery queryRunner, IInsertFromSelectNaive naiveInsert,
            IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep databaseBuilder, IAnalyzeDatabase databaseAnalyze) {
        this.stChaser = stChaser;
        this.tgdChaser = new ChaseTargetTGDs(naiveInsert);
        this.egdChaser = egdChaser;
        this.dChaser = new ChaseDCs(queryRunner);
        this.deltaBuilder = deltaBuilder;
        this.databaseBuilder = databaseBuilder;
        this.finalQueryExecutor = new ExecuteFinalQueries(queryRunner);
        this.databaseAnalyzer = databaseAnalyze;
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        List<Dependency> egds = scenario.getEGDs();
        scenario.setEGDs(new ArrayList<Dependency>());
        scenario.setExtEGDs(egds);
        CostManagerUtility.setDECostManager(scenario);
        analyzeSourceDatabase(scenario);
        long start = new Date().getTime();
        try {
            dependencyAnalyzer.analyzeDependencies(scenario);
            if (scenario.getConfiguration().isPrintStatsOnly()) {
                return scenario.getTarget();
            }
            stChaser.doChase(scenario, false);
            IDatabase targetDB = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("-------------------Chasing dependencies on mc scenario: " + scenario);
            Map<Dependency, IAlgebraOperator> egdQueryMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getExtEGDs(), scenario);
            boolean allInclusionDependencies = checkIfAllTargetTGDsAreInclusionDependencies(scenario.getExtTGDs());
            tgdChaser.doChase(scenario, chaseState);
            List<Dependency> satisfiedEGDs = findSatisfiedEGDs(scenario, targetDB);
            if (!scenario.getExtEGDs().isEmpty() && satisfiedEGDs.size() < scenario.getExtEGDs().size()) {
                int iterations = 0;
                while (true) {
                    if (chaseState.isCancelled()) {
                        ChaseUtility.stopChase(chaseState);
                    }
                    IDatabase deltaDB = deltaBuilder.generate(targetDB, scenario, LunaticConstants.CHASE_STEP_ROOT);
                    if (logger.isDebugEnabled()) logger.debug("DeltaDB: " + deltaDB);
                    ChaseTree chaseTree = new ChaseTree(scenario);
                    DeltaChaseStep root = new DeltaChaseStep(scenario, chaseTree, LunaticConstants.CHASE_STEP_ROOT, targetDB, deltaDB);
                    root.getSatisfiedEGDs().addAll(satisfiedEGDs);
                    ChaserResult egdResult = egdChaser.doChase(root, scenario, chaseState, egdQueryMap);
                    boolean cellChanges = egdResult.isNewNodes();
                    if (!cellChanges) {
                        break;
                    }
                    DeltaChaseStep lastStep = getLastStep(chaseTree.getRoot());
                    targetDB = databaseBuilder.extractDatabaseWithDistinct(lastStep.getId(), lastStep.getDeltaDB(), lastStep.getOriginalDB(), scenario);
                    scenario.setTarget(targetDB);
                    if (allInclusionDependencies) {
                        break;
                    }
                    boolean newTuples = tgdChaser.doChase(scenario, chaseState);
                    if (!newTuples) {
                        break;
                    }
                    iterations++;
                    if (iterations > ITERATION_LIMIT) {
                        throw new ChaseFailedException("Iteration limit reached. Chase might not terminate. Iterations: " + iterations);
                    }
                }
            }
            dChaser.doChase(scenario, chaseState);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.CHASE_TIME, end - start);
            if (scenario.getConfiguration().isExportSolutions()) {
                resultExporter.exportSolutionInSeparateFiles(targetDB, scenario);
            }
            finalQueryExecutor.executeFinalQueries(targetDB, scenario);
            printResult(targetDB);
            scenario.setExtEGDs(new ArrayList<Dependency>());
            scenario.setEGDs(egds);
            return targetDB;
        } catch (ChaseFailedException e) {
            throw e;
        } finally {
            if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        }
    }

    private void printResult(IDatabase targetDB) {
        if (!LunaticConfiguration.isPrintResults()) {
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println(ChaseStats.getInstance().toString());
        long preProcessingTime = 0L;
        long chasingTime = 0L;
        long postProcessingTime = 0L;
        long queryTime = 0L;
        //Pre Processing
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.INIT_DB_TIME));
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.ANALYZE_DB));
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.LOAD_TIME));
//        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.DELTA_DB_BUILDER));
//        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.STEP_DB_BUILDER));
        //Chasing
        chasingTime = LunaticUtility.increaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.CHASE_TIME));
//        chasingTime = LunaticUtility.decreaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.DELTA_DB_BUILDER));
//        chasingTime = LunaticUtility.decreaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.STEP_DB_BUILDER));
        //Post Processing
        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.WRITE_TIME));
//        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.REMOVE_DUPLICATE_TIME));
        //Query
        queryTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.FINAL_QUERY_TIME));
        //Total Processing
        long totalTime = preProcessingTime + chasingTime + postProcessingTime;
        PrintUtility.printInformation("----------------------------------------------------");
        PrintUtility.printInformation("*** PreProcessing time: " + preProcessingTime + " ms");
        PrintUtility.printInformation("*** Chase time: " + chasingTime + " ms");
        PrintUtility.printInformation("*** Query time: " + queryTime + " ms");
        PrintUtility.printInformation("*** PostProcessing time: " + postProcessingTime + " ms");
        PrintUtility.printInformation("*** TOTAL TIME: " + totalTime + " ms");
        PrintUtility.printInformation("----------------------------------------------------");
        if (logger.isDebugEnabled()) printTargetStats(targetDB);
    }

    private void printTargetStats(IDatabase targetDB) {
        System.out.println("");
        System.out.println("Target Database Stats");
        long totalNumberOfTuples = 0;
        boolean printDetails = targetDB.getTableNames().size() < 10;
        for (String tableName : targetDB.getTableNames()) {
            ITable table = targetDB.getTable(tableName);
            long tableSize = databaseSizeCalculator.getTableSize(table);
            totalNumberOfTuples += tableSize;
            if (printDetails) {
                System.out.println("# " + tableName + ": " + tableSize + " tuples");
            }
        }
        Integer numberOfNulls = databaseSizeCalculator.countNulls(targetDB);
        System.out.println("# Number of nulls: " + numberOfNulls);
        System.out.println("### Total Number of Tuples: " + totalNumberOfTuples + " tuples");
    }

    private void analyzeSourceDatabase(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return;
        }
        long start = new Date().getTime();
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        databaseAnalyzer.analyze(scenario.getSource(), numberOfThreads);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.ANALYZE_DB, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Source database analyzed in " + (end - start) + "ms");
    }

    private List<Dependency> findSatisfiedEGDs(Scenario scenario, IDatabase targetDB) {
        List<Dependency> satisfiedEGDs = Collections.synchronizedList(new ArrayList<Dependency>());
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        ThreadManager threadManager = new ThreadManager(numberOfThreads);
        for (Dependency extEGD : scenario.getExtEGDs()) {
            CheckEGDSatisfactionThread execThread = new CheckEGDSatisfactionThread(extEGD, satisfiedEGDs, targetDB, scenario);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
        return satisfiedEGDs;
    }

    private boolean checkIfAllTargetTGDsAreInclusionDependencies(List<Dependency> extTGDs) {
        for (Dependency extTGD : extTGDs) {
            if (!extTGD.isInclusionDependency()) {
                return false;
            }
        }
        return true;
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, ImmutableChaseState.getInstance());
    }

    private DeltaChaseStep getLastStep(DeltaChaseStep node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().get(0);
        }
        return node;
    }

    class CheckEGDSatisfactionThread implements IBackgroundThread {

        private Dependency extEGD;
        private List<Dependency> satisfiedEGDs;
        private IDatabase targetDB;
        private Scenario scenario;

        public CheckEGDSatisfactionThread(Dependency extEGD, List<Dependency> satisfiedEGDs, IDatabase targetDB, Scenario scenario) {
            this.extEGD = extEGD;
            this.satisfiedEGDs = satisfiedEGDs;
            this.targetDB = targetDB;
            this.scenario = scenario;
        }

        public void execute() {
            if (!ChaseUtility.checkEGDSatisfactionWithQuery(extEGD, targetDB, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("EGD " + extEGD + " is violated");
                return;
            }
            satisfiedEGDs.add(extEGD);
        }

    }
}
