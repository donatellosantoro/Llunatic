package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestChaseExtTgds extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestChaseExtTgds.class);

    public void testRSTC() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_tc);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        if (!scenario.getConfiguration().isChaseRestricted()) {
            String expected = scenario.getFileName().replace(".xml", "-skolemchase-expectedSolution.txt");
            checkExpectedInstances(result, expected, scenario);
        }else{
            checkExpectedInstances(result, scenario);
        }
    }

    public void testJoin() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_join);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
