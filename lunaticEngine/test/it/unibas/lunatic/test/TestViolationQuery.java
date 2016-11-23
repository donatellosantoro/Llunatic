package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.test.mc.dbms.TSQLHospital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestViolationQuery extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TSQLHospital.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("mc/bus/bus-violations-dbms.xml", true);
        setConfigurationForTest(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        for (Dependency extEGD : scenario.getExtEGDs()) {
            boolean satisfied = ChaseUtility.checkEGDSatisfactionWithQuery(extEGD, scenario.getTarget(), scenario);
            if (satisfied && logger.isDebugEnabled()) logger.debug("EGD " + extEGD.getId() + " is satisfied");
            if (!satisfied && logger.isDebugEnabled()) logger.debug("EGD " + extEGD.getId() + " is violated");
        }
    }
}
