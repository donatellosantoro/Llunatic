package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.parser.operators.ParseDependencies;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.TableAlias;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.utility.SpeedyUtility;
import org.apache.commons.io.IOUtils;

public class GenerateSQLFromDependencies {

    private final static Logger logger = LoggerFactory.getLogger(GenerateSQLFromDependencies.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();
    private ParseDependencies parser = new ParseDependencies();

    public void generateSQL(String dependenciesFile,
            String sourceInitDBOutFile, String targetInitDBOutFile) throws Exception {
        String dependenciesString = readDependenciesString(dependenciesFile);
        Scenario scenario = generateScenario(dependenciesString);
        Map<TableAlias, List<AttributeRef>> tableMap = generateTableMap(scenario);
        if (logger.isDebugEnabled()) logger.debug(SpeedyUtility.printMap(tableMap));
        StringBuilder sourceScript = new StringBuilder(openXMLTag());
        StringBuilder targetScript = new StringBuilder(openXMLTag());
        addCreateTable(tableMap, sourceScript, true);
        addCreateTable(tableMap, targetScript, false);
        addInsertIntoForSTTGDs(scenario, sourceScript);
        sourceScript.append(closeXMLTag());
        targetScript.append(closeXMLTag());
        IOUtils.write(sourceScript.toString(), new FileOutputStream(sourceInitDBOutFile));
        IOUtils.write(targetScript.toString(), new FileOutputStream(targetInitDBOutFile));
    }

    private Scenario generateScenario(String dependenciesString) throws Exception {
        Scenario scenario = new ScenarioMock();
        parser.generateDependencies(dependenciesString, scenario);
        return scenario;
    }

    private Map<TableAlias, List<AttributeRef>> generateTableMap(Scenario scenario) {
        Map<TableAlias, List<AttributeRef>> tableMap = new HashMap<TableAlias, List<AttributeRef>>();
        for (Dependency stTgd : scenario.getSTTgds()) {
            List<RelationalAtom> relationalAtoms = getRelationalAtoms(stTgd, true);
            addRelationalAtoms(relationalAtoms, tableMap);
        }
        for (Dependency extTgd : scenario.getExtTGDs()) {
            List<RelationalAtom> relationalAtoms = getRelationalAtoms(extTgd, false);
            addRelationalAtoms(relationalAtoms, tableMap);
        }
        for (Dependency egds : scenario.getEGDs()) {
            List<RelationalAtom> relationalAtoms = getRelationalAtoms(egds, false);
            addRelationalAtoms(relationalAtoms, tableMap);
        }
        for (Dependency extEgds : scenario.getExtEGDs()) {
            List<RelationalAtom> relationalAtoms = getRelationalAtoms(extEgds, false);
            addRelationalAtoms(relationalAtoms, tableMap);
        }
        for (Dependency dcs : scenario.getDCs()) {
            List<RelationalAtom> relationalAtoms = getRelationalAtoms(dcs, false);
            addRelationalAtoms(relationalAtoms, tableMap);
        }
        return tableMap;
    }

    private List<RelationalAtom> getRelationalAtoms(Dependency dependency, boolean hasPremiseInSource) {
        List<RelationalAtom> result = new ArrayList<RelationalAtom>();
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtom.getTableAlias().setSource(hasPremiseInSource);
                result.add(relationalAtom);
            }
        }
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtom.getTableAlias().setSource(false);
                result.add(relationalAtom);
            }
        }
        return result;
    }

    private void addRelationalAtoms(List<RelationalAtom> relationalAtoms, Map<TableAlias, List<AttributeRef>> tableMap) {
        for (RelationalAtom relationalAtom : relationalAtoms) {
            TableAlias tableAlias = relationalAtom.getTableAlias();
            tableAlias = ChaseUtility.unAlias(tableAlias);
            List<AttributeRef> attributesForTable = tableMap.get(tableAlias);
            if (attributesForTable == null) {
                attributesForTable = new ArrayList<AttributeRef>();
                tableMap.put(tableAlias, attributesForTable);
            }
            for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                AttributeRef attributeRef = new AttributeRef(tableAlias, attribute.getAttributeName());
                SpeedyUtility.addIfNotContained(attributesForTable, attributeRef);
            }
        }
    }

    private void addCreateTable(Map<TableAlias, List<AttributeRef>> tableMap, StringBuilder result, boolean useSource) {
        String schemaName = (useSource ? "source" : "target");
        result.append("create schema ").append(schemaName).append(";\n");
        result.append("SET search_path = ").append(schemaName).append(", pg_catalog;\n");
        for (TableAlias tableAlias : tableMap.keySet()) {
            if (tableAlias.isSource() != useSource) {
                continue;
            }
            result.append("create table ").append(tableAlias.getTableName().toLowerCase()).append("(").append("\n");
            result.append("\t").append("oid serial,").append("\n");
            for (AttributeRef attributeRef : tableMap.get(tableAlias)) {
                result.append("\t").append(attributeRef.getName().toLowerCase());
//                if (useNumericalValues) {
//                    result.append(" integer");
//                } else {
                result.append(" text");
//                }
                result.append(",\n");
            }
            SpeedyUtility.removeChars(",\n".length(), result);
            result.append("\n);").append("\n");
        }
    }

    private void addInsertIntoForSTTGDs(Scenario scenario, StringBuilder result) {
        for (Dependency stTgd : scenario.getSTTgds()) {
            for (IFormulaAtom atom : stTgd.getPremise().getAtoms()) {
                if (!(atom instanceof RelationalAtom)) {
                    continue;
                }
                RelationalAtom relAtom = (RelationalAtom) atom;
                result.append("INSERT INTO source.").append(relAtom.getTableName()).append("(");
                for (FormulaAttribute formulaAttribute : relAtom.getAttributes()) {
                    result.append(formulaAttribute.getAttributeName()).append(", ");
                }
                SpeedyUtility.removeChars(", ".length(), result);
                result.append(") VALUES (");
                for (FormulaAttribute formulaAttribute : relAtom.getAttributes()) {
//                    if (useNumericalValues) {
//                        result.append(extractNumericalValueFromFormulaVariable(formulaAttribute));
//                    } else {
                    result.append("'");
                    result.append(formulaAttribute.getValue().toString());
                    result.append("'");
//                    }
                    result.append(", ");
                }
                SpeedyUtility.removeChars(", ".length(), result);
                result.append(");\n");
            }
        }
    }

