package it.unibas.lunatic.test.persistence.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoadScenario extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestLoadScenario.class);

    public void testLoadDBMS() {
        try {
            Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms);
//            DBMSUtility.emptyTables(((DBMSDB)scenario.getTarget()).getAccessConfiguration());
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(4, scenario.getSource().getTableNames().size());
            Assert.assertEquals(3, UtilityTest.getSize(scenario.getSource().getTable("ibdbookset")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("locset")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("iblbookset")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("iblpublisherset")));
            Assert.assertEquals("title", scenario.getSource().getTable("ibdbookset").getAttributes().get(1).getName());
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals("title", scenario.getTarget().getTable("bookset").getAttributes().get(1).getName());
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }
}
