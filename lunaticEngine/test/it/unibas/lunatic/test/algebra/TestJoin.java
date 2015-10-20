package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Join;
import speedy.model.algebra.Scan;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

public class TestJoin extends TestCase{
    
    private static Logger logger = LoggerFactory.getLogger(TestJoin.class);

    public void testJoin() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias bookSet = new TableAlias("IBLBookSet", true);
        TableAlias publisherSet = new TableAlias("IBLPublisherSet", true);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(bookSet, "pubId"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(publisherSet, "id"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(bookSet));
        join.addChild(new Scan(publisherSet));
        Iterator<Tuple> result = join.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
    }
    
    public void testJoinRS() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testRS);
        TableAlias r = new TableAlias("R");
        TableAlias s = new TableAlias("S");
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(r, "c"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(s, "a"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(r));
        join.addChild(new Scan(s));
        if (logger.isDebugEnabled()) logger.debug("Executing " + join);
        Iterator<Tuple> result = join.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
    }
    
    public void testMultipleJoinRS() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testRS);
        TableAlias r = new TableAlias("R");
        TableAlias s = new TableAlias("S");
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(r, "a"));
        leftAttributes.add(new AttributeRef(r, "b"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(s, "a"));
        rightAttributes.add(new AttributeRef(s, "b"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(r));
        join.addChild(new Scan(s));
        Iterator<Tuple> result = join.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 3\n"));
    }
}
