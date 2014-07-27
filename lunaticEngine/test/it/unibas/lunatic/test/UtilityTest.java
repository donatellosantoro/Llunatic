package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.io.File;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityTest {

    private static Logger logger = LoggerFactory.getLogger(UtilityTest.class);

    public static Scenario loadScenario(String scenarioName) {
        return loadScenario(scenarioName, false);
    }

    public static Scenario loadScenario(String scenarioName, boolean recreateDB) {
        try {
            String fileScenario = new File(UtilityTest.class.getResource(scenarioName).toURI()).getAbsolutePath();
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
//        return getSize(table.getTupleIterator());
    }
//    public static int getSize(ITupleIterator iterator) {
//        int i = 0;
//        while (iterator.hasNext()) {
//            iterator.next();
//            i++;
//        }
//        iterator.reset();
//        return i;
//    }
}
