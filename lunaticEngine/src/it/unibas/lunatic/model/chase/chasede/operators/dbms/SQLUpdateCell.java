package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.relational.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLUpdateCell implements IUpdateCell {

    private static Logger logger = LoggerFactory.getLogger(SQLUpdateCell.class);

    @Override
    public void execute(CellRef cellRef, IValue value, IDatabase database) {
        if (logger.isDebugEnabled()) logger.debug("Changing cell " + cellRef + " with new value " + value + " in database " + database);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        AttributeRef attributeRef = cellRef.getAttributeRef();
        query.append(((DBMSDB) database).getAccessConfiguration().getSchemaName()).append(".");
        query.append(cellRef.getAttributeRef().getTableName());
        query.append(" SET ").append(attributeRef.getName()).append("=");
        query.append("'").append(cleanValue(value.toString())).append("'");
        query.append(" WHERE ").append(LunaticConstants.OID).append("=");
        query.append(cellRef.getTupleOID());
        query.append(";");
        if (logger.isDebugEnabled()) logger.debug("Update script: \n" + query.toString());
        QueryManager.executeScript(query.toString(), ((DBMSDB) database).getAccessConfiguration(), true, true, false);
    }

    private String cleanValue(String string) {
        String sqlValue = string;
        sqlValue = sqlValue.replaceAll("'", "''");
        return sqlValue;
    }
}
