package it.unibas.lunatic.test.ded.mainmemory;

import it.unibas.lunatic.test.de.mainmemory.TestChaseDTgds;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDed extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestDed.class);

    public void test() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.deds_rs);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

}
