package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.algebra.sql.GenerateTargetInsert;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.exceptions.DBMSException;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.dbms.SQLDatabaseManager;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.comparator.StringComparator;

public class ChaseSQLSTTGDsWithThreads implements IChaseSTTGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseSQLSTTGDsWithThreads.class);

    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTreeForStandardChase standardTreeBuilder = new BuildAlgebraTreeForStandardChase();
    private final GenerateTargetInsert targetInsertQuery = new GenerateTargetInsert();
    private IDatabaseManager databaseManager = new SQLDatabaseManager();

    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isDBMS()) {
            throw new DBMSException("Unable to generate SQL: data sources are not on a dbms");
        }
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating script for st tgds on scenario: " + scenario);
        IDatabase source = scenario.getSource();
        IDatabase target = scenario.getTarget();
        databaseManager.initDatabase(source, target, cleanTarget, scenario.getConfiguration().isPreventInsertDuplicateTuples());
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chasing scenario for s-t tgds...");
        List<Dependency> stTgds = new ArrayList<Dependency>(scenario.getSTTgds());
        List<Dependency> stTgdsWithLimit1 = selectTGDsWithLimit1(stTgds, scenario);
        stTgds.removeAll(stTgdsWithLimit1);
        if (logger.isDebugEnabled()) logger.debug("ST-TGDs without limit1: " + stTgds);
        if (logger.isDebugEnabled()) logger.debug("ST-TGDs with limit1: " + stTgdsWithLimit1);
        chaseSTTGDsWithoutLimit1(stTgds, scenario);
        chaseSTTGDsWithLimit1(stTgdsWithLimit1, scenario);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chase for s-t tgds completed in " + (end - start) + "ms");
    }

    private void chaseSTTGDsWithoutLimit1(List<Dependency> stTgds, Scenario scenario) {
        Map<String, Set<Dependency>> dependenciesWithSamePremise = groupDependenciesByPremise(stTgds, scenario);
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        ThreadManager threadManager = new ThreadManager(numberOfThreads);
        for (Set<Dependency> dependencies : dependenciesWithSamePremise.values()) {
            ExecuteSTTGDThread execThread = new ExecuteSTTGDThread(dependencies, scenario);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
    }

    private Map<String, Set<Dependency>> groupDependenciesByPremise(List<Dependency> stTgds, Scenario scenario) {
        Map<String, Set<Dependency>> dependenciesWithSamePremiseMap = new HashMap<String, Set<Dependency>>();
        for (Dependency stTgd : stTgds) {
            String premiseString = buildPremiseString(stTgd, scenario);
            Set<Dependency> dependenciesWithSamePremise = dependenciesWithSamePremiseMap.get(premiseString);
            if (dependenciesWithSamePremise == null) {
                dependenciesWithSamePremise = new HashSet<Dependency>();
                dependenciesWithSamePremiseMap.put(premiseString, dependenciesWithSamePremise);
            }
            dependenciesWithSamePremise.add(stTgd);
        }
        return dependenciesWithSamePremiseMap;
    }

    private String buildPremiseString(Dependency dependency, Scenario scenario) {
        return standardTreeBuilder.generateAlgebraForSourceToTargetTGD(dependency, scenario).toString();
    }

    private List<Dependency> selectTGDsWithLimit1(List<Dependency> stTgds, Scenario scenario) {
        List<Dependency> result = new ArrayList<Dependency>();
        if (!scenario.getConfiguration().isChaseRestricted()
                || scenario.getConfiguration().getChaseMode().equals(LunaticConstants.CHASE_PARALLEL_RESTRICTED_SKOLEM)) {
            return result;
        }
        for (Dependency stTgd : stTgds) {
            if (ChaseUtility.isUseLimit1ForTGD(stTgd, scenario)) {
                result.add(stTgd);
            }
        }
        return result;
    }

    private void chaseSTTGDsWithLimit1(List<Dependency> stTgdsWithLimit1, Scenario scenario) {
        if (stTgdsWithLimit1.isEmpty()) {
            return;
        }
        IDatabase source = scenario.getSource();
        IDatabase target = scenario.getTarget();
        Map<Dependency, IAlgebraOperator> treeMap = buildAlgebraTrees(stTgdsWithLimit1, scenario);
        Set<Dependency> unsatisfiedSTTGDs = new HashSet<Dependency>(stTgdsWithLimit1);
        while (!unsatisfiedSTTGDs.isEmpty()) {
            Dependency stTGD = pickNextDependency(unsatisfiedSTTGDs, scenario);
            if (logger.isDebugEnabled()) logger.debug("Executing ST-TGD " + stTGD.getId());
            IAlgebraOperator treeRoot = treeMap.get(stTGD);
            IInsertFromSelectNaive naiveInsert = OperatorFactory.getInstance().getInsertFromSelectNaive(scenario);
            boolean newTuples = naiveInsert.execute(stTGD, treeRoot, source, target, scenario);
            if (!newTuples || !DependencyUtility.hasUniversalVariablesInConclusion(stTGD)) {
                if (logger.isDebugEnabled()) logger.debug("No new tuples. ST-TGD " + stTGD + " is satisfied.");
                unsatisfiedSTTGDs.remove(stTGD);
            }
        }
    }

    private Map<Dependency, IAlgebraOperator> buildAlgebraTrees(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : extTGDs) {
            IAlgebraOperator standardInsert = standardTreeBuilder.generateAlgebraForSourceToTargetTGD(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dependency + "\n" + standardInsert);
            result.put(dependency, standardInsert);
        }
        return result;
    }

    private Dependency pickNextDependency(Set<Dependency> unsatisfiedSTTGDs, Scenario scenario) {
        List<Dependency> sortedList = new ArrayList<Dependency>(unsatisfiedSTTGDs);
        Collections.sort(sortedList, new StringComparator());
        return sortedList.get(0);
    }

    class ExecuteSTTGDThread implements IBackgroundThread {

        private Set<Dependency> dependencies;
        private Scenario scenario;

        public ExecuteSTTGDThread(Set<Dependency> dependencies, Scenario scenario) {
            this.dependencies = dependencies;
            this.scenario = scenario;
        }

        public void execute() {
            if (logger.isDebugEnabled()) logger.debug("Executing thread for st-tgd: " + dependencies);
            Set<Dependency> dependenciesToMaterialize = findDependenciesToMaterialize(dependencies, scenario);
            if (logger.isDebugEnabled()) logger.debug("Dependencies to materialize: " + dependenciesToMaterialize);
            StringBuilder result = new StringBuilder();
            result.append("BEGIN TRANSACTION;\n");
            result.append("SET CONSTRAINTS ALL DEFERRED;\n\n");
            result.append(this.generateMaterializationScript(dependencies, scenario, dependenciesToMaterialize));
            result.append(targetInsertQuery.generateScript(dependencies, dependenciesToMaterialize, scenario));
            result.append("\nCOMMIT;\n");
            result.append("--DROP SCHEMA ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(" CASCADE;\n");
            if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
            DBMSDB target = (DBMSDB) scenario.getTarget();
            AccessConfiguration accessConfiguration = (target).getAccessConfiguration();
            executeScript(result.toString(), accessConfiguration);
        }

        private void executeScript(String script, AccessConfiguration accessConfiguration) {
            try {
                QueryManager.executeScript(script, accessConfiguration, true, true, true, true);
            } catch (DBMSException ex) {
                if (ex.getMessage().contains("ERROR: function bigint_skolem(text) does not exist")
                        || ex.getMessage().contains("ERROR: function double_skolem(text) does not exist")) {
                    if (logger.isDebugEnabled()) logger.debug("Some functions are missing in the current C3p0 thread. Retrying...");
                    executeScript(script, accessConfiguration);
                    return;
                }
                ex.printStackTrace();
//                System.exit(-1);
                throw ex;
            }
        }

        private Set<Dependency> findDependenciesToMaterialize(Set<Dependency> stTgds, Scenario scenario) {
            Set<Dependency> dependenciesToMaterialize = new HashSet<Dependency>();
            Map<String, List<Dependency>> dependenciesWithSamePremiseMap = new HashMap<String, List<Dependency>>();
            for (Dependency stTgd : stTgds) {
                String premiseString = buildPremiseString(stTgd, scenario);
                List<Dependency> dependenciesWithSamePremise = dependenciesWithSamePremiseMap.get(premiseString);
                if (dependenciesWithSamePremise == null) {
                    dependenciesWithSamePremise = new ArrayList<Dependency>();
                    dependenciesWithSamePremiseMap.put(premiseString, dependenciesWithSamePremise);
                }
                dependenciesWithSamePremise.add(stTgd);
                if (!DependencyUtility.isLav(stTgd) && !DependencyUtility.isGav(stTgd)) {
                    dependenciesToMaterialize.add(stTgd);
                }
            }
            for (List<Dependency> list : dependenciesWithSamePremiseMap.values()) {
                if (list.size() > 1) {
                    dependenciesToMaterialize.addAll(list);
                }
            }
            return dependenciesToMaterialize;
        }

        private String generateMaterializationScript(Set<Dependency> stTgds, Scenario scenario, Set<Dependency> dependenciesToMaterialize) {
            StringBuilder result = new StringBuilder();
            result.append("----- Materializing queries of TGDs -----\n");
            for (Dependency dependency : stTgds) {
                if (!dependenciesToMaterialize.contains(dependency)) {
                    continue;
                }
                IAlgebraOperator operator = standardTreeBuilder.generateAlgebraForSourceToTargetTGD(dependency, scenario);
                if (logger.isDebugEnabled()) logger.debug("ST-TGD operator\n" + operator);
                String unloggedOption = (scenario.getConfiguration().isUseUnloggedWorkTables() ? " UNLOGGED " : "");
                result.append("CREATE ").append(unloggedOption).append(" TABLE ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(".").append(dependency.getId()).append(" AS\n");
                result.append(queryBuilder.treeToSQL(operator, scenario.getSource(), scenario.getTarget(), SpeedyConstants.INDENT));
                result.append(";\n\n");
            }
            result.append("\n");
            return result.toString();
        }

    }

}
