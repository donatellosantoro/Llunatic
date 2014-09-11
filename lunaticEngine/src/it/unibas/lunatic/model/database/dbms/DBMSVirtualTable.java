package it.unibas.lunatic.model.database.dbms;

import it.unibas.lunatic.model.database.lazyloading.DBMSTupleLoaderIterator;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.DBMSException;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.OidTupleComparator;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.lazyloading.ITupleLoader;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DBMSVirtualTable implements ITable {

    private final String tableName;
    private String suffix;
    private AccessConfiguration accessConfiguration;
    private List<Attribute> attributes;
//    private final ITable originalTable;

    public DBMSVirtualTable(ITable originalTable, AccessConfiguration accessConfiguration, String suffix) {
        this.tableName = originalTable.getName();
        this.accessConfiguration = accessConfiguration;
        this.suffix = suffix;
//        this.originalTable = originalTable;
//        initConnection();
    }

    public String getName() {
        return this.tableName;
    }

    public List<Attribute> getAttributes() {
        if (attributes == null) {
            initConnection();
        }
        return attributes;
    }

    public ITupleIterator getTupleIterator() {
        ResultSet resultSet = DBMSUtility.getTableResultSet(tableName + suffix, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public ITupleIterator getTupleIterator(int offset, int limit) {
        String query = getPaginationQuery(offset, limit);
        ResultSet resultSet = QueryManager.executeQuery(query, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public String getPaginationQuery(int offset, int limit) {
        return DBMSUtility.createTablePaginationQuery(tableName + suffix, accessConfiguration, offset, limit);
    }

    public Iterator<ITupleLoader> getTupleLoaderIterator() {
        ResultSet resultSet = DBMSUtility.getTableOidsResultSet(tableName + suffix, accessConfiguration);
        return new DBMSTupleLoaderIterator(resultSet, tableName, tableName + suffix, accessConfiguration);
    }

    public String printSchema(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("VirtualTable: ").append(toShortString()).append("{\n");
        for (Attribute attribute : getAttributes()) {
            result.append(indent).append(LunaticConstants.INDENT);
            result.append(attribute.getName()).append(" ");
            result.append(attribute.getType()).append("\n");
        }
        result.append(indent).append("}\n");
        return result.toString();
    }

    public String toString() {
        return toString("");
    }

    public String toShortString() {
        return this.accessConfiguration.getSchemaName() + "." + this.tableName + suffix;
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("VirtualTable: ").append(toShortString()).append("{\n");
        ITupleIterator iterator = getTupleIterator();
        while (iterator.hasNext()) {
            result.append(indent).append(LunaticConstants.INDENT).append(iterator.next()).append("\n");
        }
        iterator.close();
        result.append(indent).append("}\n");
        return result.toString();
    }

    public String toStringWithSort(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Table: ").append(getName()).append(" {\n");
        ITupleIterator iterator = getTupleIterator();
        List<Tuple> tuples = new ArrayList<Tuple>();
        while (iterator.hasNext()) {
            tuples.add(iterator.next());
        }
        Collections.sort(tuples, new OidTupleComparator());
        for (Tuple tuple : tuples) {
            result.append(indent).append(LunaticConstants.INDENT).append(tuple.toString()).append("\n");
        }
        iterator.close();
        result.append(indent).append("}\n");
        return result.toString();
    }

    private void initConnection() {
        ResultSet resultSet = null;
        try {
            resultSet = DBMSUtility.getTableResultSetForSchema(tableName + suffix, accessConfiguration);
            this.attributes = DBMSUtility.getTableAttributes(resultSet, tableName);
        } catch (SQLException ex) {
            throw new DBMSException("Unable to load table " + tableName + ".\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }

    public int getSize() {
        String query = "SELECT count(*) as count FROM " + accessConfiguration.getSchemaName() + "." + tableName + suffix;
        ResultSet resultSet = null;
        try {
            resultSet = QueryManager.executeQuery(query, accessConfiguration);
            resultSet.next();
            return resultSet.getInt("count");
        } catch (SQLException ex) {
            throw new DBMSException("Unable to execute query " + query + " on database \n" + accessConfiguration + "\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }
}
