package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestChaseEgd extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestChaseEgd.class);

    public void testRSEgd() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_egd);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testRSTgdEgd() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_tgd_egd);
        scenario.getConfiguration().setDeChaser(LunaticConstants.PROXY_MC_CHASER);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
