package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.algebra.sql.GenerateTargetInsert;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import it.unibas.lunatic.utility.DependencyUtility;
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
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class ChaseSQLSTTGDs implements IChaseSTTGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseSQLSTTGDs.class);

    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private final GenerateTargetInsert targetInsertQuery = new GenerateTargetInsert();

    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isDBMS()) {
            throw new DBMSException("Unable to generate SQL: data sources are not on a dbms");
        }
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating script for st tgds on scenario: " + scenario);
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration accessConfiguration = (target).getAccessConfiguration();
        LunaticDBMSUtility.createWorkSchema(accessConfiguration, scenario);
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chasing scenario for s-t tgds...");
        Set<Dependency> dependenciesToMaterialize = findDependenciesToMaterialize(scenario.getSTTgds(), scenario);
//        Set<Dependency> dependenciesToMaterialize = new HashSet<Dependency>(scenario.getSTTgds());
        if (logger.isDebugEnabled()) logger.debug("Dependencies to materialize: " + dependenciesToMaterialize);
        StringBuilder result = new StringBuilder();
        result.append("BEGIN TRANSACTION;\n");
        result.append("SET CONSTRAINTS ALL DEFERRED;\n\n");
        result.append(this.generateScript(scenario, dependenciesToMaterialize));
        if (cleanTarget) {
            result.append(cleanTargetScript(scenario));
        }
        result.append(targetInsertQuery.generateScript(scenario, dependenciesToMaterialize));
        result.append("\nCOMMIT;\n");
        result.append("--DROP SCHEMA ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(" CASCADE;\n");
        if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, true, false);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chase for s-t tgds completed in " + (end - start) + "ms");
    }

    private Set<Dependency> findDependenciesToMaterialize(List<Dependency> stTgds, Scenario scenario) {
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

    private String buildPremiseString(Dependency dependency, Scenario scenario) {
        IAlgebraOperator operator = treeBuilder.buildTreeForPremise(dependency, scenario);
        return operator.toString();
    }

    private String generateScript(Scenario scenario, Set<Dependency> dependenciesToMaterialize) {
        StringBuilder result = new StringBuilder();
        result.append("----- Materializing queries of TGDs -----\n");
        for (Dependency dependency : scenario.getSTTgds()) {
            if (!dependenciesToMaterialize.contains(dependency)) {
                continue;
            }
            IAlgebraOperator operator = treeBuilder.buildTreeForPremise(dependency, scenario);
            String unloggedOption = (scenario.getConfiguration().isUseUnloggedWorkTables() ? " UNLOGGED " : "");
            result.append("CREATE ").append(unloggedOption).append(" TABLE ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(".").append(dependency.getId()).append(" AS\n");
            result.append(queryBuilder.treeToSQL(operator, scenario.getSource(), scenario.getTarget(), SpeedyConstants.INDENT));
            result.append(";\n\n");
        }
        result.append("\n");
        return result.toString();
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

}
