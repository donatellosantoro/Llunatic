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

public class TestSynthetic03 extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSynthetic03.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_03);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setRemoveDuplicates(true);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(10, resultSizer.getSolutions(result));
        Assert.assertEquals(6, resultSizer.getSolutions(result));
        Assert.assertEquals(4, resultSizer.getDuplicates(result));
        checkSolutions(result);
    }
}
