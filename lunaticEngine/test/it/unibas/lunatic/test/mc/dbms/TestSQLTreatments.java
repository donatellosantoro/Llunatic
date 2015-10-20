package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.persistence.relational.QueryStatManager;

public class TestSQLTreatments extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLTreatments.class);

    public void testScenarioPOFRSP() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_fr_sp_dbms, true);
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
        Assert.assertEquals(1, resultSizer.getSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        exportResults("/Temp/expectedTreatmentsScenarioPOFRSP", result);
        checkExpectedSolutions("expectedTreatmentsScenarioPOFRSP", result);
    }

    public void testScenarioPOFRS5() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_fr_s5_dbms, true);
        setConfigurationForTest(scenario);
        setCheckEGDsAfterEachStep(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringLeavesOnlyWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
        Assert.assertEquals(3, resultSizer.getSolutions(result));
        Assert.assertEquals(21, resultSizer.getDuplicates(result));
//        exportResults("/Temp/expectedTreatmentsScenarioPOFRS5", result);
        checkExpectedSolutions("expectedTreatmentsScenarioPOFRS5", result);
    }

    public void testScenarioPOS50() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_dbms, true);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        QueryStatManager.getInstance().printStatistics();
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isTraceEnabled()) logger.debug("Result: " + result.toShortStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
//        Assert.assertEquals(34, resultSizer.getSolutions(result));
        Assert.assertEquals(23, resultSizer.getSolutions(result));
        Assert.assertEquals(34, resultSizer.getDuplicates(result));
    }

    public void testScenarioFRSPScript() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_fr_sp_script_dbms, true);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        QueryStatManager.getInstance().printStatistics();
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        checkSolutions(result);
        Assert.assertEquals(1, resultSizer.getSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
    }
}
