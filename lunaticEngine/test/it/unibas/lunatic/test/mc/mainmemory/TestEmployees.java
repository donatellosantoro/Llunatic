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

public class TestEmployees extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestEmployees.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_comparisons);
        setConfigurationForTest(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        Assert.assertEquals(47, resultSizer.getSolutions(result));
        Assert.assertEquals(3, resultSizer.getDuplicates(result));
        Assert.assertEquals(0, resultSizer.getInvalids(result));
    }
}
