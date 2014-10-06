package it.unibas.lunatic.test.ded.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLDed extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLDed.class);

    public void test() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.deds_rs_dbms, true);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
    
    public void testRSSTTGDs() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.deds_rs_sttgds_dbms, true);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

}
