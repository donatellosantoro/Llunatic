package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.algebra.sql.GenerateTargetInsert;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.ArrayList;
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
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class ChaseSQLSTTGDsWithThreads implements IChaseSTTGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseSQLSTTGDsWithThreads.class);

    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private BuildAlgebraTreeForStandardChase standardTreeBuilder = new BuildAlgebraTreeForStandardChase();
    private final GenerateTargetInsert targetInsertQuery = new GenerateTargetInsert();

    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isDBMS()) {
            throw new DBMSException("Unable to generate SQL: data sources are not on a dbms");
        }
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating script for st tgds on scenario: " + scenario);
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration accessConfiguration = target.getAccessConfiguration();
        LunaticDBMSUtility.createWorkSchema(accessConfiguration, scenario);
        createFunctionsForNumericalSkolems(scenario);
        if (scenario.getConfiguration().isPreventInsertDuplicateTuples()) {
            addTargetUniqueConstraints(target);
        }
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
        if (cleanTarget) {
            QueryManager.executeScript(cleanTargetScript(scenario), accessConfiguration, true, true, true, true);
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chasing scenario for s-t tgds...");
        Map<String, Set<Dependency>> dependenciesWithSamePremise = groupDependenciesByPremise(scenario.getSTTgds(), scenario);
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        ThreadManager threadManager = new ThreadManager(numberOfThreads);
        for (Set<Dependency> dependencies : dependenciesWithSamePremise.values()) {
            ExecuteSTTGDThread execThread = new ExecuteSTTGDThread(dependencies, scenario);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chase for s-t tgds completed in " + (end - start) + "ms");
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
            throw ex;
        }
    }

    private String buildPremiseString(Dependency dependency, Scenario scenario) {
        return getPremiseOperator(dependency, scenario).toString();
    }

    private IAlgebraOperator getPremiseOperator(Dependency dependency, Scenario scenario) {
        if (scenario.getConfiguration().isUseDistinctInSTTGDs()) {
            return standardTreeBuilder.generateAlgebraTreeWithDinstinct(dependency, scenario);
        }
        return treeBuilder.buildTreeForPremise(dependency, scenario);
    }

    private String cleanTargetScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Cleaning Target -----\n");
        DBMSDB targetDB = (DBMSDB) scenario.getTarget();
        for (String tableName : scenario.getTarget().getTableNames()) {
            result.append("DELETE FROM ").append(DBMSUtility.getSchemaNameAndDot(targetDB.getAccessConfiguration())).append(tableName).append(";\n");
        }
        result.append("\n");
        return result.toString();
    }

    private void createFunctionsForNumericalSkolems(Scenario scenario) {
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration targetAccessConfiguration = (target).getAccessConfiguration();
        DBMSUtility.createFunctionsForNumericalSkolem(targetAccessConfiguration);
        AccessConfiguration workAccessConfiguration = targetAccessConfiguration.clone();
        workAccessConfiguration.setSchemaName(SpeedyConstants.WORK_SCHEMA);
        DBMSUtility.createFunctionsForNumericalSkolem(workAccessConfiguration);
    }

    private void addTargetUniqueConstraints(DBMSDB target) {
        StringBuilder sb = new StringBuilder();
        AccessConfiguration ac = target.getAccessConfiguration();
        for (String tableName : target.getTableNames()) {
            sb.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(ac)).append(tableName).append(" ADD UNIQUE (");
            for (Attribute attribute : target.getTable(tableName).getAttributes()) {
                if (attribute.getName().equalsIgnoreCase(SpeedyConstants.OID)) {
                    continue;
                }
                sb.append(attribute.getName()).append(", ");
            }
            SpeedyUtility.removeChars(", ".length(), sb);
            sb.append(") NOT DEFERRABLE INITIALLY IMMEDIATE;\n");
        }
        QueryManager.executeScript(sb.toString(), ac, true, true, true, true);
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
            result.append(this.generateMaterializationScript(scenario, dependenciesToMaterialize));
            result.append(targetInsertQuery.generateScript(dependencies, dependenciesToMaterialize, scenario));
            result.append("\nCOMMIT;\n");
            result.append("--DROP SCHEMA ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(" CASCADE;\n");
            if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
            DBMSDB target = (DBMSDB) scenario.getTarget();
            AccessConfiguration accessConfiguration = (target).getAccessConfiguration();
            executeScript(result.toString(), accessConfiguration);
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

        private String generateMaterializationScript(Scenario scenario, Set<Dependency> dependenciesToMaterialize) {
            StringBuilder result = new StringBuilder();
            result.append("----- Materializing queries of TGDs -----\n");
            for (Dependency dependency : scenario.getSTTgds()) {
                if (!dependenciesToMaterialize.contains(dependency)) {
                    continue;
                }
                IAlgebraOperator operator = getPremiseOperator(dependency, scenario);
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
