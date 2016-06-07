package it.unibas.lunatic.run;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.persistence.DAODatabaseConfiguration;
import it.unibas.lunatic.persistence.DAOLunaticConfiguration;
import it.unibas.lunatic.persistence.DAOUtility;
import it.unibas.lunatic.persistence.encoding.DictionaryEncoder;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import org.jdom.Document;
import org.jdom.Element;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.persistence.xml.DAOXmlUtility;

public class MainExpExport {

    private final static DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final static DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final static DAODatabaseConfiguration daoDatabaseConfiguration = new DAODatabaseConfiguration();
    private final static ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();

    public static void main(String[] args) {
        String fileScenario = args[0];
        Document document = daoUtility.buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element configurationElement = rootElement.getChild("configuration");
        LunaticConfiguration configuration = daoConfiguration.loadConfiguration(configurationElement);
        if (!configuration.isExportSolutions()) {
            return;
        }
        if (!configuration.isPrintStatsOnly()) {
            return;
        }
        Element targetElement = rootElement.getChild("target");
        IValueEncoder valueEncoder = getValueEncoder(configuration, fileScenario);
        IDatabase targetDB = daoDatabaseConfiguration.loadDatabase(targetElement, "", fileScenario, valueEncoder);
        resultExporter.exportSolutionInSeparateFiles(targetDB, valueEncoder, configuration.getExportSolutionsPath(), configuration.getMaxNumberOfThreads());
    }

    private static IValueEncoder getValueEncoder(LunaticConfiguration configuration, String fileScenario) {
        if (!configuration.isUseDictionaryEncoding()) {
            return null;
        }
        return new DictionaryEncoder(DAOUtility.extractScenarioName(fileScenario));
    }
}
