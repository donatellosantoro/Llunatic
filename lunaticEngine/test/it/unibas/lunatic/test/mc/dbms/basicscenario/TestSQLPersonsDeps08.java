package it.unibas.lunatic.test.mc.dbms.basicscenario;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DBMSException;

public class TestSQLPersonsDeps08 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLPersonsDeps08.class);

    public void test08() throws Exception {
        try {
            String scenarioName = "persons-deps-08";
            Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_08_dbms, true);
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
            setConfigurationForTest(scenario);
            scenario.getConfiguration().setCheckAllNodesForEGDSatisfaction(true);
            scenario.getConfiguration().setRemoveDuplicates(false);
            DeltaChaseStep result = ChaserFactory.getChaser(scenario).doChase(scenario);
            if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
            if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
            if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
            if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
            checkSolutions(result);
            checkExpectedSolutions("expected-" + scenarioName, result);
            fail();
//        exportResults("/Temp/expected-" + scenarioName, result);
//        checkExpectedSolutions("expected-" + scenarioName, result);
        } catch (DBMSException ex) {
        }
    }
}
