package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.model.database.lazyloading.DBMSTupleLoader;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import speedy.SpeedyConstants;
import speedy.exceptions.DBMSException;
import speedy.model.database.AttributeRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TableAlias;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.persistence.Types;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;

public class LunaticDBMSUtility {

    public static final String TEMP_DB_NAME = "testdb";

    public static DBMSTupleLoader createTupleLoader(ResultSet resultSet, String tableName, String virtualTableName, AccessConfiguration configuration) {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            Object oidValue = findOIDColumn(metadata, resultSet);
            if (metadata.getColumnCount() >= 1 && metadata.getColumnName(1).equals(SpeedyConstants.OID)) {
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

    private static Object findOIDColumn(ResultSetMetaData metadata, ResultSet resultSet) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (metadata.getColumnName(i).equalsIgnoreCase(SpeedyConstants.OID)) {
                return resultSet.getObject(i);
            }
        }
        return IntegerOIDGenerator.getNextOID();
    }

    public static AccessConfiguration getTempAccessConfiguration(AccessConfiguration accessConfiguration) {
        AccessConfiguration tmpAccess = new AccessConfiguration();
        tmpAccess.setDriver(accessConfiguration.getDriver());
        tmpAccess.setUri(getTempDBName(accessConfiguration, TEMP_DB_NAME));
        tmpAccess.setLogin(accessConfiguration.getLogin());
        tmpAccess.setPassword(accessConfiguration.getPassword());
        return tmpAccess;
    }

    private static String getTempDBName(AccessConfiguration accessConfiguration, String tempDBName) {
        String uri = accessConfiguration.getUri();
        if (uri.lastIndexOf("/") != -1) {
            return uri.substring(0, uri.lastIndexOf("/") + 1) + tempDBName;
        }
        return uri.substring(0, uri.lastIndexOf(":") + 1) + tempDBName;
    }

    public static String convertDataSourceTypeToDBType(String columnType) {
        if (columnType.equals(Types.DATE)) {
            return "date";
        }
        if (columnType.equals(Types.DATETIME)) {
            return "datetime";
        }
        if (columnType.equals(Types.INTEGER)) {
            return "bigint";
        }
        if (columnType.equals(Types.REAL)) {
            return "float";
        }
        if (columnType.equals(Types.BOOLEAN)) {
            return "bool";
        }
        return "text";
    }

    public static IValue convertDBMSValue(Object attributeValue) {
        IValue value;
        if (attributeValue == null || attributeValue.toString().equalsIgnoreCase(SpeedyConstants.NULL)) {
            value = new NullValue(SpeedyConstants.NULL_VALUE);
        } else if (attributeValue.toString().startsWith(SpeedyConstants.SKOLEM_PREFIX)) {
            value = new NullValue(attributeValue);
        } else if (attributeValue.toString().startsWith(SpeedyConstants.LLUN_PREFIX)) {
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
        String tableAliasScript = LunaticDBMSUtility.tableAliasToSQL(attribureRef.getTableAlias());
        if (!tableAliasScript.isEmpty()) {
            tableAliasScript += LunaticConstants.DELTA_TABLE_SEPARATOR;
        }
        return tableAliasScript + attribureRef.getName();
    }

    public static String attributeRefToSQLDot(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(LunaticDBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(".").append(attributeRef.getName());
        return sb.toString();
    }

    public static String attributeRefToAliasSQL(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(LunaticDBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(LunaticConstants.DELTA_TABLE_SEPARATOR).append(attributeRef.getName());
        return sb.toString();
    }

    public static String cleanRelationName(String name) {
        String clean = name;
        clean = clean.replaceAll("-", LunaticConstants.DELTA_TABLE_SEPARATOR);
        return clean;
    }

    public static void createWorkSchema(AccessConfiguration accessConfiguration, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("DROP SCHEMA IF EXISTS ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(" CASCADE;\n");
        result.append("CREATE SCHEMA ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(";\n\n");
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, false, false);
    }

    public static String getSchemaWithSuffix(AccessConfiguration accessConfiguration, Scenario scenario) {
        return accessConfiguration.getSchemaAndSuffix();
    }

    public static String getWorkSchema(Scenario scenario) {
        String schemaName = SpeedyConstants.WORK_SCHEMA;
        if (scenario.hasSuffix()) {
            schemaName += SpeedyConstants.SUFFIX_SEPARATOR + scenario.getSuffix();
        }
        return schemaName;
    }

}
