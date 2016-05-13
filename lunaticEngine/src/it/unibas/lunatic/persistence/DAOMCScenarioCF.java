package it.unibas.lunatic.persistence;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.operators.ProcessDependencies;
import it.unibas.lunatic.parser.ParserOutput;
import it.unibas.lunatic.parser.operators.ParseDependenciesCF;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.persistence.xml.operators.TransformFilePaths;

public class DAOMCScenarioCF {

    private final static Logger logger = LoggerFactory.getLogger(DAOMCScenarioCF.class);
    private final DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final DAODatabaseConfiguration daoDatabaseConfiguration = new DAODatabaseConfiguration();
    private final TransformFilePaths filePathTransformator = new TransformFilePaths();
    private final ProcessDependencies dependencyProcessor = new ProcessDependencies();

    public Scenario loadScenario(String fileScenario, String suffix) throws DAOException {
        long start = new Date().getTime();
        try {
            Scenario scenario = new Scenario(fileScenario, suffix);
            long startLoadXML = new Date().getTime();
            Document document = daoUtility.buildDOM(fileScenario);
            long endLoadXML = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_XML_SCENARIO_TIME, endLoadXML - startLoadXML);
            Element rootElement = document.getRootElement();
            //SOURCE
            Element sourceElement = rootElement.getChild("source");
            IDatabase sourceDatabase = daoDatabaseConfiguration.loadDatabase(sourceElement, null, fileScenario); //Source schema doesn't need suffix
            scenario.setSource(sourceDatabase);
            //TARGET
            Element targetElement = rootElement.getChild("target");
            IDatabase targetDatabase = daoDatabaseConfiguration.loadDatabase(targetElement, suffix, fileScenario);
            scenario.setTarget(targetDatabase);
            //CONFIGURATION
            Element configurationElement = rootElement.getChild("configuration");
            LunaticConfiguration configuration = daoConfiguration.loadConfiguration(configurationElement);
            scenario.setConfiguration(configuration);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_TIME, end - start);
            //InitDB (out of LOAD_TIME stat)
            daoDatabaseConfiguration.initDatabase(scenario);
            start = new Date().getTime();
            //AUTHORITATIVE SOURCES
            Element authoritativeSourcesElement = rootElement.getChild("authoritativeSources");
            List<String> authoritativeSources = daoDatabaseConfiguration.loadAuthoritativeSources(authoritativeSourcesElement, scenario);
            scenario.setAuthoritativeSources(authoritativeSources);
            //DEPENDENCIES
            Element dependenciesElement = rootElement.getChild("dependencies");
            Element queriesElement = rootElement.getChild("queries");
            loadDependenciesAndQueries(dependenciesElement, queriesElement, scenario);
            //CONFIGURATION
            daoConfiguration.loadOtherScenarioElements(rootElement, scenario);
            //QUERIES
            end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.LOAD_TIME, end - start);
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

    private void loadDependenciesAndQueries(Element dependenciesElement, Element queriesElement, Scenario scenario) throws DAOException {
        StringBuilder dependenciesAndQueries = new StringBuilder();
        if (dependenciesElement != null) {
            Element stTGDFileElement = dependenciesElement.getChild("sttgdsFile");
            if (stTGDFileElement != null) {
                String content = loadFileContent(stTGDFileElement.getText(), scenario.getFileName());
                dependenciesAndQueries.append("ST-TGDs:\n").append(content).append("\n");
            }
            Element tTGDFileElement = dependenciesElement.getChild("ttgdsFile");
            if (tTGDFileElement != null) {
                String content = loadFileContent(tTGDFileElement.getText(), scenario.getFileName());
                dependenciesAndQueries.append("T-TGDs:\n").append(content).append("\n");
            }
            Element egdsFileElement = dependenciesElement.getChild("egdsFile");
            if (egdsFileElement != null) {
                String content = loadFileContent(egdsFileElement.getText(), scenario.getFileName());
                dependenciesAndQueries.append("EGDs:\n").append(content).append("\n");
            }
        }
        if (queriesElement != null) {
            Element queryFileElement = queriesElement.getChild("queryFile");
            if (queryFileElement != null) {
                String content = loadFileContent(queryFileElement.getText(), scenario.getFileName());
                dependenciesAndQueries.append("Queries:\n").append(content).append("\n");
            }
        }
        if (logger.isDebugEnabled()) logger.debug(dependenciesAndQueries.toString());
        ParseDependenciesCF generator = new ParseDependenciesCF();
        try {
            long start = new Date().getTime();
            generator.generateDependencies(dependenciesAndQueries.toString(), scenario);
            ParserOutput parserOutput = generator.getParserOutput();
            dependencyProcessor.processDependencies(parserOutput, scenario);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.PARSING_TIME, end - start);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException(ex);
        }
    }

    private String loadFileContent(String relativeFileName, String fileScenario) throws DAOException {
        try {
            String absoluteFileName = filePathTransformator.expand(fileScenario, relativeFileName);
            return FileUtils.readFileToString(new File(absoluteFileName));
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

}
