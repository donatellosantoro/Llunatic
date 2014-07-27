package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLEmployees extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLEmployees.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.employees_rew_dbms);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testScenarioEGD() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.employees_egd_dbms);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        checkExpectedInstances(result, scenario);
    }

    public void testScenarioDenial() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.employees_dtgd_dbms);
        try {
            DEChaserFactory.getChaser(scenario).doChase(scenario);
            fail();
        } catch (ChaseException ex) {
        }
    }
}
