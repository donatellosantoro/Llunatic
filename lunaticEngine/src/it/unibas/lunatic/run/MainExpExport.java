package it.unibas.lunatic.run;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasede.operators.ExecuteFinalQueries;
import it.unibas.lunatic.persistence.DAODatabaseConfiguration;
import it.unibas.lunatic.persistence.DAOLunaticConfiguration;
import it.unibas.lunatic.persistence.DAOUtility;
import it.unibas.lunatic.persistence.encoding.DictionaryEncoder;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.utility.PrintUtility;

public class MainExpExport {

    private final static DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final static DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final static DAODatabaseConfiguration daoDatabaseConfiguration = new DAODatabaseConfiguration();
    private final static ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();
    private final static ExecuteFinalQueries finalQueryExecutor = new ExecuteFinalQueries();

    public static void main(String[] args) {
        long startExportTime = new Date().getTime();
        List<String> options = new ArrayList<String>(Arrays.asList(args));
        String fileScenario = options.get(0);
        Document document = daoUtility.buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element configurationElement = rootElement.getChild("configuration");
        LunaticConfiguration conf = daoConfiguration.loadConfiguration(configurationElement);
        LunaticUtility.applyCommandLineOptions(conf, options);
        if (!conf.isExportSolutions()) {
            return;
        }
        if (conf.isPrintStatsOnly()) {
            return;
        }
        Element targetElement = rootElement.getChild("target");
        IValueEncoder valueEncoder = getValueEncoder(conf, fileScenario);
        IDatabase targetDB = daoDatabaseConfiguration.loadDatabase(targetElement, "", fileScenario, valueEncoder);
        List<SQLQueryString> sqlQueries = loadSQLQueries(fileScenario);
        resultExporter.exportSolutionInSeparateFiles(targetDB, valueEncoder, conf.isExportQueryResultsWithHeader(), conf.getExportSolutionsPath(), conf.getMaxNumberOfThreads());
        long endExportTime = new Date().getTime();
        PrintUtility.printInformation("------------------------------------------");
        PrintUtility.printInformation("*** Export time:  " + (endExportTime - startExportTime) + " ms");
        PrintUtility.printInformation("------------------------------------------");
        long startQueryTime = new Date().getTime();
        finalQueryExecutor.executeSQLQueries(targetDB, sqlQueries, conf, valueEncoder);
        long endQueryTime = new Date().getTime();
        PrintUtility.printInformation("------------------------------------------");
        PrintUtility.printInformation("*** Query time:  " + (endQueryTime - startQueryTime) + " ms");
        PrintUtility.printInformation("------------------------------------------");
    }

    private static IValueEncoder getValueEncoder(LunaticConfiguration configuration, String fileScenario) {
        if (!configuration.isUseDictionaryEncoding()) {
            return null;
        }
        return new DictionaryEncoder(DAOUtility.extractScenarioName(fileScenario));
    }

    @SuppressWarnings("unchecked")
    private static List<SQLQueryString> loadSQLQueries(String fileScenario) {
        String queryPath = LunaticUtility.getSQLQueriesPath(fileScenario);
        ObjectInputStream in = null;
        try {
            File queryFile = new File(queryPath);
            if(!queryFile.exists()){
                return Collections.EMPTY_LIST;
            }
            in = new ObjectInputStream(new FileInputStream(queryFile));
            List<SQLQueryString> result = (List<SQLQueryString>) in.readObject();
            in.close();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException("Unable to save sql queries to file " + queryPath + ".\n" + ex.getLocalizedMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
            }
        }
    }
}
