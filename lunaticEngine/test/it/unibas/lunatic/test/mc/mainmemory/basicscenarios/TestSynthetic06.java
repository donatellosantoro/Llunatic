package it.unibas.lunatic.test.mc.mainmemory.basicscenarios;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSynthetic06 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSynthetic06.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_06);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);//TODO++ Remove
        scenario.getConfiguration().setDiscardDuplicateTuples(true);//TODO++ Remove
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(3, resultSizer.getSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//                exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSynthetic06", result);
        checkExpectedSolutions("expectedSynthetic06", result);
    }
}
