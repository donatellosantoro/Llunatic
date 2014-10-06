package it.unibas.lunatic.test.mc.dbms.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLSyntheticTGD02 extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLSyntheticTGD02.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T02_dbms);
        setConfigurationForTest(scenario);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(5, resultSizer.getPotentialSolutions(result));
//        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        checkExpectedInstances((MainMemoryDB) result, scenario);
        checkSolutions(result);
    }
}
