package it.unibas.lunatic.test.mc.mainmemory.tgd;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSyntheticTGD04 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSyntheticTGD04.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T04);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getSymmetricCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.getDeltaDB().getTable(LunaticConstants.CELLGROUP_TABLE).toString());
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getSolutions(result));
//        Assert.assertEquals(4, OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario).loadAllCellGroupsInStep(result.getDeltaDB(), "r").size());
//        Assert.assertEquals(7, OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario).loadAllCellGroups(result.getDeltaDB(), "r.tt0").size());
//        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        checkExpectedInstances((MainMemoryDB) result, scenario);
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSyntheticTGD04", result);
        checkExpectedSolutions("expectedSyntheticTGD04", result);
    }
}
