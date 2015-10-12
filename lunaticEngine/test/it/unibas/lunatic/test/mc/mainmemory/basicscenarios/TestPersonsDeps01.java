package it.unibas.lunatic.test.mc.mainmemory.basicscenarios;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersonsDeps01 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersonsDeps01.class);

    public void test01() throws Exception { //FD Single column
        String scenarioName = "persons-deps-01";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_01);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);//TODO++ Remove
        scenario.getConfiguration().setDiscardDuplicateTuples(true);//TODO++ Remove
//        scenario.getConfiguration().setRemoveDuplicates(false);
//        scenario.getCostManager().setDoBackward(false);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(7, resultSizer.getPotentialSolutions(result)); // 2^(#groups) - 1
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }

    public void test01b() throws Exception { //FD Single column
        String scenarioName = "persons-deps-01b";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_01b);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);//TODO++ Remove
        scenario.getConfiguration().setDiscardDuplicateTuples(true);//TODO++ Remove
        scenario.getConfiguration().setRemoveDuplicates(false);
//        scenario.getCostManager().setDoBackward(false);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(13, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }
}
