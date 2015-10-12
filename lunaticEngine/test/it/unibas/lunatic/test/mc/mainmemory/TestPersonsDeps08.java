package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersonsDeps08 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersonsDeps08.class);
    
    public void test04() throws Exception { //Contraddicting MD with SimilarityMostFrequent
        String scenarioName = "persons-deps-04";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_08);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(false);
        DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }
}
