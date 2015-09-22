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
import speedy.model.algebra.Scan;
import speedy.model.algebra.Union;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

public class TestUnion extends TestCase{
    
    private static Logger logger = LoggerFactory.getLogger(TestUnion.class);

    public void testMultipleAttributeUnion() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Union union = new Union();
        union.addChild(new Scan(new TableAlias("R1", true)));
        union.addChild(new Scan(new TableAlias("R2", true)));
        Iterator<Tuple> result = union.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 6\n"));
    }
    
    public void testSingleAttributeUnion() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Union union = new Union();
        union.addChild(new Scan(new TableAlias("R3", true)));
        union.addChild(new Scan(new TableAlias("R4", true)));
        Iterator<Tuple> result = union.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 7\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 10\n"));
    }
    
    public void testFullUnion() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Union union = new Union();
        union.addChild(new Scan(new TableAlias("R3", true)));
        union.addChild(new Scan(new TableAlias("R5", true)));
        Iterator<Tuple> result = union.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 7\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 8\n"));
    }
    
    public void testSelfUnion() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testR);
        Union union = new Union();
        union.addChild(new Scan(new TableAlias("R3", true)));
        union.addChild(new Scan(new TableAlias("R3", true)));
        Iterator<Tuple> result = union.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 6\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 7\n"));
    }
}
