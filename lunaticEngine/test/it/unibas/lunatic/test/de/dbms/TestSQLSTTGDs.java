package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestSQLSTTGDs extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLSTTGDs.class);

    public void testSTTGD0() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/sttgd0-mcscenario-dbms.xml", true);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testSTTGD1() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/sttgd1-mcscenario-dbms.xml", true);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testSTTGD2() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/sttgd2-mcscenario-dbms.xml", true);
        scenario.getConfiguration().setForceOptimizeSTTGDs(true);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
