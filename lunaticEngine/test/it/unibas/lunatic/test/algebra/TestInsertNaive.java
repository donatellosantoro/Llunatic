package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestInsertNaive extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestInsertNaive.class);

    private ChaseMainMemorySTTGDs stChaser = new ChaseMainMemorySTTGDs();

    public void testRemoveDuplicate() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.testR);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }
}
