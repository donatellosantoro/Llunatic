package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckRedundancy;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
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

public class ChaseDEWithTGDOnlyScenario implements IDEChaser {

    public static final int ITERATION_LIMIT = 10;
    private final static Logger logger = LoggerFactory.getLogger(ChaseDEWithTGDOnlyScenario.class);
    private final CheckRedundancy redundancyChecker = new CheckRedundancy();
    private final AnalyzeDependencies stratificationBuilder = new AnalyzeDependencies();
    private final PartitionLinearTGDs linearTGDPartitioner = new PartitionLinearTGDs();
    private final ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();
    private final AnalyzeDatabase databaseAnalyzer = new AnalyzeDatabase();
    private final IBuildDeltaDB deltaBuilder;
    private final IBuildDatabaseForChaseStep databaseBuilder;
    private final IChaseSTTGDs stChaser;
    private final ChaseExtTGDs extTgdChaser;
    private final ChaseDCs dChaser;
    private final IRemoveDuplicates duplicateRemover;

    public ChaseDEWithTGDOnlyScenario(IChaseSTTGDs stChaser, IRunQuery queryRunner, IInsertFromSelectNaive naiveInsert,
            IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep databaseBuilder, IRemoveDuplicates duplicateRemover) {
        this.stChaser = stChaser;
//        this.extTgdChaser = new ChaseExtTGDs(naiveInsert);
        this.extTgdChaser = new ChaseExtTGDs(naiveInsert);
        this.dChaser = new ChaseDCs(queryRunner);
        this.deltaBuilder = deltaBuilder;
        this.databaseBuilder = databaseBuilder;
        this.duplicateRemover = duplicateRemover;
    }

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        checkDataSources(scenario);
        long start = new Date().getTime();
        try {
            stChaser.doChase(scenario, false);
            IDatabase targetDB = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("-------------------Chasing dependencies on mc scenario: " + scenario);
            stratificationBuilder.prepareDependenciesAndGenerateStratification(scenario);
            linearTGDPartitioner.findLinearTGD(scenario);
            boolean allLinearTGDs = checkIfAllTGDsAreLinear(scenario.getExtTGDs());
            extTgdChaser.doChase(scenario, chaseState);
            if (!scenario.getEGDs().isEmpty()) {
                int iterations = 0;
                while (iterations < ITERATION_LIMIT) {
                    if (chaseState.isCancelled()) {
                        ChaseUtility.stopChase(chaseState);
                    }
//                    boolean cellChanges = egdChaser.doChase(scenario, chaseState);
//                    boolean newTuples = extTgdChaser.doChase( scenario, chaseState);
//                    if (!newTuples && !cellChanges) {
//                        break;
//                    } else {
//                        iterations++;
//                    }
                }
            }
            dChaser.doChase(scenario, chaseState);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.TOTAL_TIME, end - start);
            if (scenario.getConfiguration().isExportSolutions()) {
                duplicateRemover.removeDuplicatesModuloOID(targetDB, scenario);
                resultExporter.exportSolutionInSeparateFiles(targetDB, scenario);
            }
            printResult(targetDB);
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

    private void checkDataSources(Scenario scenario) {
        redundancyChecker.checkDuplicateOIDs(scenario);
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, ImmutableChaseState.getInstance());
    }

}
