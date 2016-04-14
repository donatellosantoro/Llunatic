package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.sql.GenerateTargetInsert;
import it.unibas.lunatic.model.algebra.sql.GenerateTrigger;
import it.unibas.lunatic.model.algebra.sql.MaterializePremiseQueries;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DBMSException;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class ChaseSQLSTTGDs implements IChaseSTTGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseSQLSTTGDs.class);

    private final MaterializePremiseQueries materializeQuery = new MaterializePremiseQueries();
    private final GenerateTargetInsert targetInsertQuery = new GenerateTargetInsert();
    private final GenerateTrigger triggerGenerator = new GenerateTrigger();

    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isDBMS()) {
            throw new DBMSException("Unable to generate SQL: data sources are not on a dbms");
        }
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating script for st tgds on scenario: " + scenario);
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration accessConfiguration = (target).getAccessConfiguration();
        LunaticDBMSUtility.createWorkSchema(accessConfiguration, scenario);
        LunaticDBMSUtility.deleteSkolemOccurrencesTable(target, accessConfiguration, scenario);
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chasing scenario for s-t tgds...");
        StringBuilder result = new StringBuilder();
        result.append("BEGIN TRANSACTION;\n");
        result.append("SET CONSTRAINTS ALL DEFERRED;\n\n");
        result.append(materializeQuery.generateScript(scenario));
        if (cleanTarget) {
            result.append(cleanTargetScript(scenario));
        }
        result.append(triggerGenerator.generateScript(scenario));
        result.append(targetInsertQuery.generateScript(scenario));
        result.append("\nCOMMIT;\n");
        result.append("--DROP SCHEMA ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(" CASCADE;\n");
        if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, true, false);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (LunaticConfiguration.isPrintSteps()) System.out.println("****Chase for s-t tgds completed in " + (end - start) + "ms");
    }

    private String cleanTargetScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Cleaning Target -----\n");
        DBMSDB targetDB = (DBMSDB) scenario.getTarget();
        for (String tableName : scenario.getTarget().getTableNames()) {
            if (tableName.equals(LunaticConstants.SKOLEM_OCC_TABLE)) {
                continue;
            }
            result.append("DELETE FROM ").append(DBMSUtility.getSchemaNameAndDot(targetDB.getAccessConfiguration())).append(tableName).append(";\n");
        }
        result.append("\n");
        return result.toString();
    }
}
