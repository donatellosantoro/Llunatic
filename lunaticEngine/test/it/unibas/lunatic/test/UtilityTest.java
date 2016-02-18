package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.Assert;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.ITable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.persistence.xml.DAOXmlUtility;

public class UtilityTest {

    private static Logger logger = LoggerFactory.getLogger(UtilityTest.class);
    private static DAOXmlUtility daoUtility = new DAOXmlUtility();
    public static final String RESOURCES_FOLDER = "/resources/";

    public static Scenario loadScenarioFromResources(String fileScenario) {
        return loadScenarioFromResources(fileScenario, false);
    }

    public static Scenario loadScenarioFromAbsolutePath(String fileScenario) {
        return loadScenarioFromAbsolutePath(fileScenario, null, false);
    }

    public static Scenario loadScenarioFromResources(String fileScenario, boolean recreateDB) {
        return loadScenarioFromResources(fileScenario, null, recreateDB);
    }

    public static Scenario loadScenarioFromResources(String fileScenario, String suffix) {
        return loadScenarioFromResources(fileScenario, suffix, false);
    }

    public static Scenario loadScenarioFromResources(String fileScenario, String suffix, boolean recreateDB) {
        try {
            fileScenario = RESOURCES_FOLDER + fileScenario;
            URL scenarioURL = UtilityTest.class.getResource(fileScenario);
            Assert.assertNotNull("Load scenario " + fileScenario, scenarioURL);
            fileScenario = new File(scenarioURL.toURI()).getAbsolutePath();
            return loadScenario(fileScenario, suffix, recreateDB);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
            return null;
        }
    }

    public static Scenario loadScenarioFromAbsolutePath(String fileScenario, String suffix, boolean recreateDB) {
        return loadScenario(fileScenario, suffix, recreateDB);
    }

    private static Scenario loadScenario(String fileScenario, String suffix, boolean recreateDB) {
        if (logger.isDebugEnabled()) logger.debug("Loading scenario: " + fileScenario);
        Assert.assertNotNull(fileScenario);
        try {
            if (recreateDB) {
                deleteDB(loadTargetAccessConfiguration(fileScenario, suffix));
            }
        } catch (Exception ex) {
            logger.warn("Unable to drop database.\n" + ex.getLocalizedMessage()); //Fail if DBMS error and continue if not exists
        }
        try {
            DAOMCScenario daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario, suffix);
            scenario.setAbsolutePath(fileScenario);
            return scenario;
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
            return null;
        }
    }

    public static void deleteDB(AccessConfiguration accessConfiguration) {
        String script = "DROP DATABASE " + accessConfiguration.getDatabaseName() + ";\n";
        if (logger.isDebugEnabled()) logger.debug("Executing script " + script);
        QueryManager.executeScript(script, LunaticDBMSUtility.getTempAccessConfiguration(accessConfiguration), true, true, true, false);
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

    public static long getSize(ITable table) {
        return table.getSize();
    }

    private static AccessConfiguration loadTargetAccessConfiguration(String fileScenario, String suffix) {
        Document document = daoUtility.buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <access-configuration>");
        }
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        if (suffix != null && !suffix.trim().isEmpty()) {
            accessConfiguration.setSchemaSuffix(suffix.trim());
        }
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }
}
