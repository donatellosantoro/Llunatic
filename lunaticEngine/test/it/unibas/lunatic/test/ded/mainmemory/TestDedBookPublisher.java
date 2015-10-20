package it.unibas.lunatic.test.ded.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestDedBookPublisher extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestDedBookPublisher.class);

    public void testScenarioST() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_ded);
//        scenario.getConfiguration().setDeChaser(LunaticConstants.CLASSIC_DE_CHASER);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        Assert.assertNotNull(result);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        chaseStats.printStatistics();
    }
}
