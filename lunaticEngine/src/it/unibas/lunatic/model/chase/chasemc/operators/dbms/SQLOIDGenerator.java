package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.DBMSException;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.dbms.DBMSTable;
import it.unibas.lunatic.model.database.mainmemory.datasource.OID;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLOIDGenerator implements IOIDGenerator {

    private static SQLOIDGenerator singleton = new SQLOIDGenerator();
    private Map<String, Long> oidMap = new HashMap<String, Long>();

    private SQLOIDGenerator() {
    }

    public static SQLOIDGenerator getInstance() {
        return singleton;
    }

    @Override
    public void initializeOIDs(IDatabase database) {
        oidMap = new HashMap<String, Long>();
        for (String tableName : database.getTableNames()) {
            DBMSTable table = (DBMSTable) database.getTable(tableName);
            Long maxOID = getMaxOID(table);
            oidMap.put(table.getName(), maxOID);
        }
    }

    @Override
    public OID getNextOID(String tableName) {
        Long lastOID = oidMap.get(tableName);
        if (lastOID == null) {
            throw new IllegalArgumentException("Unable to generate next OID for unknown table " + tableName + " in oidMap " + oidMap);
        }
        Long nextOID = lastOID + 1;
        oidMap.put(tableName, nextOID);
        return new OID(nextOID);
    }

    public void addCounter(String tableName, int size) {
        Long lastOID = oidMap.get(tableName);
        if (lastOID == null) {
            throw new IllegalArgumentException("Unable to generate next OID for unknown table " + tableName + " in oidMap " + oidMap);
        }
        Long nextOID = lastOID + size;
        oidMap.put(tableName, nextOID);
    }

    private Long getMaxOID(DBMSTable table) {
        Long maxOID = 0L;
        String query = "SELECT max(" + LunaticConstants.OID + ") FROM " + table.getAccessConfiguration().getSchemaName() + "." + table.getName();
        ResultSet rs = QueryManager.executeQuery(query, table.getAccessConfiguration());
        try {
            if (rs.next()) {
                maxOID = rs.getLong(1);
            }
        } catch (SQLException ex) {
            throw new DBMSException("Unable to extract max OID from " + table.getName() + ". " + ex.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(rs);
        }
        return maxOID;
    }
}
