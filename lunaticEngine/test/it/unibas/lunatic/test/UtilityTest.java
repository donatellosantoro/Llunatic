package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.io.File;
import java.net.URL;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityTest {

    private static Logger logger = LoggerFactory.getLogger(UtilityTest.class);

    public static Scenario loadScenario(String scenarioName) {
        return loadScenario(scenarioName, false);
    }

    public static Scenario loadScenarioFromAbsolutePath(String scenarioName) {
        return loadScenarioFromAbsolutePath(scenarioName, false);
    }

    public static Scenario loadScenario(String scenarioName, boolean recreateDB) {
        return loadScenario(scenarioName, recreateDB, false);
    }

    public static Scenario loadScenarioFromAbsolutePath(String scenarioName, boolean recreateDB) {
        return loadScenario(scenarioName, recreateDB, true);
    }

    private static Scenario loadScenario(String scenarioPath, boolean recreateDB, boolean absolutePath) {
        Assert.assertNotNull(scenarioPath);
        try {
            String fileScenario;
            if (absolutePath) {
                fileScenario = scenarioPath;
            } else {
                URL url = UtilityTest.class.getResource(scenarioPath);
                Assert.assertNotNull("File " + scenarioPath + " doesn't exist", url);
                fileScenario = new File(url.toURI()).getAbsolutePath();
            }
            DAOMCScenario daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            if (recreateDB) {
                deleteDB(scenario);
                scenario = daoScenario.loadScenario(fileScenario);
            }
            scenario.setAbsolutePath(fileScenario);
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
            return scenario;
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
            return null;
        }
    }

    public static void deleteDB(Scenario scenario) {
        String script = "DROP DATABASE " + ((DBMSDB) scenario.getTarget()).getAccessConfiguration().getDatabaseName() + ";\n";
        if (logger.isDebugEnabled()) logger.debug("Executing script " + script);
        QueryManager.executeScript(script, DBMSUtility.getTempAccessConfiguration(((DBMSDB) scenario.getTarget()).getAccessConfiguration()), true, true, true);
    }

    public static String getAbsoluteFileName(String fileName) {
        return UtilityTest.class.getResource(fileName).getFile();
    }

    public static String getExternalFolder(String fileName) {
        File buildDir = new File(UtilityTest.class.getResource("/").getFile()).getParentFile();
        File rootDir = buildDir.getParentFile();
        String miscDir = rootDir.toString() + File.separator + "misc";
        return miscDir + fileName;
    }

    public static int getSize(ITable table) {
        return table.getSize();
    }
}
