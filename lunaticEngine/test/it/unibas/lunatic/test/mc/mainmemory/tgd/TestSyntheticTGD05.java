package it.unibas.lunatic.test.mc.mainmemory.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSyntheticTGD05 extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSyntheticTGD05.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T05);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toLongStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getSolutions(result));
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
    }
}
