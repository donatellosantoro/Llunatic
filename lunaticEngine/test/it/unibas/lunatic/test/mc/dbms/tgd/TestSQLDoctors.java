package it.unibas.lunatic.test.mc.dbms.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLDoctors extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLDoctors.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.doctors_dbms, true);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        scenario.getCostManagerConfiguration().addNoBackwardDependency("md1");
//        DependencyUtility.findDependency("md1", scenario.getExtEGDs()).setDoBackward(false);
        scenario.getCostManagerConfiguration().setRequestMajorityInSimilarityCostManager(true);
        scenario.getConfiguration().setOptimizeSTTGDs(false);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
//        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug(result.toLongStringWithSort());
        assertEquals(7, resultSizer.getSolutions(result));
        assertEquals(44, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expectedDoctors/", result);
        checkExpectedSolutions("expectedDoctors", result);
    }
}
