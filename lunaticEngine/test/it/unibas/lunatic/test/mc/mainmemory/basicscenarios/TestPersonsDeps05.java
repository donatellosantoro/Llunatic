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

public class TestPersonsDeps05 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersonsDeps05.class);

    public void test05() throws Exception { //Rewriting
        String scenarioName = "persons-deps-05";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_05);
        if (logger.isDebugEnabled()) logger.debug(scenario.toStringDependencies());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(false);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(7, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }

    public void test05b() throws Exception { //Rewriting
        String scenarioName = "persons-deps-05b";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_05b);
        if (logger.isDebugEnabled()) logger.debug(scenario.toStringDependencies());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(false);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(13, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }

    public void test05c() throws Exception { //Rewriting
        String scenarioName = "persons-deps-05c";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_05c);
        if (logger.isDebugEnabled()) logger.debug(scenario.toStringDependencies());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(true);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
//        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(13, resultSizer.getPotentialSolutions(result));
//        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
//        checkExpectedSolutions("expected-" + scenarioName, result);
    }
}