//    private int extractNumericalValueFromFormulaVariable(FormulaAttribute formulaAttribute) {
//        String formulaVariable = formulaAttribute.getValue().toString();
//        String numericalChars = formulaVariable.replaceAll("[^\\d]", "");
//        return Integer.parseInt(numericalChars);
//    }

    private String readDependenciesString(String dependenciesFile) {
        Document document = daoUtility.buildDOM(dependenciesFile);
        Element rootElement = document.getRootElement();
        return rootElement.getValue().trim();
    }

    private String openXMLTag() {
        return "<init-db>\n<![CDATA[\n";
    }

    private String closeXMLTag() {
        return "\n]]>\n</init-db>";
    }

    private static class ScenarioMock extends Scenario {

        public ScenarioMock() {
            super("", null);
        }

        @Override
        public IDatabase getSource() {
            return new DatabaseMock();
        }

        @Override
        public IDatabase getTarget() {
            return new DatabaseMock();
        }
    }

    private static class DatabaseMock extends MainMemoryDB {

        public DatabaseMock() {
            super(null);
        }

        @Override
        public String printSchema() {
            return "- MOCK SCHEMA - ";
        }

        @Override
        public List<String> getTableNames() {
            return new ArrayList<String>() {

                @Override
                public boolean contains(Object o) {
                    return true;
                }
            };
        }

    }
}
