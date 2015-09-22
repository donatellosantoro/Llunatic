package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.util.Iterator;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Difference;
import speedy.model.algebra.Scan;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

public class TestDifference extends TestCase{
    
    private static Logger logger = LoggerFactory.getLogger(TestDifference.class);

    public void testMultipleAttributeDifference() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Difference difference = new Difference();
        difference.addChild(new Scan(new TableAlias("R1", true)));
        difference.addChild(new Scan(new TableAlias("R2", true)));
        Iterator<Tuple> result = difference.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 1\n"));
    }
    
    public void testSingleAttributeDifference() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Difference difference = new Difference();
        difference.addChild(new Scan(new TableAlias("R3", true)));
        difference.addChild(new Scan(new TableAlias("R4", true)));
        Iterator<Tuple> result = difference.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
    }
    
    public void testFullDifference() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Difference difference = new Difference();
        difference.addChild(new Scan(new TableAlias("R3", true)));
        difference.addChild(new Scan(new TableAlias("R5", true)));
        Iterator<Tuple> result = difference.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 6\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 7\n"));
    }
    
    public void testEmptyDifference() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Difference difference = new Difference();
        difference.addChild(new Scan(new TableAlias("R3", true)));
        difference.addChild(new Scan(new TableAlias("R3", true)));
        Iterator<Tuple> result = difference.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 0\n"));
    }
}
