package it.unibas.lunatic.test.mc.dbms.tgd;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.costmanager.MinCostRepairCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.SimilarityToMostFrequentCostManager;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.test.de.mainmemory.TestChaseDTgds;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TTGDScalability extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TTGDScalability.class);
    private int SIZE = 12500;
//    private int SIZE = 25000;
//    private int SIZE = 50000;
//    private int SIZE = 100000;
//    private int SIZE = 200000;
    private int ALREADY_INSERT = 90; //%
    private Random random = new Random();

    @Override
    protected void setUp() throws Exception {
    }

    public void test() throws Exception {
        Scenario scenario = prepareScenario();
//        scenario.getCostManager().setDoBackward(false);
        scenario.setCostManager(new MinCostRepairCostManager());
//        scenario.setCostManager(new SimilarityToMostFrequentCostManager());
//        scenario.setPartialOrder(new FrequencyPartialOrder());
//        scenario.getConfiguration().setUseCellGroupsForTGDs(false);
        if (SIZE <= 1000) setConfigurationForTest(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        long start = new Date().getTime();
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        long end = new Date().getTime();
        System.out.println("Execution time (" + SIZE + "): " + (end - start) + " ms");
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        System.out.println("Solutions " + resultSizer.getSolutions(result));
        if (SIZE <= 1000) checkSolutions(result);
    }

//    public void testLimit1() throws Exception {
//        Scenario scenario = prepareScenario();
//        scenario.getConfiguration().setUseLimit1(true);
//        long start = new Date().getTime();
//        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
//        DeltaChaseStep result = chaser.doChase(scenario);
//        long end = new Date().getTime();
//        System.out.println("Execution time LIMIT1 (" + SIZE + "): " + (end - start) + " ms");
////        Assert.assertEquals(SIZE, scenario.getTarget().getTable("s").getSize());
//    }
    private Scenario prepareScenario() {
        Scenario scenario = UtilityTest.loadScenario(References.synthetic_TScal_dbms, true);
        System.out.print("Generating target... ");
        int toInsert = (SIZE * ALREADY_INSERT) / 100;
        for (int i = 0; i < SIZE; i++) {
            int rndKey = random.nextInt(SIZE);
            String rndString = UUID.randomUUID().toString().substring(0, 10);
//            String insert = "insert into target.r(a, b, c) values ('" + rndString + "', '" + rndString + "', '" + i + "');\n";
            String insert = "insert into target.r(a, b, c) values ('" + rndKey + "', '" + rndString + "', '" + i + "');\n";
            if (i < toInsert) {
//                insert += "insert into target.s(a, b) values ('" + i + "', '" + i + "');\n";
                insert += "insert into target.s(a, b) values ('" + i + "', '" + i + "');\n";
            }
            QueryManager.executeInsertOrDelete(insert, ((DBMSDB) scenario.getTarget()).getAccessConfiguration());
        }
        System.out.println("Done!");
        System.out.println("R: " + SIZE + " Tuples");
        System.out.println("S: " + toInsert + " Tuples");
        return scenario;
    }
}
