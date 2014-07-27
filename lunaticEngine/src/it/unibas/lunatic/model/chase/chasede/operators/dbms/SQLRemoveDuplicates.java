package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLRemoveDuplicates implements IRemoveDuplicates {

    private static Logger logger = LoggerFactory.getLogger(SQLRemoveDuplicates.class);
//    private static String SUFF = "_distinct";

    @Override
    public void removeDuplicatesModuloOID(IDatabase database) {
        DBMSDB dbmsDB = (DBMSDB)database;
        StringBuilder result = new StringBuilder();
        result.append("BEGIN TRANSACTION;\n");
        result.append("SET CONSTRAINTS ALL DEFERRED;\n\n");
        for (String tableName : dbmsDB.getTableNames()) {
            ITable table = dbmsDB.getTable(tableName);
            result.append(removeDuplicatesFromTable(tableName, table.getAttributes(), dbmsDB.getAccessConfiguration().getSchemaName()));
        }
        result.append("\nCOMMIT;\n");
        if (logger.isDebugEnabled()) logger.debug("----Script for STTGDs: " + result);
        QueryManager.executeScript(result.toString(), dbmsDB.getAccessConfiguration(), true, false, false);
    }

    private String removeDuplicatesFromTable(String tableName, List<Attribute> attributes, String schema) {
        StringBuilder result = new StringBuilder();
        result.append("DELETE FROM ").append(schema).append(".").append(tableName).append(" WHERE oid NOT IN (\n");
        result.append(" SELECT min(oid) FROM ").append(schema).append(".").append(tableName);
        result.append(" GROUP BY ");
        for (Attribute attribute : attributes) {
            if (!attribute.getName().equals(LunaticConstants.OID)) {
                result.append(attribute.getName());
                result.append(", ");
            }
        }
        LunaticUtility.removeChars(", ".length(), result);
        result.append(");\n");
        return result.toString();
    }
    
//    private String removeDuplicatesFromTable(String tableName, List<Attribute> attributes, String schema){
//        StringBuilder result = new StringBuilder();
//        result.append("CREATE TABLE ").append(schema).append(".").append(tableName).append(SUFF).append(" WITH OIDS AS\n");
//        result.append("SELECT DISTINCT * FROM ").append(schema).append(".").append(tableName).append(";\n\n");
//        result.append("DROP TABLE ").append(schema).append(".").append(tableName).append(";\n");
//        result.append("ALTER TABLE ").append(schema).append(".").append(tableName).append(SUFF);
//        result.append(" RENAME TO ").append(tableName).append(";\n");
//        return result.toString();
//    }
}
