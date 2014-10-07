package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLChaseEgd extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLChaseEgd.class);

    public void testRSEgd() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_egd_dbms);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testRSTgdEgd() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_tgd_egd_dbms, true);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
