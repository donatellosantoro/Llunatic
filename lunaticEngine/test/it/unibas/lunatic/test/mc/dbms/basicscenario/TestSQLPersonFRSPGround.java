package it.unibas.lunatic.test.mc.dbms.basicscenario;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLPersonFRSPGround extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLPersonFRSPGround.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_fr_sp_ground_dbms, true);
        setConfigurationForTest(scenario);
        scenario.getCostManagerConfiguration().setType(LunaticConstants.COST_MANAGER_SIMILARITY);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.getDeltaDB().printInstances(false));
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-frsp-ground", result);
        checkExpectedSolutions("expected-frsp-ground", result);
    }

}
