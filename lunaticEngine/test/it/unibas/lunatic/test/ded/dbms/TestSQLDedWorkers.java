package it.unibas.lunatic.test.ded.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLDedWorkers extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLDedWorkers.class);

    public void testSolution() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.deds_workers_1_dbms, true);
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        Assert.assertNotNull(result);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        chaseStats.printStatistics();
    }

    public void testInstance1() {//20 Success, 0 Failed
        testAllSolutions(References.deds_workers_1_dbms, true);
        chaseStats.printStatistics("INSTANCE 1\n");
        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
        long success = executed - failed;
        Assert.assertEquals(total, executed);
        Assert.assertEquals(20, success);
        Assert.assertEquals(0, failed);
    }

    public void testAllSolutions2() {//10 Success, 10 Failed
        testAllSolutions(References.deds_workers_2_dbms, true);
        chaseStats.printStatistics("INSTANCE 2\n");
        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
        long success = executed - failed;
        Assert.assertEquals(total, executed);
        Assert.assertEquals(10, success);
        Assert.assertEquals(10, failed);
    }

//    public void testAllSolutions3() {//4 Success, 16 Failed
//        testAllSolutions(References.deds_workers_3_dbms, true);
//        chaseStats.printStatistics("INSTANCE 3\n");
//        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
//        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
//        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
//        long success = executed - failed;
//        Assert.assertEquals(total, executed);
//        Assert.assertEquals(4, success);
//        Assert.assertEquals(16, failed);
//    }

    public void testAllSolutions4() { //16 Success, 4 Failed
        testAllSolutions(References.deds_workers_4_dbms, true);
        chaseStats.printStatistics("INSTANCE 4\n");
        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
        long success = executed - failed;
        Assert.assertEquals(total, executed);
        Assert.assertEquals(16, success);
        Assert.assertEquals(4, failed);
    }


    public void testAllSolutions5() { //0 Success, 20 Failed
        testAllSolutions(References.deds_workers_5_dbms, true);
        chaseStats.printStatistics("INSTANCE 5\n");
        long total = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_GREEDY_SCENARIOS);
        long executed = (long) chaseStats.getStat(ChaseStats.NUMBER_OF_EXECUTED_GREEDY_SCENARIOS);
        long failed = (chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS) == null ? 0 : chaseStats.getStat(ChaseStats.NUMBER_OF_FAILED_GREEDY_SCENARIOS));
        long success = executed - failed;
        Assert.assertEquals(total, executed);
        Assert.assertEquals(0, success);
        Assert.assertEquals(20, failed);
    }

    private void testAllSolutions(String fileScenario, boolean recreateDB) {
        Scenario scenario = UtilityTest.loadScenarioFromResources(fileScenario, recreateDB);
        scenario.getConfiguration().setChaseDEDGreedyExecuteAllScenarios(true);
//        scenario.getConfiguration().setDeChaser(LunaticConstants.CLASSIC_DE_CHASER);
        try {
            IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
            Assert.assertNotNull(result);
            if (logger.isDebugEnabled()) logger.debug(result.printInstances(true));
        } catch (ChaseException ex) {
            if (logger.isDebugEnabled()) logger.debug("No solution...");
            logger.error(ex.getLocalizedMessage());
        }
    }
}
