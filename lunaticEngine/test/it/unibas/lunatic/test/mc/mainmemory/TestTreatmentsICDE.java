package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.GenerateModifiedCells;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.operators.mainmemory.MainMemoryRunQuery;

public class TestTreatmentsICDE extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestTreatmentsICDE.class);

    protected GenerateModifiedCells modifiedCellsGenerator = new GenerateModifiedCells(new MainMemoryRunQuery());

    public void testScenarioICDE() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_icde);
        scenario.getCostManagerConfiguration().setDoBackward(false);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        setConfigurationForTest(scenario);
        setCheckEGDsAfterEachStep(scenario);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
//        Assert.assertEquals(22, resultSizer.getSolutions(result));
//        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        if (logger.isDebugEnabled()) logger.debug("Delta db:\n" + result.getDeltaDB().printInstances());
    }
}


