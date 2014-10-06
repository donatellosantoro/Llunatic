package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEmployeesNoSymmetric extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestEmployeesNoSymmetric.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_comparisons_nosymmetric);
//        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_comparisons_nosymmetric_dbms);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseSymmetricOptimization(false);
//        scenario.getConfiguration().setUseLimit1(true);
        try {
            ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
            DeltaChaseStep result = chaser.doChase(scenario);
            Assert.fail();
        } catch (Exception error) {
        }
    }
}
