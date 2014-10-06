package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSynthetic08 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSynthetic08.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_08);
        setConfigurationForTest(scenario);
        scenario.getCostManager().setDoBackward(false);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getSolutions(result));
        checkSolutions(result);
        
        Map<String, List<PrecisionAndRecall>> quality = compareWithExpectedInstances(result, "expected08", Arrays.asList(new String[]{LunaticConstants.OID, LunaticConstants.TID}), 1.0, false);
        if (logger.isDebugEnabled()) logger.debug(printPrecisionAndRecall(quality));
        checkQuality(quality);
    }
}
