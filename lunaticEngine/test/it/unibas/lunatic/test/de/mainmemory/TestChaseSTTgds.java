package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestChaseSTTgds extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestChaseSTTgds.class);
    private ChaseMainMemorySTTGDs stChaser = new ChaseMainMemorySTTGDs();

    public void testPublisher() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testEmployees() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_rew);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testCompanies() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.companies_rew);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

}
