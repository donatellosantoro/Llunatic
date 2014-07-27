package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.dbms.DBMSTable;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLInsertTuple implements IInsertTuple {

    private static Logger logger = LoggerFactory.getLogger(SQLInsertTuple.class);
    

    public void execute(ITable table, Tuple tuple) {
        DBMSTable dbmsTable = (DBMSTable) table;
        AccessConfiguration accessConfiguration = dbmsTable.getAccessConfiguration();
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO ");
        insertQuery.append(accessConfiguration.getSchemaName()).append(".").append(dbmsTable.getName());
        insertQuery.append(" (");
        for (Cell cell : tuple.getCells()) {
            insertQuery.append(cell.getAttribute()).append(", ");
        }
        LunaticUtility.removeChars(", ".length(), insertQuery);
        insertQuery.append(")");
        insertQuery.append(" VALUES (");
        for (Cell cell : tuple.getCells()) {
            String cellValue = cell.getValue().toString();
            cellValue = cellValue.replaceAll("'", "''");
            insertQuery.append("'").append(cellValue).append("'").append(", ");
        }
        LunaticUtility.removeChars(", ".length(), insertQuery);
        insertQuery.append(");");
        if (logger.isDebugEnabled()) logger.debug("Insert query:\n" + insertQuery.toString());
        QueryManager.executeInsertOrDelete(insertQuery.toString(), ((DBMSTable) table).getAccessConfiguration());
    }
}
