package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestInsertNaive extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestInsertNaive.class);

    public void testRemoveDuplicate() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        scenario.getConfiguration().setDeChaser(LunaticConstants.CLASSIC_DE_CHASER);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
