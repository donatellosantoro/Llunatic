package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSTable;
import it.unibas.lunatic.model.dependency.AllConstantsInFormula;
import it.unibas.lunatic.model.dependency.ConstantInFormula;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.sql.DatabaseMetaData;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.EmptyDB;
import speedy.persistence.Types;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;

public class SQLCreateTablesForConstants implements ICreateTablesForConstants {

    private static final Logger logger = LoggerFactory.getLogger(SQLCreateTablesForConstants.class);

    public void createTable(AllConstantsInFormula constantsInFormula, Scenario scenario, boolean autoritative) {
        if (scenario.getSource() instanceof EmptyDB) {
            DBMSDB newSource = createEmptySourceDatabase(scenario);
            scenario.setSource(newSource);
        }
        DBMSDB dbmsSourceDB = (DBMSDB) scenario.getSource();
        String tableNameForPremise = constantsInFormula.getTableNameForPremiseConstants();
        if (dbmsSourceDB.getTableNames().contains(tableNameForPremise)) {
            return;
        }
        executeCreateStatement(constantsInFormula, dbmsSourceDB, true);
        if (autoritative) scenario.getAuthoritativeSources().add(tableNameForPremise);
        String tableNameForConclusion = constantsInFormula.getTableNameForConclusionConstants();
        if (dbmsSourceDB.getTableNames().contains(tableNameForConclusion)) {
            return;
        }
        executeCreateStatement(constantsInFormula, dbmsSourceDB, false);
        if (autoritative) scenario.getAuthoritativeSources().add(tableNameForConclusion);
    }

    private DBMSDB createEmptySourceDatabase(Scenario scenario) {
        AccessConfiguration sourceAccessConfiguration = ((DBMSDB) scenario.getTarget()).getAccessConfiguration().clone();
        sourceAccessConfiguration.setSchemaName("source");
        StringBuilder createSchemaStatement = new StringBuilder();
        createSchemaStatement.append("DROP SCHEMA IF EXISTS ").append(sourceAccessConfiguration.getSchemaName()).append(" CASCADE;\n\n");
        createSchemaStatement.append("CREATE SCHEMA ").append(sourceAccessConfiguration.getSchemaName()).append(";\n\n");
        QueryManager.executeScript(createSchemaStatement.toString(), sourceAccessConfiguration, true, true, false, false);
        return new DBMSDB(sourceAccessConfiguration);
    }

    private void executeCreateStatement(AllConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB, boolean premise) {
        String createStatement = generateCreateStatement(constantsInFormula, dbmsSourceDB, premise);
        String insertStatement = generateInsertStatement(constantsInFormula, dbmsSourceDB, premise);
        String statement = createStatement + insertStatement;
        QueryManager.executeScript(statement, dbmsSourceDB.getAccessConfiguration(), true, true, true, false);
        DBMSTable newConstantTable;
        if (premise) {
            newConstantTable = new DBMSTable(constantsInFormula.getTableNameForPremiseConstants(), dbmsSourceDB.getAccessConfiguration());
        } else {
            newConstantTable = new DBMSTable(constantsInFormula.getTableNameForConclusionConstants(), dbmsSourceDB.getAccessConfiguration());
        }
        dbmsSourceDB.addTable(newConstantTable);
    }

    private String generateCreateStatement(AllConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB, boolean premise) {
        AccessConfiguration accessConfiguration = dbmsSourceDB.getAccessConfiguration();
        StringBuilder script = new StringBuilder();
        script.append("----- Generating constant table -----\n");
        String tableName;
        if (premise) {
            tableName = constantsInFormula.getTableNameForPremiseConstants();
        } else {
            tableName = constantsInFormula.getTableNameForConclusionConstants();
        }
        script.append("CREATE TABLE ").append(accessConfiguration.getSchemaName()).append(".").append(tableName).append("(").append("\n");
        for (ConstantInFormula constant : constantsInFormula.getConstants(premise)) {
            String attributeName = DependencyUtility.buildAttributeNameForConstant(constant.getConstantValue()) ;  
            String type = constant.getType();
//            String type = LunaticUtility.findType(constantValue);
            String dbmsType = DBMSUtility.convertDataSourceTypeToDBType(type);
            script.append(SpeedyConstants.INDENT).append(attributeName).append(" ").append(dbmsType).append(",").append("\n");
        }
        LunaticUtility.removeChars(", ".length(), script);
        script.append(") WITH OIDS;").append("\n\n");
        if (logger.isDebugEnabled()) logger.debug("----Generating constant table: " + script);
        return script.toString();
    }

    private String generateInsertStatement(AllConstantsInFormula constantsInFormula, DBMSDB dbmsSourceDB, boolean premise) {
        AccessConfiguration accessConfiguration = dbmsSourceDB.getAccessConfiguration();
        StringBuilder script = new StringBuilder();
        script.append("----- Adding tuple in constant table -----\n");
        String tableName;
        if (premise) {
            tableName = constantsInFormula.getTableNameForPremiseConstants();
        } else {
            tableName = constantsInFormula.getTableNameForConclusionConstants();
        }
        script.append("INSERT INTO ").append(accessConfiguration.getSchemaName()).append(".").append(tableName).append(" VALUES(").append("\n");
        for (ConstantInFormula constant : constantsInFormula.getConstants(premise)) {
            String attributeName = DependencyUtility.buildAttributeNameForConstant(constant.getConstantValue()) ;  
            String type = constant.getType();
//            String type = LunaticUtility.findType(constantValue);
            String valueString = constant.getConstantValue().toString();
            if (type.equals(Types.STRING)) {
                valueString = "'" + valueString + "'";
            }
            script.append(SpeedyConstants.INDENT).append(valueString).append(",").append("\n");
        }
        LunaticUtility.removeChars(", ".length(), script);
        script.append(");\n");
        if (logger.isDebugEnabled()) logger.debug("----Generating constant table: " + script);
        return script.toString();
    }

}
