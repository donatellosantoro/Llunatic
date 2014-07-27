package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.dbms.DBMSTable;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTargetInsert {

    private FormulaAttributeToSQL attributeGenerator = new FormulaAttributeToSQL();

    public String generateScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Generating target insert -----\n");
        Map<String, List<String>> insertMap = new HashMap<String, List<String>>();
        for (Dependency stTgd : scenario.getSTTgds()) {
            generateScript(stTgd, insertMap);
        }
        result.append(generateScriptFromMap(scenario, insertMap));
        return result.toString();
    }

    private void generateScript(Dependency stTgd, Map<String, List<String>> insertMap) {
        IFormula conclusion = stTgd.getConclusion();
        for (IFormulaAtom atom : conclusion.getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            TableAlias tableAlias = relationalAtom.getTableAlias();
            List<String> selects = getSelectsForTable(insertMap, tableAlias.getTableName());
            String sourceSQLQuery = generateSelectForInsert(relationalAtom, stTgd);
            selects.add(sourceSQLQuery);
        }
    }

    private List<String> getSelectsForTable(Map<String, List<String>> insertMap, String tableName) {
        if (insertMap.containsKey(tableName)) {
            return insertMap.get(tableName);
        }
        List<String> selects = new ArrayList<String>();
        insertMap.put(tableName, selects);
        return selects;
    }

    private String generateSelectForInsert(RelationalAtom relationalAtom, Dependency stTgd) {
        StringBuilder result = new StringBuilder();
        result.append(LunaticConstants.INDENT).append("SELECT DISTINCT ");
        Map<FormulaVariable, SkolemFunctionGenerator> skolems = new HashMap<FormulaVariable, SkolemFunctionGenerator>();
        for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
            result.append(attributeGenerator.generateSQL(formulaAttribute, stTgd, skolems));
            result.append(", ");
        }
        LunaticUtility.removeChars(", ".length(), result);
        result.append(" FROM ").append(LunaticConstants.WORK_SCHEMA).append(".").append(stTgd.getId());
        return result.toString();
    }

    private String generateScriptFromMap(Scenario scenario, Map<String, List<String>> insertMap) {
        StringBuilder result = new StringBuilder();
        DBMSDB target = (DBMSDB) scenario.getTarget();
        String targetSchemaName = target.getAccessConfiguration().getSchemaName();
        for (String tableToInsert : insertMap.keySet()) {
            result.append("INSERT INTO ").append(targetSchemaName).append(".").append(tableToInsert);
            result.append(getTableAttributes(target, tableToInsert));
            result.append("\n");
            List<String> selects = insertMap.get(tableToInsert);
            result.append(selects.get(0));
            for (int i = 1; i < selects.size(); i++) {
                result.append("\n UNION \n");
                result.append(selects.get(i));
            }
            result.append(";\n\n");
        }
        return result.toString();
    }

    private String getTableAttributes(DBMSDB target, String tableToInsert) {
        DBMSTable table = (DBMSTable) target.getTable(tableToInsert);
        StringBuilder attributes = new StringBuilder("(");
        for (Attribute attribute : table.getAttributes()) {
            if(attribute.getName().equals(LunaticConstants.OID)){
                continue;
            }
            attributes.append(attribute.getName()).append(", ");
        }
        LunaticUtility.removeChars(", ".length(), attributes);
        attributes.append(")");
        return attributes.toString();
    }
}
