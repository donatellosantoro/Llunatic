package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseExtTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLInsertFromSelectNaive;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import java.util.Date;
import java.util.UUID;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TChaseExtTGDSingleTuple extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TChaseExtTGDSingleTuple.class);

    private ChaseExtTGDs tgdChaser;
    private int SIZE = 100000;
    private int ALREADY_INSERT = 90; //%

    @Override
    protected void setUp() throws Exception {
        tgdChaser = new ChaseExtTGDs(new SQLInsertFromSelectNaive());
    }

    public void test() throws Exception {
        Scenario scenario = prepareScenario();
        long start = new Date().getTime();
        tgdChaser.doChase(scenario,ImmutableChaseState.getInstance());
        long end = new Date().getTime();
        System.out.println("Execution time (" + SIZE + "): " + (end - start) + " ms");
        Assert.assertEquals(SIZE, scenario.getTarget().getTable("s1").getSize());
        Assert.assertEquals(SIZE, scenario.getTarget().getTable("s2").getSize());
    }

    public void testLimit1() throws Exception {
        Scenario scenario = prepareScenario();
        scenario.getConfiguration().setUseLimit1(true);
        long start = new Date().getTime();
        tgdChaser.doChase(scenario, ImmutableChaseState.getInstance());
        long end = new Date().getTime();
        System.out.println("Execution time LIMIT1 (" + SIZE + "): " + (end - start) + " ms");
        Assert.assertEquals(SIZE, scenario.getTarget().getTable("s1").getSize());
        Assert.assertEquals(SIZE, scenario.getTarget().getTable("s2").getSize());
    }
    
    private Scenario prepareScenario() {
        Scenario scenario = UtilityTest.loadScenario(References.RS_tgd, true);
        System.out.print("Generating target... ");
        int toInsert = (SIZE * ALREADY_INSERT) / 100;
        for (int i = 0; i < SIZE; i++) {
            String rndString = UUID.randomUUID().toString().substring(0, 10);
            String insert = "insert into target.r1(a,b, c) values ('" + rndString + "', '" + rndString + "', '" + i + "');\n";
            insert += "insert into target.r2(a,b) values ('" + rndString + "', '" + rndString + "');\n";
            if (i < toInsert) {
                insert += "insert into target.s1(a,b) values ('" + i + "', '" + i + "');\n";
                insert += "insert into target.s2(a,b) values ('" + i + "', '" + i + "');\n";
            }
            QueryManager.executeInsertOrDelete(insert, ((DBMSDB) scenario.getTarget()).getAccessConfiguration());
        }
        System.out.println("Done!");
        System.out.println("R1: " + SIZE + " Tuples");
        System.out.println("R2: " + SIZE + " Tuples");
        System.out.println("S1: " + toInsert + " Tuples");
        System.out.println("S2: " + toInsert + " Tuples");
//        Assert.assertEquals(SIZE, scenario.getTarget().getTable("r1").getSize());
//        Assert.assertEquals(SIZE, scenario.getTarget().getTable("r2").getSize());
//        Assert.assertEquals(toInsert, scenario.getTarget().getTable("s1").getSize());
//        Assert.assertEquals(toInsert, scenario.getTarget().getTable("s2").getSize());
        return scenario;
    }
}
