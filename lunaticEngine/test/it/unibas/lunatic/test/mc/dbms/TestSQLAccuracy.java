package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLAccuracy extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLAccuracy.class);

    public void testScenarioOrderingAttribute() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.accuracy_poset_dbms, true);
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
