package it.unibas.lunatic.test.mc.dbms.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLSyntheticTGD03 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLSyntheticTGD03.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T03_dbms, true);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseStandardChase(false);
//        ChaserFactory.setDoBackward(false);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toLongStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        checkSolutions(result);
        checkExpectedSolutions("expectedTGD03", result);
    }

    public void testScenarioStandardChase() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T03_dbms, true);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setUseStandardChase(true);
//        ChaserFactory.setDoBackward(false);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toLongStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        checkSolutions(result);
        checkExpectedSolutions("expectedTGD03", result);
    }
}
