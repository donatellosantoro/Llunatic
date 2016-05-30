package it.unibas.lunatic.test.mc.mainmemory.basicscenarios;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSynthetic01 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSynthetic01.class);

    public void testScenario() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_01);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setRemoveDuplicates(true);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(16, resultSizer.getSolutions(result));
        Assert.assertEquals(10, resultSizer.getSolutions(result));
        Assert.assertEquals(6, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSynthetic01", result);
        checkExpectedSolutions("expectedSynthetic01", result);
    }

    public void testScenarioNonSymmetric() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_01);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setRemoveDuplicates(true);
        scenario.getConfiguration().setUseSymmetricOptimization(false);
        scenario.getConfiguration().setDiscardDuplicateTuples(true);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(16, resultSizer.getSolutions(result));
        Assert.assertEquals(10, resultSizer.getSolutions(result));
        Assert.assertEquals(6, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSynthetic01", result);
        checkExpectedSolutions("expectedSynthetic01", result);
    }
}
