package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
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
//        scenario.getConfiguration().setUseSymmetricOptimization(false);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        ChaseMCScenario chaser = scenario.getSymmetricCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        Assert.assertTrue(scenario.getDependency("e1").hasSymmetricChase());
        //Assert.assertTrue(scenario.getDependency("e1b").hasSymmetricAtoms());
        Assert.assertTrue(scenario.getDependency("e2").hasSymmetricChase());
        Assert.assertFalse(scenario.getDependency("e4").hasSymmetricChase());
        Assert.assertFalse(scenario.getDependency("e5").hasSymmetricChase());
        Assert.assertEquals(3, resultSizer.getSolutions(result));
        Assert.assertEquals(3, resultSizer.getInvalids(result));
    }
}
