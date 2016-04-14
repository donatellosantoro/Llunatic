package it.unibas.lunatic.test.de.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestChaseDTgds extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestChaseDTgds.class);

    public void testEmployeesDTGDs() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_dtgd);
        try {
            DEChaserFactory.getChaser(scenario).doChase(scenario);
            fail();
        } catch (ChaseException ex) {
        }
    }

}
