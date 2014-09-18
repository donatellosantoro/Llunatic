package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.model.database.lazyloading.DBMSTupleLoader;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.exceptions.DBMSException;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.expressions.Expression;
import it.unibas.lunatic.persistence.Types;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBMSUtility {

    private static Logger logger = LoggerFactory.getLogger(DBMSUtility.class);

    public static final String TEMP_DB_NAME = "testdb";
//    private static IConnectionFactory dataSourceDB = new SimpleDbConnectionFactory();

    public static List<String> loadTableNames(AccessConfiguration accessConfiguration) {
        List<String> tableNames = new ArrayList<String>();
        String schemaName = accessConfiguration.getSchemaName();
        ResultSet tableResultSet = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading table names: " + accessConfiguration);
            Connection connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
        }
        return tableNames;
    }

    public static List<Key> loadKeys(AccessConfiguration accessConfiguration) {
        List<Key> result = new ArrayList<Key>();
        String schemaName = accessConfiguration.getSchemaName();
        Connection connection = null;
        ResultSet tableResultSet = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading keys: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (logger.isDebugEnabled()) logger.debug("Searching primary keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getPrimaryKeys(catalog, null, tableName);
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (logger.isDebugEnabled()) logger.debug("Analyzing primary key: " + columnName);
                    if (logger.isDebugEnabled()) logger.debug("Found a Primary Key: " + columnName);
                    Key key = new Key(new AttributeRef(tableName, columnName), true);
                    result.add(key);
                }
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
        }
        return result;
    }

    public static List<ForeignKey> loadForeignKeys(AccessConfiguration accessConfiguration) {
        Map<String, ForeignKey> foreignKeyMap = new HashMap<String, ForeignKey>();
        String schemaName = accessConfiguration.getSchemaName();
        Connection connection = null;
        ResultSet tableResultSet = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading foreign keys: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (logger.isDebugEnabled()) logger.debug("Searching foreign keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getImportedKeys(catalog, null, tableName);
                while (resultSet.next()) {
                    String fkeyName = resultSet.getString("FK_NAME");
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                    String keyPrimaryKey = pkTableName + "." + pkColumnName;
                    String fkTableName = resultSet.getString("FKTABLE_NAME");
                    String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                    String keyForeignKey = fkTableName + "." + fkColumnName;
                    if (logger.isDebugEnabled()) logger.debug("Analyzing Primary Key: " + keyPrimaryKey + " Found a Foreign Key: " + fkColumnName + " in table " + fkTableName);
                    if (logger.isDebugEnabled()) logger.debug("Analyzing foreign key: " + keyForeignKey + " references " + keyPrimaryKey);
                    addFKToMap(foreignKeyMap, fkeyName, new AttributeRef(pkTableName, pkColumnName), new AttributeRef(fkTableName, fkColumnName));
                }
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
        }
        return new ArrayList<ForeignKey>(foreignKeyMap.values());
    }

    private static void addFKToMap(Map<String, ForeignKey> foreignKeyMap, String fkeyName, AttributeRef keyAttribute, AttributeRef refAttribute) {
        ForeignKey fk = foreignKeyMap.get(fkeyName);
        if (fk == null) {
            fk = new ForeignKey(keyAttribute, refAttribute);
            foreignKeyMap.put(fkeyName, fk);
            return;
        }
        fk.getKeyAttributes().add(keyAttribute);
        fk.getRefAttributes().add(refAttribute);
    }

    public static boolean isDBExists(AccessConfiguration accessConfiguration) {
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Checking if db exists: " + accessConfiguration);
            connection = new SimpleDbConnectionFactory().getConnection(accessConfiguration);
//            connection = QueryManager.getConnection(accessConfiguration);
            return true;
        } catch (Exception daoe) {
        } finally {
            QueryManager.closeConnection(connection);
        }
        return false;
    }

    public static boolean isSchemaExists(AccessConfiguration accessConfiguration) {
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Checking if schema exists: " + accessConfiguration);
            connection = new SimpleDbConnectionFactory().getConnection(accessConfiguration);
//            connection = QueryManager.getConnection(accessConfiguration);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet schemaResultSet = databaseMetaData.getSchemas();
            while (schemaResultSet.next()) {
                String schemaName = schemaResultSet.getString("TABLE_SCHEM");
                if (schemaName.equals(accessConfiguration.getSchemaName())) {
                    return true;
                }
            }
        } catch (Exception daoe) {
        } finally {
            QueryManager.closeConnection(connection);
        }
        return false;
    }

    public static void createDB(AccessConfiguration accessConfiguration) {
        AccessConfiguration tempAccessConfiguration = getTempAccessConfiguration(accessConfiguration);
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Creating db: " + accessConfiguration);
            connection = new SimpleDbConnectionFactory().getConnection(tempAccessConfiguration);
//            connection = QueryManager.getConnection(tempAccessConfiguration);
            String createQuery = "create database " + accessConfiguration.getDatabaseName() + ";";
            ScriptRunner scriptRunner = getScriptRunner(connection);
            scriptRunner.setAutoCommit(true);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(new StringReader(createQuery));
        } catch (Exception daoe) {
            throw new DBMSException("Unable to create new database " + accessConfiguration.getDatabaseName() + ".\n" + tempAccessConfiguration + "\n" + daoe.getLocalizedMessage());
        } finally {
            QueryManager.closeConnection(connection);
        }
    }

    public static void emptyTables(AccessConfiguration accessConfiguration) {
        String schemaName = accessConfiguration.getSchemaName();
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Empty tables: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                String emptyQuery = "delete from " + accessConfiguration.getSchemaName() + "." + tableName + ";";
                ScriptRunner scriptRunner = getScriptRunner(connection);
                scriptRunner.setAutoCommit(true);
                scriptRunner.setStopOnError(false);
                scriptRunner.runScript(new StringReader(emptyQuery));
            }
        } catch (Exception sqle) {
            throw new DBMSException("Unable to empty tables.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeConnection(connection);
        }
    }

    private static ScriptRunner getScriptRunner(Connection connection) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setLogWriter(null);
