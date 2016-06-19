package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSynthetic10 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSynthetic10.class);

//    public void testScenario() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_10);
//        setConfigurationForTest(scenario);
////        ChaserFactory.setDoBackward(false);
//        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
//        DeltaChaseStep result = chaser.doChase(scenario);
//        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
//        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
//        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(3, resultSizer.getSolutions(result));
//        Assert.assertEquals(0, resultSizer.getDuplicates(result));
////        checkExpectedInstances((MainMemoryDB) result, scenario);
//        checkSolutions(result);
////        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSynthetic07", result);
////        checkExpectedSolutions("expectedSynthetic07", result);
//    }

    public void testScenarioSimilarity() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_10_similarity);
        setConfigurationForTest(scenario);
//        ChaserFactory.setDoBackward(false);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        checkExpectedInstances((MainMemoryDB) result, scenario);
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSynthetic07", result);
//        checkExpectedSolutions("expectedSynthetic07", result);
    }

}
