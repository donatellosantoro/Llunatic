package it.unibas.lunatic.test.ded.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDed extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestDed.class);

    public void testRS() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.deds_rs);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
    
    public void testRSSTTGDs() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.deds_rs_sttgds);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
    
    public void setUp(){
        ChaseStats.getInstance().resetStatistics();
    }
}
