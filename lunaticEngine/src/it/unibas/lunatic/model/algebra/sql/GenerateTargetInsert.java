package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.TableAlias;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSTable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class GenerateTargetInsert {

    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private BuildAlgebraTreeForStandardChase standardTreeBuilder = new BuildAlgebraTreeForStandardChase();

    public GenerateTargetInsert() {
    }

    public String generateScript(Collection<Dependency> sttgds, Set<Dependency> dependenciesToMaterialize, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Generating target insert -----\n");
        Map<String, List<String>> insertMap = new HashMap<String, List<String>>();
        for (Dependency stTgd : sttgds) {
            generateScriptDependency(stTgd, insertMap, dependenciesToMaterialize, scenario);
        }
        result.append(generateScriptFromMap(scenario, insertMap));
        return result.toString();
    }

    private void generateScriptDependency(Dependency stTgd, Map<String, List<String>> insertMap, Set<Dependency> dependenciesToMaterialize, Scenario scenario) {
        IFormula conclusion = stTgd.getConclusion();
        for (IFormulaAtom atom : conclusion.getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            TableAlias tableAlias = relationalAtom.getTableAlias();
            List<String> selects = getSelectsForTable(insertMap, tableAlias.getTableName());
            String sourceSQLQuery = generateSelectForInsert(relationalAtom, stTgd, dependenciesToMaterialize, scenario);
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

    private String generateSelectForInsert(RelationalAtom relationalAtom, Dependency stTgd, Set<Dependency> dependenciesToMaterialize, Scenario scenario) {
        FormulaAttributeToSQL attributeGenerator = new FormulaAttributeToSQL(); //Operator with state
        StringBuilder result = new StringBuilder();
        result.append(SpeedyConstants.INDENT).append("SELECT DISTINCT ");
        Map<FormulaVariable, IValueGenerator> skolems = new HashMap<FormulaVariable, IValueGenerator>();
        for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
            result.append(attributeGenerator.generateSQL(formulaAttribute, stTgd, skolems, scenario));
            result.append(", ");
        }
        LunaticUtility.removeChars(", ".length(), result);
        result.append(" FROM ");
        if (dependenciesToMaterialize.contains(stTgd)) {
            result.append(LunaticDBMSUtility.getWorkSchema(scenario)).append(".").append(stTgd.getId());
        } else {
            result.append(" (\n");
            IAlgebraOperator operator = getPremiseOperator(stTgd, scenario);
            result.append(queryBuilder.treeToSQL(operator, scenario.getSource(), scenario.getTarget(), SpeedyConstants.INDENT));
            result.append("\n) as tmp_").append(stTgd.getId());
        }
        return result.toString();
    }

    private String generateScriptFromMap(Scenario scenario, Map<String, List<String>> insertMap) {
        StringBuilder result = new StringBuilder();
        DBMSDB target = (DBMSDB) scenario.getTarget();
        String targetSchemaName = DBMSUtility.getSchemaNameAndDot(target.getAccessConfiguration());
        for (String tableToInsert : insertMap.keySet()) {
            result.append("INSERT INTO ").append(targetSchemaName).append(tableToInsert);
            result.append(getTableAttributes(target, tableToInsert));
            result.append("\n");
            List<String> selects = insertMap.get(tableToInsert);
            result.append(selects.get(0));
            for (int i = 1; i < selects.size(); i++) {
                result.append("\n UNION \n");
                result.append(selects.get(i));
            }
            if (scenario.getConfiguration().isPreventInsertDuplicateTuples()) {
                result.append(generateOnConflictPart(target, tableToInsert));
            }
            result.append(";\n\n");
        }
        return result.toString();
    }

    private String getTableAttributes(DBMSDB target, String tableToInsert) {
        DBMSTable table = (DBMSTable) target.getTable(tableToInsert);
        StringBuilder attributes = new StringBuilder("(");
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(SpeedyConstants.OID)) {
                continue;
            }
            attributes.append(attribute.getName()).append(", ");
        }
        LunaticUtility.removeChars(", ".length(), attributes);
        attributes.append(")");
        return attributes.toString();
    }

    private IAlgebraOperator getPremiseOperator(Dependency dependency, Scenario scenario) {
        if (scenario.getConfiguration().isUseDistinctInSTTGDs()) {
            return standardTreeBuilder.generateAlgebraTreeWithDinstinct(dependency, scenario);
        }
        return treeBuilder.buildTreeForPremise(dependency, scenario);
    }

    private String generateOnConflictPart(DBMSDB target, String tableToInsert) {
        StringBuilder result = new StringBuilder();
        result.append(SpeedyConstants.INDENT).append("\nON CONFLICT ");
        result.append(getTableAttributes(target, tableToInsert));
        result.append(" DO NOTHING\n");
        return result.toString();
    }
}
