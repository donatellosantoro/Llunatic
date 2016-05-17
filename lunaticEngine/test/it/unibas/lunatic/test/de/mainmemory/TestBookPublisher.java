package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestBookPublisher extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestBookPublisher.class);

    public void testScenarioSTOptimized() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        scenario.getConfiguration().setDeChaser(LunaticConstants.DE_OPTIMIZED_CHASER);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testScenarioEGDOptimized() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_egd);
        scenario.getConfiguration().setDeChaser(LunaticConstants.DE_OPTIMIZED_CHASER);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
