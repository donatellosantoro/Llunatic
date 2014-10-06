package it.unibas.lunatic.test.mc.mainmemory.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSyntheticTGD06 extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSyntheticTGD06.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T06);
        setConfigurationForTest(scenario);
//        scenario.getConfiguration().setRemoveDuplicates(false);
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getSolutions(result));
        for (Dependency dependency : scenario.getExtEGDs()) {
            if (logger.isDebugEnabled()) logger.debug(dependency.toLongString());
        }
//        if (logger.isDebugEnabled()) logger.debug("Duplicate solutions: " + resultSizer.getDuplicates(result));
//        checkExpectedInstances((MainMemoryDB) result, scenario);
        checkSolutions(result);
    }
}
