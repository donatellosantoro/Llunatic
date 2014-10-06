package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.UtilityTest;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSynth extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestSynth.class);

    public void testScenarioST() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/synt/synt25-mcscenario-0k.xml");
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
    }
}
