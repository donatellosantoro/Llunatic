package it.unibas.lunatic.persistence;

import it.unibas.lunatic.persistence.encoding.DictionaryEncoder;
import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForCertainAnswerQuery;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.ProcessDependencies;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.operators.ParseDependenciesCF;
import it.unibas.lunatic.persistence.encoding.DummyEncoder;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.SQLQueryString;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOMCScenarioCF {

    private final static Logger logger = LoggerFactory.getLogger(DAOMCScenarioCF.class);
    private final DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final DAODatabaseConfiguration daoDatabaseConfiguration = new DAODatabaseConfiguration();
    private final ProcessDependencies dependencyProcessor = new ProcessDependencies();

    public Scenario loadScenario(String fileScenario, DAOConfiguration config) throws DAOException {
        long start = new Date().getTime();
        try {
            Scenario scenario = new Scenario(fileScenario, config.getSuffix());
            long startLoadXML = new Date().getTime();
            Document document = daoUtility.buildDOM(fileScenario);
            long endLoadXML = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_XML_SCENARIO_TIME, endLoadXML - startLoadXML);
            Element rootElement = document.getRootElement();
            //CONFIGURATION
            Element configurationElement = rootElement.getChild("configuration");
            LunaticConfiguration configuration = daoConfiguration.loadConfiguration(configurationElement);
            scenario.setConfiguration(configuration);
            if (configuration.isUseDictionaryEncoding()) {
                if (config.isImportData()) {
                    scenario.setValueEncoder(new DictionaryEncoder(DAOUtility.extractScenarioName(fileScenario)));
                    if (config.isRemoveExistingDictionary()) {
                        scenario.getValueEncoder().removeExistingEncoding();
                    }
                    scenario.getValueEncoder().prepareForEncoding();
                } else {
                    scenario.setValueEncoder(new DummyEncoder());
                }
            }
            //SOURCE
            Element sourceElement = rootElement.getChild("source");
            IDatabase sourceDatabase = daoDatabaseConfiguration.loadDatabase(sourceElement, null, fileScenario, scenario.getValueEncoder()); //Source schema doesn't need suffix
            scenario.setSource(sourceDatabase);
            //TARGET
            Element targetElement = rootElement.getChild("target");
            IDatabase targetDatabase = daoDatabaseConfiguration.loadDatabase(targetElement, config.getSuffix(), fileScenario, scenario.getValueEncoder());
            scenario.setTarget(targetDatabase);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_TIME, end - start);
            //InitDB (out of LOAD_TIME stat)
            if (config.isImportData()) {
                daoDatabaseConfiguration.initDatabase(scenario);
            }
            start = new Date().getTime();
            //AUTHORITATIVE SOURCES
            Element authoritativeSourcesElement = rootElement.getChild("authoritativeSources");
            List<String> authoritativeSources = daoDatabaseConfiguration.loadAuthoritativeSources(authoritativeSourcesElement, scenario);
            scenario.setAuthoritativeSources(authoritativeSources);
            //DEPENDENCIES
            Element dependenciesElement = rootElement.getChild("dependencies");
            Element queriesElement = rootElement.getChild("queries");
            loadDependenciesAndQueries(dependenciesElement, queriesElement, config, scenario);
            //CONFIGURATION
            daoConfiguration.loadOtherScenarioElements(rootElement, scenario);
            //QUERIES
            end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_TIME, end - start);
            if (configuration.isUseDictionaryEncoding()) {
                if (config.isImportData()) {
                    scenario.getValueEncoder().closeEncoding();
                }
            }
            return scenario;
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load scenario from file " + fileScenario;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        } finally {
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDependenciesAndQueries(Element dependenciesElement, Element queriesElement, DAOConfiguration config, Scenario scenario) throws DAOException, IOException {
        StringBuilder dependenciesAndQueries = new StringBuilder();
        if (config.isUseRewrittenDependencies()) {
            String filePath = getEncodedDependenciesPath(scenario.getFileName());
            dependenciesAndQueries.append(FileUtils.readFileToString(new File(filePath)));
        } else {
            if (dependenciesElement != null) {
                Element stTGDFileElement = dependenciesElement.getChild("sttgdsFile");
                if (stTGDFileElement != null) {
                    String content = DAOUtility.loadFileContent(stTGDFileElement.getText(), scenario.getFileName());
                    dependenciesAndQueries.append("ST-TGDs:\n").append(content).append("\n");
                }
                Element tTGDFileElement = dependenciesElement.getChild("ttgdsFile");
                if (tTGDFileElement != null) {
                    String content = DAOUtility.loadFileContent(tTGDFileElement.getText(), scenario.getFileName());
                    dependenciesAndQueries.append("T-TGDs:\n").append(content).append("\n");
                }
                Element egdsFileElement = dependenciesElement.getChild("egdsFile");
                if (egdsFileElement != null) {
                    String content = DAOUtility.loadFileContent(egdsFileElement.getText(), scenario.getFileName());
                    dependenciesAndQueries.append("EGDs:\n").append(content).append("\n");
                }
            }
            if (queriesElement != null) {
                dependenciesAndQueries.append("Queries:\n");
                List<Element> queryListElement = queriesElement.getChildren("queryFile");
                for (Element queryFileElement : queryListElement) {
                    String content = DAOUtility.loadFileContent(queryFileElement.getText(), scenario.getFileName());
                    String queryId = FilenameUtils.getBaseName(queryFileElement.getText());
                    dependenciesAndQueries.append(queryId).append(": ");
                    dependenciesAndQueries.append(content).append("\n");
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug(dependenciesAndQueries.toString());
        ParseDependenciesCF generator = new ParseDependenciesCF();
        try {
            long start = new Date().getTime();
            generator.generateDependencies(dependenciesAndQueries.toString(), scenario);
            long end = new Date().getTime();
            ParserOutput parserOutput = generator.getParserOutput();
            ChaseStats.getInstance().addStat(ChaseStats.PARSING_TIME, end - start);
            if (config.isProcessDependencies()) {
                dependencyProcessor.processDependencies(parserOutput, scenario);
            }
            if (config.isExportRewrittenDependencies()) {
                exportRewrittenDependencies(parserOutput, scenario.getFileName());
                exportRewrittenSQLQueries(parserOutput, scenario);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException(ex);
        }
    }

    private void exportRewrittenDependencies(ParserOutput parserOutput, String scenarioName) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (!parserOutput.getStTGDs().isEmpty()) {
            sb.append("\nST-TGDs:");
            for (Dependency d : parserOutput.getStTGDs()) {
                sb.append(d.toCFString());
            }
        }
        if (!parserOutput.geteTGDs().isEmpty()) {
            sb.append("\nT-TGDs:");
            for (Dependency d : parserOutput.geteTGDs()) {
                sb.append(d.toCFString());
            }
        }
        if (!parserOutput.getEgds().isEmpty()) {
            sb.append("\nEGDs:");
            for (Dependency d : parserOutput.getEgds()) {
                sb.append(d.toCFString());
            }
        }
//        if (!parserOutput.getQueries().isEmpty()) {
//            sb.append("\nQueries:");
//            for (Dependency d : parserOutput.getQueries()) {
//                sb.append(d.toCFString());
//            }
//        }
        String filePath = getEncodedDependenciesPath(scenarioName);
        if (logger.isDebugEnabled()) logger.debug("Encoded dependencies file: " + filePath);
        FileUtils.writeStringToFile(new File(filePath), sb.toString());
    }

    private void exportRewrittenSQLQueries(ParserOutput parserOutput, Scenario scenario) {
        if (parserOutput.getQueries().isEmpty()) {
            return;
        }
        ParserOutput parserOutputQueries = new ParserOutput();
        parserOutputQueries.getQueries().addAll(parserOutput.getQueries());
        dependencyProcessor.processDependencies(parserOutputQueries, scenario);
        BuildAlgebraTreeForCertainAnswerQuery treeBuilder = new BuildAlgebraTreeForCertainAnswerQuery();
        AlgebraTreeToSQL algebraTreeToSQL = new AlgebraTreeToSQL();
        List<SQLQueryString> queries = new ArrayList<SQLQueryString>();
        for (Dependency d : parserOutputQueries.getQueries()) {
            IAlgebraOperator operator = treeBuilder.generateOperator(d, scenario);
            String sql = algebraTreeToSQL.treeToSQL(operator, scenario.getSource(), scenario.getTarget(), "");
            SQLQueryString sqlQuery = new SQLQueryString(d.getId(), sql);
            queries.add(sqlQuery);
        }
        String queryPath = LunaticUtility.getSQLQueriesPath(scenario.getFileName());
        ObjectOutputStream out = null;
        try {
            File queryFile = new File(queryPath);
            queryFile.getParentFile().mkdirs();
            out = new ObjectOutputStream(new FileOutputStream(queryFile));
            out.writeObject(queries);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException("Unable to save sql queries to file " + queryPath + ".\n" + ex.getLocalizedMessage());
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
            }
        }
    }

    private String getEncodedDependenciesPath(String scenarioPath) {
        File fileScenario = new File(scenarioPath);
        String fileName = fileScenario.getName();
        String homeDir = System.getProperty("user.home");
        return homeDir + File.separator + SpeedyConstants.WORK_DIR + File.separator + "Encoding" + File.separator + "ENC_" + fileName + ".txt";
    }

}
