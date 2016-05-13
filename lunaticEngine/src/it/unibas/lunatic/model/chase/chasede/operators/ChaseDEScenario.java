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
import it.unibas.lunatic.model.chase.commons.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.commons.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.AnalyzeDependencies;
import it.unibas.lunatic.model.dependency.operators.PartitionLinearTGDs;
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
import java.util.Map;
import speedy.model.algebra.IAlgebraOperator;

public class ChaseDEScenario implements IDEChaser {

    public static final int ITERATION_LIMIT = 10;
    private final static Logger logger = LoggerFactory.getLogger(ChaseDEScenario.class);
    private final AnalyzeDependencies stratificationBuilder = new AnalyzeDependencies();
    private final PartitionLinearTGDs linearTGDPartitioner = new PartitionLinearTGDs();
    private final ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();
    private final BuildAlgebraTreeForEGD treeBuilderForEGD = new BuildAlgebraTreeForEGD();
    private final AnalyzeDatabase databaseAnalyzer = new AnalyzeDatabase();
    private final ExecuteFinalQueries finalQueryExecutor;
    private final IBuildDeltaDB deltaBuilder;
    private final IBuildDatabaseForChaseStep databaseBuilder;
    private final IChaseSTTGDs stChaser;
    private final ChaseTargetTGDs tgdChaser;
    private final ChaseDeltaEGDs egdChaser;
    private final ChaseDCs dChaser;

    public ChaseDEScenario(IChaseSTTGDs stChaser, ChaseDeltaEGDs egdChaser, IRunQuery queryRunner, IInsertFromSelectNaive naiveInsert,
            IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep databaseBuilder) {
        this.stChaser = stChaser;
        this.tgdChaser = new ChaseTargetTGDs(naiveInsert);
        this.egdChaser = egdChaser;
        this.dChaser = new ChaseDCs(queryRunner);
        this.deltaBuilder = deltaBuilder;
        this.databaseBuilder = databaseBuilder;
        this.finalQueryExecutor = new ExecuteFinalQueries(queryRunner);
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_STTGDS, scenario.getSTTgds().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EGDS, scenario.getEGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EXTGDS, scenario.getExtTGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_DCS, scenario.getDCs().size());
        if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        List<Dependency> egds = scenario.getEGDs();
        scenario.setEGDs(new ArrayList<Dependency>());
        scenario.setExtEGDs(egds);
        CostManagerUtility.setDECostManager(scenario);
        long start = new Date().getTime();
        try {
            stChaser.doChase(scenario, false);
            IDatabase targetDB = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("-------------------Chasing dependencies on mc scenario: " + scenario);
            stratificationBuilder.prepareDependenciesAndGenerateStratification(scenario);
            linearTGDPartitioner.findLinearTGD(scenario);
            Map<Dependency, IAlgebraOperator> egdQueryMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getExtEGDs(), scenario);
            boolean allLinearTGDs = checkIfAllTGDsAreLinear(scenario.getExtTGDs());
            tgdChaser.doChase(scenario, chaseState);
            if (!scenario.getExtEGDs().isEmpty()) {
                int iterations = 0;
                while (true) {
                    if (chaseState.isCancelled()) {
                        ChaseUtility.stopChase(chaseState);
                    }
                    IDatabase deltaDB = deltaBuilder.generate(targetDB, scenario, LunaticConstants.CHASE_STEP_ROOT);
                    if (logger.isDebugEnabled()) logger.debug("DeltaDB: " + deltaDB);
                    ChaseTree chaseTree = new ChaseTree(scenario);
                    DeltaChaseStep root = new DeltaChaseStep(scenario, chaseTree, LunaticConstants.CHASE_STEP_ROOT, targetDB, deltaDB);
                    ChaserResult egdResult = egdChaser.doChase(root, scenario, chaseState, egdQueryMap);
                    boolean cellChanges = egdResult.isNewNodes();
                    if (!cellChanges) {
                        break;
                    }
                    DeltaChaseStep lastStep = getLastStep(chaseTree.getRoot());
                    targetDB = databaseBuilder.extractDatabaseWithDistinct(lastStep.getId(), lastStep.getDeltaDB(), lastStep.getOriginalDB(), scenario);
                    scenario.setTarget(targetDB);
                    if (allLinearTGDs) {
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
            ChaseStats.getInstance().addStat(ChaseStats.TOTAL_TIME, end - start);
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
        if (!LunaticConfiguration.isPrintSteps()) {
            return;
        }
        System.out.println(ChaseStats.getInstance().toString());
        long preProcessingTime = 0L;
        long chasingTime = 0L;
        long postProcessingTime = 0L;
        //Pre Processing
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.LOAD_TIME));
        //Chasing
        chasingTime = LunaticUtility.increaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.TOTAL_TIME));
        //Post Processing
        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.WRITE_TIME));
        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.REMOVE_DUPLICATE_TIME));
        //Total Processing
        long totalTime = preProcessingTime + chasingTime + postProcessingTime;
        PrintUtility.printInformation("*** PreProcessing time: " + preProcessingTime + " ms");
        PrintUtility.printInformation("*** Chasing time: " + chasingTime + " ms");
        PrintUtility.printInformation("*** PostProcessing time: " + postProcessingTime + " ms");
        PrintUtility.printInformation("*** Total time: " + totalTime + " ms");
        printTargetStats(targetDB);
    }

    private void printTargetStats(IDatabase targetDB) {
        System.out.println("");
        System.out.println("Target Database Stats");
        long totalNumberOfTuples = 0;
        boolean printDetails = targetDB.getTableNames().size() < 10;
        for (String tableName : targetDB.getTableNames()) {
            ITable table = targetDB.getTable(tableName);
            long tableSize = databaseAnalyzer.getTableSize(table);
            totalNumberOfTuples += tableSize;
            if (printDetails) {
                System.out.println("# " + tableName + ": " + tableSize + " tuples");
            }
        }
        Integer numberOfNulls = databaseAnalyzer.countNulls(targetDB);
        System.out.println("# Number of nulls: " + numberOfNulls);
        System.out.println("### Total Number of Tuples: " + totalNumberOfTuples + " tuples");
    }

    private boolean checkIfAllTGDsAreLinear(List<Dependency> extTGDs) {
        for (Dependency extTGD : extTGDs) {
            if (!extTGD.isLinearTGD()) {
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

}
