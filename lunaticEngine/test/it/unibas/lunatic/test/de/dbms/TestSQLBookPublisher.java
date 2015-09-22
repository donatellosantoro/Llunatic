package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;

public class TestSQLBookPublisher extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLBookPublisher.class);

//    public void testScenarioClassic() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms, true);
//        scenario.getConfiguration().setDeChaser(LunaticConstants.CLASSIC_DE_CHASER);
//        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
//        if (logger.isDebugEnabled()) logger.debug(result.toString());
//        checkExpectedInstances(result, scenario);
//    }

    public void testScenarioProxy() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms, true);
        scenario.getConfiguration().setDeChaser(LunaticConstants.PROXY_MC_CHASER);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
