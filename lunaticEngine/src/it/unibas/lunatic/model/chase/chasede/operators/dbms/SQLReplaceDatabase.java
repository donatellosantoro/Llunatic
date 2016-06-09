package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IReplaceDatabase;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSVirtualDB;
import speedy.model.database.dbms.DBMSVirtualTable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class SQLReplaceDatabase implements IReplaceDatabase {

    private final static Logger logger = LoggerFactory.getLogger(SQLReplaceDatabase.class);

    public void replaceTargetDB(IDatabase newDatabase, Scenario scenario) {
        DBMSDB targetDB = (DBMSDB) scenario.getTarget();
        AccessConfiguration targetAc = targetDB.getAccessConfiguration();
        DBMSVirtualDB virtualDB = (DBMSVirtualDB) newDatabase;
        AccessConfiguration virtualAc = virtualDB.getAccessConfiguration();
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN TRANSACTION").append(";\n");
        for (String tableName : targetDB.getTableNames()) {
            String newName = tableName + "_" + new Date().getTime();
            sb.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(targetAc)).append(tableName);
            sb.append(" RENAME TO ").append(newName).append(";\n");
            sb.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(targetAc)).append(newName);
            sb.append(" SET SCHEMA ").append(virtualAc.getSchemaName()).append(";\n");
        }
        for (String tableName : virtualDB.getTableNames()) {
            DBMSVirtualTable virtualTable = (DBMSVirtualTable) virtualDB.getTable(tableName);
            String newName = tableName + "_" + new Date().getTime();
            sb.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(virtualAc)).append(virtualTable.getVirtualName());
            sb.append(" RENAME TO ").append(virtualTable.getName()).append(";\n");
            sb.append("ALTER TABLE ").append(DBMSUtility.getSchemaNameAndDot(virtualAc)).append(virtualTable.getName());
            sb.append(" SET SCHEMA ").append(targetAc.getSchemaName()).append(";\n");
        }
        sb.append("COMMIT TRANSACTION").append(";\n");
        if (logger.isDebugEnabled()) logger.debug(sb.toString());
        QueryManager.executeScript(sb.toString(), targetAc, true, true, true, false);
    }

}