//        scriptRunner.setErrorLogWriter(null);
        return scriptRunner;
    }

    public static ResultSet getTableResultSet(String tableName, AccessConfiguration accessConfiguration) {
        String query = "SELECT " + LunaticConstants.OID + ",* FROM " + accessConfiguration.getSchemaName() + "." + tableName;
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static String createTablePaginationQuery(String tableName, AccessConfiguration accessConfiguration, int offset, int limit) {
        return "SELECT " + LunaticConstants.OID + ",* FROM " + accessConfiguration.getSchemaName() + "." + tableName + " LIMIT " + limit + " OFFSET " + offset;
    }

    public static ResultSet getTableOidsResultSet(String tableName, AccessConfiguration accessConfiguration) {
        String query = "SELECT " + LunaticConstants.OID + " FROM " + accessConfiguration.getSchemaName() + "." + tableName;
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static ResultSet getTupleResultSet(String tableName, TupleOID oid, AccessConfiguration accessConfiguration, Connection c) {
        String query = "SELECT " + LunaticConstants.OID + ",* FROM " + accessConfiguration.getSchemaName() + "." + tableName + " WHERE " + LunaticConstants.OID + "=" + oid.toString();
        return QueryManager.executeQuery(query, c, accessConfiguration);
    }

    public static ResultSet getTableResultSetForSchema(String tableName, AccessConfiguration accessConfiguration) {
        String query = "SELECT " + LunaticConstants.OID + ",* FROM " + accessConfiguration.getSchemaName() + "." + tableName + " LIMIT 0";
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static Tuple createTupleFromQuery(ResultSet resultSet) {
        return createTuple(resultSet, null);
    }

    public static DBMSTupleLoader createTupleLoader(ResultSet resultSet, String tableName, String virtualTableName, AccessConfiguration configuration) {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            Object oidValue = findOIDColumn(metadata, resultSet);
            if (metadata.getColumnCount() >= 1 && metadata.getColumnName(1).equals(LunaticConstants.OID)) {
                oidValue = resultSet.getObject(1);
            }
            TupleOID tupleOID = new TupleOID(oidValue);
            DBMSTupleLoader tuple = new DBMSTupleLoader(tableName, virtualTableName, tupleOID, configuration);
            return tuple;
        } catch (Exception daoe) {
            daoe.printStackTrace();
            throw new DBMSException("Unable to read tuple.\n" + daoe.getLocalizedMessage());
        }
    }

    public static Tuple createTuple(ResultSet resultSet, String tableName) {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int startColumn = 1;
            Object oidValue = findOIDColumn(metadata, resultSet);
            if (metadata.getColumnCount() >= 1 && metadata.getColumnName(1).equals(LunaticConstants.OID)) {
                oidValue = resultSet.getObject(1);
                startColumn = 2;
            }
            if (metadata.getColumnCount() >= 2 && metadata.getColumnName(2).equals(LunaticConstants.OID)) {
                startColumn = 3;
            }
            TupleOID tupleOID = new TupleOID(oidValue);
            Tuple tuple = new Tuple(tupleOID);
            Cell oidCell = new Cell(tupleOID, new AttributeRef(tableName, LunaticConstants.OID), new ConstantValue(tupleOID));
            tuple.addCell(oidCell);
            int columns = metadata.getColumnCount();
            for (int col = startColumn; col <= columns; col++) {
                String attributeName = metadata.getColumnName(col);
                Object attributeValue = resultSet.getObject(col);
                IValue value = convertDBMSValue(attributeValue);
                AttributeRef attributeRef;
                if (tableName != null) {
                    attributeRef = new AttributeRef(tableName, attributeName);
                } else {
                    attributeRef = extractAttributeRef(attributeName);
                }
                Cell cell = new Cell(tupleOID, attributeRef, value);
                tuple.addCell(cell);
            }
            return tuple;
        } catch (Exception daoe) {
            daoe.printStackTrace();
            throw new DBMSException("Unable to read tuple.\n" + daoe.getLocalizedMessage());
        }
    }

    private static Object findOIDColumn(ResultSetMetaData metadata, ResultSet resultSet) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (metadata.getColumnName(i).equalsIgnoreCase(LunaticConstants.OID)) {
                return resultSet.getObject(i);
            }
        }
        return IntegerOIDGenerator.getNextOID();
    }

    public static List<Attribute> getTableAttributes(ResultSet resultSet, String tableName) throws SQLException {
        List<Attribute> result = new ArrayList<Attribute>();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columns = metadata.getColumnCount();
        for (int col = 1; col <= columns; col++) {
            String attributeName = metadata.getColumnName(col);
            String attributeType = metadata.getColumnTypeName(col);
            Attribute attribute = new Attribute(tableName, attributeName, DBMSUtility.convertDBTypeToDataSourceType(attributeType));
            result.add(attribute);
        }
        return result;
    }

    public static AccessConfiguration getTempAccessConfiguration(AccessConfiguration accessConfiguration) {
        AccessConfiguration tmpAccess = new AccessConfiguration();
        tmpAccess.setDriver(accessConfiguration.getDriver());
        tmpAccess.setUri(getTempDBName(accessConfiguration, TEMP_DB_NAME));
        tmpAccess.setLogin(accessConfiguration.getLogin());
        tmpAccess.setPassword(accessConfiguration.getPassword());
        return tmpAccess;
    }

    public static AccessConfiguration getWorkAccessConfiguration(AccessConfiguration accessConfiguration) {
        AccessConfiguration workSchema = accessConfiguration.clone();
        workSchema.setSchemaName(LunaticConstants.WORK_SCHEMA);
        return workSchema;
    }

    private static String getTempDBName(AccessConfiguration accessConfiguration, String tempDBName) {
        String uri = accessConfiguration.getUri();
        if (uri.lastIndexOf("/") != -1) {
            return uri.substring(0, uri.lastIndexOf("/") + 1) + tempDBName;
        }
        return uri.substring(0, uri.lastIndexOf(":") + 1) + tempDBName;
    }

    public static String convertDBTypeToDataSourceType(String columnType) {
        if (columnType.equalsIgnoreCase("varchar") || columnType.equalsIgnoreCase("char")
                || columnType.equalsIgnoreCase("text") || columnType.equalsIgnoreCase("bpchar")
                || columnType.equalsIgnoreCase("bit") || columnType.equalsIgnoreCase("mediumtext")
                || columnType.equalsIgnoreCase("longtext")) {
            return Types.STRING;
        }
        if (columnType.equalsIgnoreCase("serial") || columnType.equalsIgnoreCase("enum")) {
            return Types.STRING;
        }
        if (columnType.equalsIgnoreCase("date")) {
            return Types.DATE;
        }
        if (columnType.equalsIgnoreCase("datetime") || columnType.equalsIgnoreCase("timestamp")) {
            return Types.DATETIME;
        }
        if (columnType.toLowerCase().startsWith("serial") || columnType.toLowerCase().startsWith("int") || columnType.toLowerCase().startsWith("tinyint") || columnType.toLowerCase().startsWith("bigint") || columnType.toLowerCase().startsWith("smallint")) {
            return Types.INTEGER;
        }
        if (columnType.toLowerCase().startsWith("float") || columnType.toLowerCase().startsWith("real") || columnType.toLowerCase().startsWith("float")) {
            return Types.DOUBLE;
        }
        if (columnType.equalsIgnoreCase("bool")) {
            return Types.BOOLEAN;
        }
        return Types.STRING;
    }

    public static IValue convertDBMSValue(Object attributeValue) {
        IValue value;
        if (attributeValue == null) {
            value = new NullValue(LunaticConstants.NULL_VALUE);
        } else if (attributeValue.toString().startsWith(LunaticConstants.SKOLEM_PREFIX)) {
            value = new NullValue(attributeValue);
        } else if (attributeValue.toString().startsWith(LunaticConstants.LLUN_PREFIX)) {
            value = new LLUNValue(attributeValue);
        } else {
            value = new ConstantValue(attributeValue);
        }
        return value;
    }

    public static String tableAliasToSQL(TableAlias tableAlias) {
        StringBuilder result = new StringBuilder();
        result.append(tableAlias.isSource() ? "source_" : "");
        result.append(tableAlias.getTableName());
        result.append(tableAlias.getAlias().equals("") ? "" : LunaticConstants.DELTA_TABLE_SEPARATOR + tableAlias.getAlias());
        return result.toString();
    }

    public static String attributeRefToSQL(AttributeRef attribureRef) {
        String tableAliasScript = DBMSUtility.tableAliasToSQL(attribureRef.getTableAlias());
        if (!tableAliasScript.isEmpty()) {
            tableAliasScript += LunaticConstants.DELTA_TABLE_SEPARATOR;
        }
        return tableAliasScript + attribureRef.getName();
    }

    public static String attributeRefToSQLDot(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(DBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(".").append(attributeRef.getName());
        return sb.toString();
    }

    public static String attributeRefToAliasSQL(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(DBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(LunaticConstants.DELTA_TABLE_SEPARATOR).append(attributeRef.getName());
        return sb.toString();
    }

    public static String expressionToSQL(Expression expression) {
        return expressionToSQL(expression, true);
    }

    public static String cleanRelationName(String name) {
        String clean = name;
        clean = clean.replaceAll("-", LunaticConstants.DELTA_TABLE_SEPARATOR);
        return clean;
    }

    public static String expressionToSQL(Expression expression, boolean useAlias) {
        if (logger.isDebugEnabled()) logger.debug("Converting expression " + expression);
        Expression expressionClone = expression.clone();
        String expressionString = expression.toString();
        List<Variable> jepVariables = expressionClone.getJepExpression().getSymbolTable().getVariables();
        List<String> variables = expressionClone.getVariables();
        if (expressionString.startsWith("isNull(") || expressionString.startsWith("isNotNull(")) {
            Variable var = jepVariables.get(0);
            String attributeName = extractAttributeNameFromVariable(var.getDescription().toString(), expressionClone, useAlias);
            var.setDescription(attributeName);
            return expressionClone.toSQLString();
        }
        if (expressionString.startsWith("\"") && expressionString.endsWith("\"") && expressionString.length() > 1) {
            return expressionString.substring(1, expressionString.length() - 1);
        }
        for (String variable : variables) {
            String attributeName = extractAttributeNameFromVariable(variable, expressionClone, useAlias);
            Variable var = expressionClone.getJepExpression().getSymbolTable().getVar(variable);
            if (var == null) {
                var = expressionClone.getJepExpression().getSymbolTable().getVar("Source." + variable);
            }
            if (var != null) {
                var.setDescription(attributeName);
            }
        }
        return expressionClone.toSQLString();
    }

    private static String extractAttributeNameFromVariable(String variable, Expression expression, boolean useAlias) {
        if (logger.isDebugEnabled()) logger.debug("Extracting attribute name for variable " + variable + " in expression " + expression);
        Variable variableExpression = expression.getJepExpression().getVar(variable);
        for (Variable var : expression.getJepExpression().getSymbolTable().getVariables()) {
            if (var.getDescription() != null && var.getDescription().toString().equals(variable)) {
                variableExpression = var;
            }
        }
        if (variableExpression == null) {
            throw new IllegalArgumentException("Unknow variable " + variable + " in expression " + expression);
//            return variable;
        }
        Object objectVariable = variableExpression.getDescription();
        if (objectVariable instanceof FormulaVariable) {
            FormulaVariable variableDescription = (FormulaVariable) variableExpression.getDescription();
            FormulaVariableOccurrence occurrence = variableDescription.getPremiseRelationalOccurrences().get(0);
            if (logger.isDebugEnabled()) logger.debug("Occurrences: " + variableDescription.getPremiseRelationalOccurrences());
            AttributeRef attributeRef = occurrence.getAttributeRef();
            String result;
            if (useAlias) {
                result = DBMSUtility.attributeRefToSQLDot(attributeRef);
            } else {
                result = DBMSUtility.attributeRefToSQL(attributeRef);
            }
            if (logger.isDebugEnabled()) logger.debug("Return " + result);
            return result;
        }
        if (objectVariable instanceof AttributeRef) {
            AttributeRef attributeRef = (AttributeRef) objectVariable;
            String result;
            if (useAlias) {
                result = DBMSUtility.attributeRefToSQLDot(attributeRef);
            } else {
                result = DBMSUtility.attributeRefToSQL(attributeRef);
            }
            if (logger.isDebugEnabled()) logger.debug("Return " + result);
            return result;
        }
        if (logger.isDebugEnabled()) logger.debug("Return " + variable);
        return variable;
    }

    private static AttributeRef extractAttributeRef(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Unable to extract attribute from empty string");
        }
        boolean source = false;
        if (value.startsWith("source_")) {
            source = true;
            value = value.substring("source_".length());
        }
        int lastIndex = value.lastIndexOf(LunaticConstants.DELTA_TABLE_SEPARATOR);
        if (lastIndex == -1) {
            return new AttributeRef("", value);
//            throw new IllegalArgumentException("Unable to extract attribute from string " + value);
        }
        String tableAliasSQL = value.substring(0, lastIndex);
        String attributeSQL = value.substring(lastIndex + LunaticConstants.DELTA_TABLE_SEPARATOR.length());
        TableAlias tableAlias;
        if (tableAliasSQL.contains(LunaticConstants.DELTA_TABLE_SEPARATOR)) {
            int firstIndex = tableAliasSQL.indexOf(LunaticConstants.DELTA_TABLE_SEPARATOR);
            String tableName = tableAliasSQL.substring(0, firstIndex);
            String alias = tableAliasSQL.substring(firstIndex + LunaticConstants.DELTA_TABLE_SEPARATOR.length());
            tableAlias = new TableAlias(tableName, alias);
        } else {
            tableAlias = new TableAlias(tableAliasSQL);
        }
        tableAlias.setSource(source);
        AttributeRef attributeRef = new AttributeRef(tableAlias, attributeSQL);
        return attributeRef;
    }

    public static void createWorkSchema(AccessConfiguration accessConfiguration) {
        StringBuilder result = new StringBuilder();
        result.append("DROP SCHEMA IF EXISTS ").append(LunaticConstants.WORK_SCHEMA).append(" CASCADE;\n");
        result.append("CREATE SCHEMA ").append(LunaticConstants.WORK_SCHEMA).append(";\n\n");
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, false);
    }

    public static void deleteSkolemOccurrencesTable(DBMSDB targetDB, AccessConfiguration accessConfiguration) {
        StringBuilder result = new StringBuilder();
        result.append("----- Removing Existing Skolem Table -----\n");
//        result.append("DROP TABLE ").append(targetDB.getAccessConfiguration().getSchemaName()).append(".");
        result.append("DROP TABLE IF EXISTS ").append(LunaticConstants.WORK_SCHEMA).append(".");
        result.append(LunaticConstants.SKOLEM_OCC_TABLE).append(";\n");
        for (String tableName : targetDB.getTableNames()) {
            if (tableName.equals(LunaticConstants.SKOLEM_OCC_TABLE)) {
                continue;
            }
            result.append("DROP TRIGGER IF EXISTS trigg_update_skolem_").append(tableName);
            result.append(" ON ").append(targetDB.getAccessConfiguration().getSchemaName()).append(".").append(tableName).append(";").append("\n\n");
        }
        result.append("\n");
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, false);
    }
}
