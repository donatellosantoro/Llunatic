package it.unibas.lunatic.test.mc.mainmemory.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDoctors extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestDoctors.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.doctors);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        scenario.getCostManagerConfiguration().addNoBackwardDependency("md1");
//        DependencyUtility.findDependency("md1", scenario.getExtEGDs()).setDoBackward(false);
        scenario.getCostManagerConfiguration().setRequestMajorityInSimilarityCostManager(true);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
//        if (logger.isDebugEnabled()) logger.debug(result.toLongStringLeavesOnlyWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        assertEquals(15, resultSizer.getSolutions(result));
        assertEquals(9, resultSizer.getDuplicates(result));
//        checkSolutions(result);
//        exportResults("/Temp/expectedDoctorsMM/", result);
        checkExpectedSolutions("expectedDoctors", result);
    }
}
