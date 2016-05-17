package it.unibas.lunatic.persistence;

import it.unibas.lunatic.exceptions.DAOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.Attribute;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class DAODBSchemaCF {

    private final static Logger logger = LoggerFactory.getLogger(DAODBSchemaCF.class);

    public String getInitDB(Element schemaFileElement, String fileScenario, boolean useDictionaryEncoding) {
        String schemaName = schemaFileElement.getAttributeValue("schema");
        if (schemaName == null) {
            throw new DAOException("Unable to load schemaFile. Missing attribute 'schema' in element <schemaFile>");
        }
        boolean isSource = schemaName.equalsIgnoreCase("source");
        File schemaFile = DAOUtility.loadFile(schemaFileElement.getText(), fileScenario);
        Map<String, List<Attribute>> schemaMap = loadSchema(schemaFile);
        if (schemaMap.isEmpty()) {
            throw new DAOException("SchemaFile " + schemaFileElement.getText() + " does not contain any table");
        }
        StringBuilder initDBScript = new StringBuilder();
        initDBScript.append("create schema ").append(schemaName).append(";\n");
        initDBScript.append("SET search_path = ").append(schemaName).append(", pg_catalog;\n\n");
        for (String tableName : schemaMap.keySet()) {
            List<Attribute> attributes = schemaMap.get(tableName);
            appendTable(tableName, attributes, initDBScript, isSource, useDictionaryEncoding);
        }
        if (logger.isDebugEnabled()) logger.debug("InitDB Script:\n" + initDBScript.toString());
        return initDBScript.toString();
    }

    private Map<String, List<Attribute>> loadSchema(File schemaFile) {
        Map<String, List<Attribute>> schemaMap = new HashMap<String, List<Attribute>>();
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(schemaFile);
            reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                readTable(line, reader, schemaMap);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            throw new DAOException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
            }
        }
        return schemaMap;
    }

    private void readTable(String tableNameLine, BufferedReader reader, Map<String, List<Attribute>> schemaMap) throws IOException {
        if (!tableNameLine.contains("{")) {
            throw new DAOException("Wrong schema format for table name in line " + tableNameLine);
        }
        String tableName = (tableNameLine.substring(0, tableNameLine.indexOf("{") - 1)).trim();
        tableName = DBMSUtility.cleanTableName(tableName);
        if (logger.isDebugEnabled()) logger.debug("Reading table '" + tableName + "'");
        List<Attribute> attributesForTable = new ArrayList<Attribute>();
        String line;
        while (!"}".equals(line = reader.readLine())) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] attributeType = line.split(":");
            if (attributeType.length != 2) {
                throw new DAOException("Wrong schema format for attribute in line " + tableNameLine);
            }
            String attribute = attributeType[0].trim();
            String type = cleanType(attributeType[1].trim());
            if (logger.isDebugEnabled()) logger.debug("Attribute: '" + attribute + "' - Type: " + type);
            attributesForTable.add(new Attribute(tableName, attribute, type));
        }
        schemaMap.put(tableName, attributesForTable);
    }

    private String cleanType(String type) {
        if (type.endsWith(",")) {
            return type.substring(0, type.length() - 1);
        }
        return type;
    }

    private void appendTable(String tableName, List<Attribute> attributes, StringBuilder initDBScript, boolean isSource, boolean useDictionaryEncoding) {
        initDBScript.append("CREATE TABLE ").append(tableName).append(" (");
        initDBScript.append("\n\t  oid serial,");
        for (Attribute attribute : attributes) {
            initDBScript.append("\n\t").append(attribute.getName()).append(" ").append(convertType(attribute.getType(), isSource, useDictionaryEncoding)).append(",");
        }
        SpeedyUtility.removeChars(",".length(), initDBScript);
        initDBScript.append("\n);\n");
    }

    private Object convertType(String type, boolean isSource, boolean useDictionaryEncoding) {
        if (useDictionaryEncoding) {
            return "bigint";
        }
        if (type.equalsIgnoreCase("STRING")) {
            return "text";
        }
        if (type.equalsIgnoreCase("INTEGER")) {
            if (isSource) {
                return "integer";
            }
            return "bigint";
        }
        if (type.equalsIgnoreCase("DOUBLE")) {
            if (isSource) {
                return "float";
            }
            return "double precision";
        }
        if (type.equalsIgnoreCase("SYMBOL")) {
            return "text";
        }
        throw new DAOException("Unknown type " + type);
    }

}
