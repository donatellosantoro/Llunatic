package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEmployeesNoSymmetric extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestEmployeesNoSymmetric.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_comparisons_nosymmetric);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringLeavesOnlyWithSort());
//        exportResults("/Temp/expectedEmployeesNoSymmetric", result);
        checkExpectedSolutions("expectedEmployeesNoSymmetric", result);
    }
}
