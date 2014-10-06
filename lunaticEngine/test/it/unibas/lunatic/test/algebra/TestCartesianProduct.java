package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.CartesianProduct;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.util.Iterator;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCartesianProduct extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestCartesianProduct.class);

    public void testCartesianProduct() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias bookSet = new TableAlias("IBLBookSet", true);
        TableAlias publisherSet = new TableAlias("IBLPublisherSet", true);
        CartesianProduct cartesianProduct = new CartesianProduct();
        cartesianProduct.addChild(new Scan(bookSet));
        cartesianProduct.addChild(new Scan(publisherSet));
        Iterator<Tuple> result = cartesianProduct.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
    }

    public void testCartesianProductRS() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testRS);
        TableAlias r = new TableAlias("R");
        TableAlias s = new TableAlias("S");
        CartesianProduct cartesianProduct = new CartesianProduct();
        cartesianProduct.addChild(new Scan(r));
        cartesianProduct.addChild(new Scan(s));
        if (logger.isDebugEnabled()) logger.debug("Executing " + cartesianProduct);
        Iterator<Tuple> result = cartesianProduct.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 20\n"));
    }

    public void testMultipleCartesianProduct() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias bookSet = new TableAlias("IBLBookSet", true);
        TableAlias publisherSet = new TableAlias("IBLPublisherSet", true);
        TableAlias locSet = new TableAlias("LOCSet", true);
        CartesianProduct cartesianProduct = new CartesianProduct();
        cartesianProduct.addChild(new Scan(bookSet));
        cartesianProduct.addChild(new Scan(publisherSet));
        cartesianProduct.addChild(new Scan(locSet));
        Iterator<Tuple> result = cartesianProduct.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 8\n"));
    }
}
