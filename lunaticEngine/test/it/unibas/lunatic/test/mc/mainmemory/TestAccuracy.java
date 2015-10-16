package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAccuracy extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestAccuracy.class);

    public void testScenarioOrderingAttribute() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.accuracy_poset);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(true);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("accuracy", scenario));
//        if (logger.isDebugEnabled()) logger.debug(result.toShortStringWithSortWithoutDuplicates());
        if (logger.isDebugEnabled()) logger.debug(result.toStringLeavesOnlyWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicates: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-accuracy", result);
        checkExpectedSolutions("expected-accuracy", result);
    }

    public void testScenarioOrderingAttributeNonSymmetric() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.accuracy_poset);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);
        scenario.getConfiguration().setDiscardDuplicateTuples(true);
        scenario.getConfiguration().setRemoveDuplicates(true);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("accuracy", scenario));
//        if (logger.isDebugEnabled()) logger.debug(result.toShortStringWithSortWithoutDuplicates());
        if (logger.isDebugEnabled()) logger.debug(result.toStringLeavesOnlyWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicates: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
        checkExpectedSolutions("expected-accuracy", result);
    }
}
