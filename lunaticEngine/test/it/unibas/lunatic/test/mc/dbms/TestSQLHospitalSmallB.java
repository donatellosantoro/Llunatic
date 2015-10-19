package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.persistence.relational.QueryStatManager;

public class TestSQLHospitalSmallB extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLHospitalSmallB.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.hospital_small_b_dbms, true);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        QueryStatManager.getInstance().printStatistics();
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("hospital", scenario));
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicates: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(1, resultSizer.getSolutions(result));
        checkSolutions(result);
    }
}
