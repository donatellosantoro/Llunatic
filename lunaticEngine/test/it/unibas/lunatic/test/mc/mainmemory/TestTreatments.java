package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryRunQuery;
import it.unibas.lunatic.test.GenerateModifiedCells;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTreatments extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestTreatments.class);

    protected GenerateModifiedCells modifiedCellsGenerator = new GenerateModifiedCells(new MainMemoryRunQuery());

    public void testScenarioPOFRSP() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_fr_s5_poset);
        setConfigurationForTest(scenario);
        setCheckEGDsAfterEachStep(scenario);
        scenario.getCostManager().setDoPermutations(false);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
        Assert.assertEquals(1, resultSizer.getSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
    }

    public void testScenarioPOFRS5() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_fr_s5_poset);
        setConfigurationForTest(scenario);
        setCheckEGDsAfterEachStep(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
//        Assert.assertEquals(8, resultSizer.getSolutions(result));
        Assert.assertEquals(2, resultSizer.getSolutions(result));
        Assert.assertEquals(34, resultSizer.getDuplicates(result));
    }
//    

    public void testScenarioPOS50() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_poset);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
//        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toShortStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
//        Assert.assertEquals(34, resultSizer.getSolutions(result));
        Assert.assertEquals(22, resultSizer.getSolutions(result));
        Assert.assertEquals(35, resultSizer.getDuplicates(result));
    }
}
