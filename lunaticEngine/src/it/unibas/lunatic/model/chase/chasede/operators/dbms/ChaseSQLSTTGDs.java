package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DBMSException;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.algebra.sql.GenerateTargetInsert;
import it.unibas.lunatic.model.algebra.sql.GenerateTrigger;
import it.unibas.lunatic.model.algebra.sql.MaterializePremiseQueries;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseSQLSTTGDs implements IChaseSTTGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseSQLSTTGDs.class);

    private MaterializePremiseQueries materializeQuery = new MaterializePremiseQueries();
    private GenerateTargetInsert targetInsertQuery = new GenerateTargetInsert();
    private GenerateTrigger triggerGenerator = new GenerateTrigger();

    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isDBMS()) {
            throw new DBMSException("Unable to generate SQL: data sources are not on a dbms");
        }
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating script for st tgds on scenario: " + scenario);
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration accessConfiguration = (target).getAccessConfiguration();
        DBMSUtility.createWorkSchema(accessConfiguration);
        DBMSUtility.deleteSkolemOccurrencesTable(target, accessConfiguration);
        if (scenario.getSTTgds().isEmpty()) {
            return;
        }
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
        result.append("--DROP SCHEMA ").append(LunaticConstants.WORK_SCHEMA).append(" CASCADE;\n");
        if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, true);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
    }

    private String cleanTargetScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Cleaning Target -----\n");
        DBMSDB targetDB = (DBMSDB) scenario.getTarget();
        for (String tableName : scenario.getTarget().getTableNames()) {
            if (tableName.equals(LunaticConstants.SKOLEM_OCC_TABLE)) {
                continue;
            }
            result.append("DELETE FROM ").append(targetDB.getAccessConfiguration().getSchemaName()).append(".").append(tableName).append(";\n");
        }
        result.append("\n");
        return result.toString();
    }
}
