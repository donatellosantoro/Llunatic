package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestTGDStratification extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestTGDStratification.class);

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
}
