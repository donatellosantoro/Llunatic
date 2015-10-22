package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCustomers extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestCustomers.class);

    public void testScenarioDelta() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.customers_cfd);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setUseSymmetricOptimization(false);
//        scenario.getConfiguration().setDiscardDuplicateTuples(true);
//        scenario.getConfiguration().setRemoveDuplicates(true);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        Assert.assertEquals(36, resultSizer.getSolutions(result));
        Assert.assertEquals(14, resultSizer.getDuplicates(result));
        checkSolutions(result);
    }
}
