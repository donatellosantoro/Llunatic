package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLEmployees extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLEmployees.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_comparisons_dbms);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setUseSymmetricOptimization(false);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        Assert.assertEquals(3, resultSizer.getSolutions(result));
    }
}
