package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestTGDs extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestTGDs.class);

    public void testTgd0() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgd0-mcscenario.xml");
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testTgd1() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgd1-mcscenario.xml");
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testTgd2() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgd2-mcscenario.xml");
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
//        exportResults("/Temp/expectedTGD2/", result);
        checkExpectedInstances(result, scenario);
    }
}
