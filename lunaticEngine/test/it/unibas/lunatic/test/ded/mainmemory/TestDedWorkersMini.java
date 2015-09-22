package it.unibas.lunatic.test.ded.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDedWorkersMini extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestDedWorkersMini.class);

    public void testSolution() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.deds_workers_1);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isTraceEnabled()) logger.debug(scenario.toString());
        Assert.assertNotNull(result);
        if (logger.isTraceEnabled()) logger.debug(result.toString());
        chaseStats.printStatistics();
    }

    public void testAllSolutions1() {//20 Success, 0 Failed
        testAllSolutions(References.deds_workers_1);
        chaseStats.printStatistics("INSTANCE 1\n");
        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
        long success = executed - failed;
        if (logger.isDebugEnabled()) logger.debug("Total: " + total + " - Executed: " + executed + " - Success: " + success + " - Failed:" + failed);
        Assert.assertEquals(total, executed);
        Assert.assertEquals(20, success);
        Assert.assertEquals(0, failed);
    }

//    public void testAllSolutions2() {//10 Success, 10 Failed
//        testAllSolutions(References.deds_workers_2);
//        chaseStats.printStatistics("INSTANCE 2\n");
//        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
//        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
//        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
//        long success = executed - failed;
//        if (logger.isDebugEnabled()) logger.debug("Total: " + total + " - Executed: " + executed + " - Success: " + success + " - Failed:" + failed);
//        Assert.assertEquals(total, executed);
//        Assert.assertEquals(10, success);
//        Assert.assertEquals(10, failed);
//    }
//
//    public void testAllSolutions3() {//4 Success, 16 Failed
//        testAllSolutions(References.deds_workers_3);
//        chaseStats.printStatistics("INSTANCE 3\n");
//        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
//        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
//        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
//        long success = executed - failed;
//        if (logger.isDebugEnabled()) logger.debug("Total: " + total + " - Executed: " + executed + " - Success: " + success + " - Failed:" + failed);
//        Assert.assertEquals(total, executed);
//        Assert.assertEquals(4, success);
//        Assert.assertEquals(16, failed);
//    }
//
//    public void testAllSolutions4() { //16 Success, 4 Failed
//        testAllSolutions(References.deds_workers_4);
//        chaseStats.printStatistics("INSTANCE 4\n");
//        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
//        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
//        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
//        long success = executed - failed;
//        if (logger.isDebugEnabled()) logger.debug("Total: " + total + " - Executed: " + executed + " - Success: " + success + " - Failed:" + failed);
//        Assert.assertEquals(total, executed);
//        Assert.assertEquals(16, success);
//        Assert.assertEquals(4, failed);
//    }
//
//    public void testAllSolutions5() { //0 Success, 20 Failed
//        testAllSolutions(References.deds_workers_5);
//        chaseStats.printStatistics("INSTANCE 5\n");
//        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
//        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
//        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
//        long success = executed - failed;
//        if (logger.isDebugEnabled()) logger.debug("Total: " + total + " - Executed: " + executed + " - Success: " + success + " - Failed:" + failed);
//        Assert.assertEquals(total, executed);
//        Assert.assertEquals(0, success);
//        Assert.assertEquals(20, failed);
//    }

    private void testAllSolutions(String fileScenario) {
        Scenario scenario = UtilityTest.loadScenarioFromResources(fileScenario);
        scenario.getConfiguration().setChaseDEDGreedyExecuteAllScenarios(true);
//        scenario.getConfiguration().setDeChaser(LunaticConstants.CLASSIC_DE_CHASER);
        try {
            IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
            Assert.assertNotNull(result);
            if (logger.isTraceEnabled()) logger.debug(result.printInstances(true));
        } catch (ChaseException ex) {
            if (logger.isTraceEnabled()) logger.debug("No solution...");
        }
    }
}
