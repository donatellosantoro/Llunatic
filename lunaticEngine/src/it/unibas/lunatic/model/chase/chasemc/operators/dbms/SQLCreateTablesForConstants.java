package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import it.unibas.lunatic.model.database.EmptyDB;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.dbms.DBMSTable;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.dependency.ConstantsInFormula;
import it.unibas.lunatic.persistence.Types;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLCreateTablesForConstants implements ICreateTablesForConstants {

    private static Logger logger = LoggerFactory.getLogger(SQLCreateTablesForConstants.class);

    public void createTable(ConstantsInFormula constantsInFormula, Scenario scenario) {
        if (scenario.getSource() instanceof EmptyDB) {
            DBMSDB newSource = createEmptySourceDatabase(scenario);
            scenario.setSource(newSource);
        }
        DBMSDB dbmsSourceDB = (DBMSDB) scenario.getSource();
        String tableName = constantsInFormula.getTableName();
        if (dbmsSourceDB.getTableNames().contains(tableName)) {
            return;
        }
        executeCreateStatement(constantsInFormula, dbmsSourceDB);
        scenario.getAuthoritativeSources().add(tableName);
    }

    private DBMSDB createEmptySourceDatabase(Scenario scenario) {
        AccessConfiguration sourceAccessConfiguration = ((DBMSDB) scenario.getTarget()).getAccessConfiguration().clone();
        sourceAccessConfiguration.setSchemaName("source");
        StringBuilder createSchemaStatement = new StringBuilder();
        createSchemaStatement.append("CREATE SCHEMA ").append(sourceAccessConfiguration.getSchemaName()).append(";\n\n");
        QueryManager.executeScript(createSchemaStatement.toString(), sourceAccessConfiguration, true, true, false);
        return new DBMSDB(sourceAccessConfiguration);
    }

    private void executeCreateStatement(ConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB) {
        String createStatement = generateCreateStatement(constantsInFormula, dbmsSourceDB);
        String insertStatement = generateInsertStatement(constantsInFormula, dbmsSourceDB);
        String statement = createStatement + insertStatement;
        QueryManager.executeScript(statement, dbmsSourceDB.getAccessConfiguration(), true, true, true);
        DBMSTable newConstantTable = new DBMSTable(constantsInFormula.getTableName(), dbmsSourceDB.getAccessConfiguration());
        dbmsSourceDB.addTable(newConstantTable);
    }

    private String generateCreateStatement(ConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB) {
        AccessConfiguration accessConfiguration = dbmsSourceDB.getAccessConfiguration();
        StringBuilder script = new StringBuilder();
        script.append("----- Generating constant table -----\n");
        script.append("CREATE TABLE ").append(accessConfiguration.getSchemaName()).append(".").append(constantsInFormula.getTableName()).append("(").append("\n");
        List<String> attributeNames = constantsInFormula.getAttributeNames();
        List<Object> constantValues = constantsInFormula.getConstantValues();
        for (int i = 0; i < constantValues.size(); i++) {
            String attributeName = attributeNames.get(i);
            Object constantValue = constantValues.get(i);
            String type = LunaticUtility.findType(constantValue);
            String dbmsType = DBMSUtility.convertDataSourceTypeToDBType(type);
            script.append(LunaticConstants.INDENT).append(attributeName).append(" ").append(dbmsType).append(",").append("\n");
        }
        LunaticUtility.removeChars(", ".length(), script);
        script.append(") WITH OIDS;").append("\n\n");
        if (logger.isDebugEnabled()) logger.debug("----Generating constant table: " + script);
        return script.toString();
    }

    private String generateInsertStatement(ConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB) {
        AccessConfiguration accessConfiguration = dbmsSourceDB.getAccessConfiguration();
        StringBuilder script = new StringBuilder();
        script.append("----- Adding tuple in constant table -----\n");
        script.append("INSERT INTO ").append(accessConfiguration.getSchemaName()).append(".").append(constantsInFormula.getTableName()).append(" VALUES(").append("\n");
        List<Object> constantValues = constantsInFormula.getConstantValues();
        for (int i = 0; i < constantValues.size(); i++) {
            Object constantValue = constantValues.get(i);
            String type = LunaticUtility.findType(constantValue);
            String valueString = constantValue.toString();
            if (type.equals(Types.STRING)) {
                valueString = "'" + valueString + "'";
            }
            script.append(LunaticConstants.INDENT).append(valueString).append(",").append("\n");
        }
        LunaticUtility.removeChars(", ".length(), script);
        script.append(");\n");
        if (logger.isDebugEnabled()) logger.debug("----Generating constant table: " + script);
        return script.toString();
    }

}
