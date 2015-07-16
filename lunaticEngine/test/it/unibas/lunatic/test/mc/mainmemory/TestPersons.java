package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersons extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersons.class);

    
    public void testScenarioNoPermutation() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons);
        setConfigurationForTest(scenario);
//        setCheckEGDsAfterEachStep(scenario);
        scenario.getCostManager().setDoBackward(false);
        scenario.getCostManager().setDoPermutations(false);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("persons", scenario));
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
//        if (logger.isDebugEnabled()) logger.debug("Delta db:\n" + result.getDeltaDB().printInstances());
        Map<String, List<PrecisionAndRecall>> quality = compareWithExpectedInstances(result, "expected-nop", Arrays.asList(new String[]{LunaticConstants.OID, LunaticConstants.TID}), 1.0, false);
        if (logger.isDebugEnabled()) logger.debug(printPrecisionAndRecall(quality));
        checkQuality(quality);
    }

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons);
        setConfigurationForTest(scenario);
//        setCheckEGDsAfterEachStep(scenario);
//        scenario.getCostManager().setDoBackward(false);
//        scenario.getCostManager().setDoPermutations(false);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("persons", scenario));
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(9, resultSizer.getPotentialSolutions(result));
        checkSolutions(result);
//        if (logger.isDebugEnabled()) logger.debug("Delta db:\n" + result.getDeltaDB().printInstances());
    }
}
