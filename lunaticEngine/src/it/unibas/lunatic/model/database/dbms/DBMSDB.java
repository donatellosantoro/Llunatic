package it.unibas.lunatic.model.database.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.database.ForeignKey;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.Key;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.util.ArrayList;
import java.util.List;

public class DBMSDB implements IDatabase {

    private AccessConfiguration accessConfiguration;
    private String initDBScript;
    private List<String> tableNames;
    private List<Key> keys;
    private List<ForeignKey> foreignKeys;
    private boolean initialized = false;
    private List<DBMSTable> tables = new ArrayList<DBMSTable>();

    public DBMSDB(AccessConfiguration accessConfiguration) {
        this.accessConfiguration = accessConfiguration;
    }

    public DBMSDB(DBMSDB db, AccessConfiguration accessConfiguration) {
        this.accessConfiguration = accessConfiguration;
        this.initDBScript = db.initDBScript;
        this.tableNames = db.tableNames;
        this.keys = db.keys;
        this.foreignKeys = db.foreignKeys;
        this.initialized = db.initialized;
        this.tables = db.tables;
    }

    private void initDBMS() {
        if (initialized) {
            return;
        }
        if (!DBMSUtility.isDBExists(accessConfiguration)) {
            DBMSUtility.createDB(accessConfiguration);
        }
        if (initDBScript != null && !DBMSUtility.isSchemaExists(accessConfiguration)) {
            QueryManager.executeScript(initDBScript, accessConfiguration, false, true, false);
        }
        initialized = true;
        loadTables();
    }

    private void loadTables() {
        for (String tableName : getTableNames()) {
            tables.add(new DBMSTable(tableName, accessConfiguration));
        }
    }

    public void addTable(ITable table) {
        tables.add((DBMSTable) table);
    }

    public String getName() {
        return this.accessConfiguration.getSchemaName();
    }

    public List<String> getTableNames() {
        initDBMS();
        if (tableNames == null) {
            tableNames = DBMSUtility.loadTableNames(accessConfiguration);
        }
        return tableNames;
    }

    public List<Key> getKeys() {
        initDBMS();
        if (keys == null) {
            keys = DBMSUtility.loadKeys(accessConfiguration);
        }
        return keys;
    }

    public List<Key> getKeys(String table) {
        List<Key> result = new ArrayList<Key>();
        for (Key key : getKeys()) {
            String tableName = key.getAttributes().get(0).getTableName();
            if (tableName.equals(table)) {
                result.add(key);
            }
        }
        return result;
    }

    public List<ForeignKey> getForeignKeys() {
        initDBMS();
        if (foreignKeys == null) {
            foreignKeys = DBMSUtility.loadForeignKeys(accessConfiguration);
        }
        return foreignKeys;
    }

    public List<ForeignKey> getForeignKeys(String table) {
        List<ForeignKey> result = new ArrayList<ForeignKey>();
        for (ForeignKey foreignKey : getForeignKeys()) {
            String tableName = foreignKey.getRefAttributes().get(0).getTableName();
            if (tableName.equals(table)) {
                result.add(foreignKey);
            }
        }
        return result;
    }

    public ITable getTable(String name) {
//        return new DBMSTable(name, accessConfiguration);
        initDBMS();
        for (DBMSTable table : tables) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        throw new IllegalArgumentException("Unable to find table " + name + " in database " + printSchema());
    }

    public ITable getFirstTable() {
        return getTable(getTableNames().get(0));
    }

    public AccessConfiguration getAccessConfiguration() {
        return accessConfiguration;
    }

    public String getInitDBScript() {
        return initDBScript;
    }

    public void setInitDBScript(String initDBScript) {
        this.initDBScript = initDBScript;
    }

    public int getSize() {
        int size = 0;
        for (String tableName : tableNames) {
            DBMSTable table = (DBMSTable) getTable(tableName);
            size += table.getSize();
        }
        return size;
    }

    public IDatabase clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String printSchema() {
        StringBuilder result = new StringBuilder();
        result.append("Schema: ").append(getName()).append(" {\n");
        for (String tableName : getTableNames()) {
            DBMSTable table = (DBMSTable) getTable(tableName);
            result.append(table.printSchema(LunaticConstants.INDENT));
//            table.closeConnection();
        }
        if (!getKeys().isEmpty()) {
            result.append(LunaticConstants.INDENT).append("--------------- Keys: ---------------\n");
            for (Key key : getKeys()) {
                result.append(LunaticConstants.INDENT).append(key).append("\n");
            }
        }
        if (!getForeignKeys().isEmpty()) {
            result.append(LunaticConstants.INDENT).append("----------- Foreign Keys: -----------\n");
            for (ForeignKey foreignKey : getForeignKeys()) {
                result.append(LunaticConstants.INDENT).append(foreignKey).append("\n");
            }
        }
        result.append("}\n");
        return result.toString();
    }

    public String printInstances() {
        return printInstances(false);
    }

    public String printInstances(boolean sort) {
        StringBuilder result = new StringBuilder();
        for (String tableName : getTableNames()) {
            DBMSTable table = (DBMSTable) getTable(tableName);
            if (sort) {
                result.append(table.toStringWithSort(LunaticConstants.INDENT));
            } else {
                result.append(table.toString(LunaticConstants.INDENT));
            }
//            table.closeConnection();
        }
        return result.toString();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
//        result.append(printSchema());
        result.append(printInstances());
        return result.toString();
    }
}
