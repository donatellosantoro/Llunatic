package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class SQLRemoveDuplicates implements IRemoveDuplicates {

    private static Logger logger = LoggerFactory.getLogger(SQLRemoveDuplicates.class);
//    private static String SUFF = "_distinct";

    @Override
    public void removeDuplicatesModuloOID(IDatabase database, Scenario scenario) {
        long start = new Date().getTime();
        DBMSDB dbmsDB = (DBMSDB) database;
        StringBuilder result = new StringBuilder();
        result.append("BEGIN TRANSACTION;\n");
        result.append("SET CONSTRAINTS ALL DEFERRED;\n\n");
        for (String tableName : dbmsDB.getTableNames()) {
            ITable table = dbmsDB.getTable(tableName);
            String schemaName = DBMSUtility.getSchemaNameAndDot(dbmsDB.getAccessConfiguration());
            result.append(removeDuplicatesFromTable(tableName, table.getAttributes(), schemaName));
        }
        result.append("\nCOMMIT;\n");
        if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
        QueryManager.executeScript(result.toString(), dbmsDB.getAccessConfiguration(), true, true, false, false);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.REMOVE_DUPLICATE_TIME, end - start);
    }

    private String removeDuplicatesFromTable(String tableName, List<Attribute> attributes, String schema) {
        StringBuilder result = new StringBuilder();
        result.append("DELETE FROM ").append(schema).append(tableName).append(" WHERE oid NOT IN (\n");
        result.append(" SELECT min(oid) FROM ").append(schema).append(tableName);
        result.append(" GROUP BY ");
        for (Attribute attribute : attributes) {
            if (!attribute.getName().equals(SpeedyConstants.OID)) {
                result.append(attribute.getName());
                result.append(", ");
            }
        }
        LunaticUtility.removeChars(", ".length(), result);
        result.append(");\n");
        return result.toString();
    }
}
