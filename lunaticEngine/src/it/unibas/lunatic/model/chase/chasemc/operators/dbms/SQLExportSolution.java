package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStepMC;
import it.unibas.lunatic.model.chase.chasemc.operators.IExportSolution;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSVirtualDB;
import speedy.model.database.dbms.DBMSVirtualTable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class SQLExportSolution implements IExportSolution {

    private static Logger logger = LoggerFactory.getLogger(SQLExportSolution.class);
    private IBuildDatabaseForChaseStepMC databaseBuilder;

    @Override
    public void export(DeltaChaseStep step, String suffix, Scenario scenario) {
        initializeOperators(scenario);
        AccessConfiguration deltaAccessConfiguration = ((DBMSDB) step.getDeltaDB()).getAccessConfiguration();
        AccessConfiguration newAccessConfiguration = getNewSchemaName((DBMSDB) scenario.getTarget(), suffix);
        DBMSUtility.createSchema(newAccessConfiguration);
        DBMSVirtualDB database = (DBMSVirtualDB) databaseBuilder.extractDatabase(step.getId(), step.getDeltaDB(), step.getOriginalDB(), scenario);
        StringBuilder script = new StringBuilder();
        for (String tableName : database.getTableNames()) {
            DBMSVirtualTable table = (DBMSVirtualTable) database.getTable(tableName);
            String newTable = newAccessConfiguration.getSchemaAndSuffix() + "." + table.getName();
            String deltaTable = DBMSUtility.getSchemaNameAndDot(deltaAccessConfiguration) + table.getName() + table.getSuffix();
            script.append("CREATE TABLE ").append(newTable);
            script.append(" AS SELECT * FROM ").append(deltaTable).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("Script for exporting database:\n" + script);
        QueryManager.executeScript(script.toString(), newAccessConfiguration, true, true, true, false);
    }

    @Override
    public void overrideWorkSchema(DeltaChaseStep step, String suffix, Scenario scenario, boolean cleanPreviousSteps) {
        initializeOperators(scenario);
        DBMSVirtualDB database = (DBMSVirtualDB) databaseBuilder.extractDatabase(step.getId(), step.getDeltaDB(), step.getOriginalDB(), scenario);
        this.overrideWorkSchema(database, step, suffix, scenario, cleanPreviousSteps);
    }

    @Override
    public void overrideWorkSchema(IDatabase database, DeltaChaseStep step, String suffix, Scenario scenario, boolean cleanPreviousSteps) {
        initializeOperators(scenario);
        DBMSVirtualDB virtualDB = (DBMSVirtualDB) database;
        AccessConfiguration previousTargetAccessConfiguration = ((DBMSDB) scenario.getTarget()).getAccessConfiguration();
        AccessConfiguration deltaAccessConfiguration = ((DBMSDB) step.getDeltaDB()).getAccessConfiguration();
        AccessConfiguration newAccessConfiguration = getNewSchemaName((DBMSDB) scenario.getTarget(), suffix);
        Set<String> tablesToKeep = findTablesToKeep(virtualDB);
        if (logger.isDebugEnabled()) logger.debug("Tables to keep: " + tablesToKeep);
        StringBuilder script = new StringBuilder();
        for (String tableName : DBMSUtility.loadTableNames(deltaAccessConfiguration)) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing table " + tableName);
            if (tablesToKeep.contains(tableName)) {
                String newTableName = tableName.replaceAll(virtualDB.getSuffix(), "");
                if (logger.isDebugEnabled()) logger.debug("Renaming table " + tableName + " into " + newTableName);
                script.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(deltaAccessConfiguration)).append(tableName);
                script.append(" RENAME TO ").append(newTableName).append(";\n");
            } else {
                script.append("DROP TABLE ").append(DBMSUtility.getSchemaNameAndDot(deltaAccessConfiguration)).append(tableName).append(" CASCADE;\n");
            }
        }
        script.append("ALTER SCHEMA ").append(deltaAccessConfiguration.getSchemaAndSuffix()).append(" RENAME TO ").append(newAccessConfiguration.getSchemaAndSuffix()).append(";\n");
        if (cleanPreviousSteps && previousTargetAccessConfiguration.hasSuffix()) {
            script.append("DROP SCHEMA ").append(previousTargetAccessConfiguration.getSchemaAndSuffix()).append(" CASCADE;");
        }
        if (logger.isDebugEnabled()) logger.debug("Script for exporting database:\n" + script);
        QueryManager.executeScript(script.toString(), deltaAccessConfiguration, true, true, true, false);
    }

    private AccessConfiguration getNewSchemaName(DBMSDB target, String suffix) {
        AccessConfiguration newSchema = target.getAccessConfiguration().clone();
        newSchema.setSchemaSuffix(suffix);
        return newSchema;
    }

    private Set<String> findTablesToKeep(DBMSVirtualDB database) {
        Set<String> result = new HashSet<String>();
        for (String tableName : database.getTableNames()) {
            result.add(tableName + database.getSuffix());
        }
        return result;
    }

    private void initializeOperators(Scenario scenario) {
        this.databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
    }

}
