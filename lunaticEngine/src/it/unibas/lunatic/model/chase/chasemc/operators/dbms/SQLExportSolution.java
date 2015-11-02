package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IExportSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSVirtualDB;
import speedy.model.database.dbms.DBMSVirtualTable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class SQLExportSolution implements IExportSolution {

    private static Logger logger = LoggerFactory.getLogger(SQLExportSolution.class);
    private IBuildDatabaseForChaseStep databaseBuilder;

    @Override
    public void export(DeltaChaseStep step, String suffix, Scenario scenario) {
        initializeOperators(scenario);
        AccessConfiguration deltaAccessConfiguration = ((DBMSDB) step.getDeltaDB()).getAccessConfiguration();
        AccessConfiguration newAccessConfiguration = createNewSchema((DBMSDB) scenario.getTarget(), suffix);
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

    private AccessConfiguration createNewSchema(DBMSDB target, String suffix) {
        AccessConfiguration newSchema = target.getAccessConfiguration().clone();
        newSchema.setSchemaSuffix(suffix);
        DBMSUtility.createSchema(newSchema);
        return newSchema;
    }

    private void initializeOperators(Scenario scenario) {
        this.databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
    }

}
