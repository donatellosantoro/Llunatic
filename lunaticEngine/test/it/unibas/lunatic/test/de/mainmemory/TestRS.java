package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestRS extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestRS.class);

    public void testTgd0() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/RS/RS-affected-mcscenario.xml");
        scenario.getConfiguration().setOptimizeSTTGDs(false);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
