package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.dbms.DBMSTable;
import it.unibas.lunatic.persistence.Types;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLInsertTuple implements IInsertTuple {

    private static Logger logger = LoggerFactory.getLogger(SQLInsertTuple.class);

    public void execute(ITable table, Tuple tuple, IDatabase source, IDatabase target) {
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
            String attributeType = getAttributeType(cell.getAttributeRef(), source, target);
            if (attributeType.equals(Types.STRING)) {
                insertQuery.append("'");
            }
            insertQuery.append(cellValue);
            if (attributeType.equals(Types.STRING)) {
                insertQuery.append("'");
            }
            insertQuery.append(", ");
        }
        LunaticUtility.removeChars(", ".length(), insertQuery);
        insertQuery.append(");");
        if (logger.isDebugEnabled()) logger.debug("Insert query:\n" + insertQuery.toString());
        QueryManager.executeInsertOrDelete(insertQuery.toString(), ((DBMSTable) table).getAccessConfiguration());
    }

    private String getAttributeType(AttributeRef attributeRef, IDatabase source, IDatabase target) {
        ITable table;
        if (attributeRef.isSource()) {
            table = getTable(attributeRef.getTableName(), source);
        } else {
            table = getTable(attributeRef.getTableName(), target);
        }
        if (table == null) {
            //Original table doesn't contain the attribute (delta db attribute)
            return Types.STRING;
        }
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(attributeRef.getName())) {
                return attribute.getType();
            }
        }
        //Original table doesn't contain the attribute (delta db attribute)
        return Types.STRING;
    }

    private ITable getTable(String name, IDatabase database) {
        if(!database.getTableNames().contains(name)){
            return null;
        }
        return database.getTable(name);
    }
}
