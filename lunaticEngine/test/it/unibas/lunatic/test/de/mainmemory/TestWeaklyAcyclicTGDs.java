package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestWeaklyAcyclicTGDs extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestWeaklyAcyclicTGDs.class);

    public void testWA() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgds-weakly-acyclic-mcscenario.xml");
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
    }

    public void testNotWA() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgds-not-weakly-acyclic-mcscenario.xml");
        try {
            IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
            if (logger.isDebugEnabled()) logger.debug(result.toString());
            fail();
        } catch (ChaseException e) {
        }
    }

    public void testNotWA2() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/tgd/tgds-not-weakly-acyclic2-mcscenario.xml");
        try {
            IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
            if (logger.isDebugEnabled()) logger.debug(result.toString());
            fail();
        } catch (ChaseException e) {
        }
    }
}
