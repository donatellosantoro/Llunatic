package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.commons.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.ITable;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.operators.dbms.RunSQLQueryString;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class ChaseDEScenarioProxy implements IDEChaser {

    private static Logger logger = LoggerFactory.getLogger(ChaseDEScenarioProxy.class);
    private AnalyzeDatabase databaseAnalyzer = new AnalyzeDatabase();

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        ChaseMCScenario mcChaser = ChaserFactory.getChaser(scenario);
        List<Dependency> egds = scenario.getEGDs();
        scenario.setEGDs(new ArrayList<Dependency>());
        scenario.setExtEGDs(egds);
        CostManagerUtility.setDECostManager(scenario);
        DeltaChaseStep chaseStep = mcChaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("----MC result: " + chaseStep);
        if (chaseStep.getNumberOfLeaves() > 1) {
            throw new ChaseException("MCChaser returns more then one solution");
        }
        DeltaChaseStep solution = getSolution(chaseStep);
        if (solution.isInvalid()) {
            throw new ChaseFailedException("Chase fails. No solutions...");
        }
        IBuildDatabaseForChaseStep databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
        long start = new Date().getTime();
        IDatabase result = databaseBuilder.extractDatabaseWithDistinct(solution.getId(), solution.getDeltaDB(), solution.getOriginalDB(), scenario);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.BUILD_SOLUTION_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) PrintUtility.printInformation("*** Writing solution in database time: " + (end - start) + " ms");
        executeFinalQueries(result, scenario);
        if (logger.isDebugEnabled()) logger.debug("----Result of chase: " + result);
        printResult(result);
        scenario.setExtEGDs(new ArrayList<Dependency>());
        scenario.setEGDs(egds);
        return result;
    }

    private void executeFinalQueries(IDatabase result, Scenario scenario) {
        if (scenario.getSQLQueries().isEmpty()) {
            return;
        }
        RunSQLQueryString sqlQueryRunner = new RunSQLQueryString();
        for (SQLQueryString sqlQuery : scenario.getSQLQueries()) {
            long start = new Date().getTime();
            ITupleIterator it = sqlQueryRunner.runQuery(sqlQuery, result);
            long resultSize = SpeedyUtility.getTupleIteratorSize(it);
            it.close();
            long end = new Date().getTime();
            if (LunaticConfiguration.isPrintSteps()) PrintUtility.printInformation("*** Query " + sqlQuery.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize);
            ChaseStats.getInstance().addStat(ChaseStats.FINAL_QUERY_TIME, end - start);
        }
    }

    private void printResult(IDatabase targetDB) {
        if (!LunaticConfiguration.isPrintSteps()) {
            return;
        }
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
        long preProcessingTime = 0L;
        long chasingTime = 0L;
        long postProcessingTime = 0L;
        //Pre Processing
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.LOAD_TIME));
        preProcessingTime = LunaticUtility.increaseIfNotNull(preProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.DELTA_DB_BUILDER));
        //Chasing
        chasingTime = LunaticUtility.increaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.TOTAL_TIME));
        chasingTime = LunaticUtility.decreaseIfNotNull(chasingTime, ChaseStats.getInstance().getStat(ChaseStats.DELTA_DB_BUILDER));
        //Post Processing
        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.WRITE_TIME));
        postProcessingTime = LunaticUtility.increaseIfNotNull(postProcessingTime, ChaseStats.getInstance().getStat(ChaseStats.BUILD_SOLUTION_TIME));
        //Total Processing
        long totalTime = preProcessingTime + chasingTime + postProcessingTime;
        PrintUtility.printInformation("*** PreProcessing time: " + preProcessingTime + " ms");
        PrintUtility.printInformation("*** Chasing time: " + chasingTime + " ms");
        PrintUtility.printInformation("*** PostProcessing time: " + postProcessingTime + " ms");
        PrintUtility.printInformation("*** Total time: " + totalTime + " ms");
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, ImmutableChaseState.getInstance());
    }

    private DeltaChaseStep getSolution(DeltaChaseStep chaseStep) {
        if (chaseStep.isLeaf()) {
            return chaseStep;
        }
        return getSolution(chaseStep.getChildren().get(0));
    }
}
