package it.unibas.lunatic.test.mc.mainmemory.basicscenarios;

import it.unibas.lunatic.LunaticConstants;
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

public class TestPersonsFRSP extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersonsFRSP.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_fr_sp);
        setConfigurationForTest(scenario);
        scenario.getCostManagerConfiguration().setType(LunaticConstants.COST_MANAGER_SIMILARITY);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("persons", scenario));
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.getDeltaDB().printInstances(false));
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
        checkExpectedSolutions("expected-frsp", result);
    }

    public void testScenarioNonSymmetric() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_fr_sp);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);
        scenario.getConfiguration().setDiscardDuplicateTuples(true);
        scenario.getCostManagerConfiguration().setType(LunaticConstants.COST_MANAGER_SIMILARITY);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("persons", scenario));
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.getDeltaDB().printInstances(false));
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
        checkExpectedSolutions("expected-frsp", result);
    }
}
