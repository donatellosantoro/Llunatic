package it.unibas.lunatic.persistence;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.persistence.file.CSVFile;
import speedy.persistence.file.IImportFile;
import speedy.persistence.file.XMLFile;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.operators.TransformFilePaths;

public class DAODatabaseConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(DAODatabaseConfiguration.class);

    private static final String DB_TYPE_MAINMEMORY = "XML";
    private static final String DB_TYPE_MAINMEMORY_GENERATE = "GENERATE";
    private static final String DB_TYPE_DBMS = "DBMS";
    private final DAOMainMemoryDatabase daoMainMemoryDatabase = new DAOMainMemoryDatabase();
    private final DAODBSchemaCF daoSchema = new DAODBSchemaCF();
    private final TransformFilePaths filePathTransformator = new TransformFilePaths();

    public IDatabase loadDatabase(Element databaseElement, String suffix, String fileScenario, IValueEncoder valueEncoder) throws DAOException {
        if (databaseElement == null || databaseElement.getChildren().isEmpty()) {
            return new EmptyDB();
        }
        Element typeElement = databaseElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        boolean useDictionaryEncoding = (valueEncoder != null);
        String databaseType = typeElement.getValue();
        if (DB_TYPE_MAINMEMORY.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("xml");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <xml>");
            }
            String schemaRelativeFile = xmlElement.getChild("xml-schema").getValue();
            String schemaAbsoluteFile = filePathTransformator.expand(fileScenario, schemaRelativeFile);
            String instanceRelativeFile = xmlElement.getChild("xml-instance").getValue();
            String instanceAbsoluteFile = null; //Optional field
            if (instanceRelativeFile != null && !instanceRelativeFile.trim().isEmpty()) {
                instanceAbsoluteFile = filePathTransformator.expand(fileScenario, instanceRelativeFile);
            }
            return daoMainMemoryDatabase.loadXMLScenario(schemaAbsoluteFile, instanceAbsoluteFile);
        } else if (DB_TYPE_MAINMEMORY_GENERATE.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("generate");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <generate>");
            }
            String plainInstance = xmlElement.getValue();
            return daoMainMemoryDatabase.loadPlainScenario(plainInstance);
        } else if (DB_TYPE_DBMS.equalsIgnoreCase(databaseType)) {
            Element dbmsElement = databaseElement.getChild("access-configuration");
            if (dbmsElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <access-configuration>");
            }
            AccessConfiguration accessConfiguration = new AccessConfiguration();
            accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
            accessConfiguration.setUri(dbmsElement.getChildText("uri").trim() + (useDictionaryEncoding ? "_enc" : ""));
//            accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
            accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
            if (suffix != null && !suffix.trim().isEmpty()) {
                accessConfiguration.setSchemaSuffix(suffix.trim());
            }
            accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
            accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
            Element initDbElement = databaseElement.getChild("init-db");
            DBMSDB database = new DBMSDB(accessConfiguration);
            if (initDbElement != null) {
                database.getInitDBConfiguration().setInitDBScript(initDbElement.getValue());
            }
            Element schemaFileElement = databaseElement.getChild("schemaFile");
            if (schemaFileElement != null) {
                database.getInitDBConfiguration().setInitDBScript(daoSchema.getInitDB(schemaFileElement, fileScenario, useDictionaryEncoding));
            }
            database.getInitDBConfiguration().setValueEncoder(valueEncoder);
            processImport(databaseElement, database, fileScenario);
            return database;
        } else {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Unknown database type " + databaseType);
        }
    }

    private void processImport(Element databaseElement, DBMSDB database, String fileScenario) throws DAOException {
        Element importXmlElement = databaseElement.getChild("import");
        if (importXmlElement != null) {
            Attribute createTableAttribute = importXmlElement.getAttribute("createTables");
            if (createTableAttribute != null) {
                database.getInitDBConfiguration().setCreateTablesFromFiles(Boolean.parseBoolean(createTableAttribute.getValue()));
            }
            for (Object inputFileObj : importXmlElement.getChildren("input")) {
                Element inputFileElement = (Element) inputFileObj;
                String fileName = inputFileElement.getText();
                String tableName = inputFileElement.getAttribute("table").getValue();
                String type = inputFileElement.getAttribute("type").getValue();
                fileName = filePathTransformator.expand(fileScenario, fileName);
                IImportFile fileToImport;
                if (type.equalsIgnoreCase(SpeedyConstants.XML)) {
                    fileToImport = new XMLFile(fileName);
                } else if (type.equalsIgnoreCase(SpeedyConstants.CSV)) {
                    CSVFile csvFile = new CSVFile(fileName);
                    if (inputFileElement.getAttribute("separator") != null) {
                        String separator = inputFileElement.getAttribute("separator").getValue();
                        csvFile.setSeparator(separator.charAt(0));
                    }
                    if (inputFileElement.getAttribute("quoteCharacter") != null) {
                        String quoteCharacter = inputFileElement.getAttribute("quoteCharacter").getValue();
                        csvFile.setQuoteCharacter(quoteCharacter.charAt(0));
                    }
                    if (inputFileElement.getAttribute("hasHeader") != null) {
                        boolean hasHeader = Boolean.parseBoolean(inputFileElement.getAttribute("hasHeader").getValue());
                        csvFile.setHasHeader(hasHeader);
                    }
                    fileToImport = csvFile;
                } else {
                    throw new DAOException("Type " + type + " is not supported");
                }
                database.getInitDBConfiguration().addFileToImportForTable(tableName, fileToImport);
            }
        }
    }

    public void initDatabase(Scenario scenario) {
        if (!scenario.isDBMS()) {
            return;
        }
        long start = new Date().getTime();
        if (scenario.getSource() != null && (scenario.getSource() instanceof DBMSDB)) {
            DBMSDB dbmsdb = (DBMSDB) scenario.getSource();
            dbmsdb.initDBMS();
        }
        DBMSDB dbmsdb = (DBMSDB) scenario.getTarget();
        dbmsdb.initDBMS();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.INIT_DB_TIME, end - start);
    }

    @SuppressWarnings("unchecked")
    public List<String> loadAuthoritativeSources(Element authoritativeSourcesElement, Scenario scenario) {
        if (authoritativeSourcesElement == null || authoritativeSourcesElement.getChildren().isEmpty()) {
            return new ArrayList<String>();
        }
        List<String> sources = new ArrayList<String>();
        List<Element> sourceElements = authoritativeSourcesElement.getChildren("source");
        for (Element sourceElement : sourceElements) {
            sources.add(sourceElement.getText());
        }
        return sources;
    }
}
