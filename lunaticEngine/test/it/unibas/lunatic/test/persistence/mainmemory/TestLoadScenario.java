package it.unibas.lunatic.test.persistence.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.io.File;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoadScenario extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestLoadScenario.class);
    private DAOMCScenario daoScenario;

    public void testLoadXML() {
        try {
            String fileScenario = new File(this.getClass().getResource(References.bookPublisher).toURI()).getAbsolutePath();
            daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(4, scenario.getSource().getTableNames().size());
            Assert.assertEquals(3, UtilityTest.getSize(scenario.getSource().getTable("IBDBookSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("LOCSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("IBLBookSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("IBLPublisherSet")));
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals(2, scenario.getTarget().getTableNames().size());
            Assert.assertEquals(0, UtilityTest.getSize(scenario.getTarget().getTable("BookSet")));
            Assert.assertEquals(0, UtilityTest.getSize(scenario.getTarget().getTable("PublisherSet")));
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    public void testLoadPlain() {
        try {
            String fileScenario = new File(this.getClass().getResource(References.bookPublisher_plain).toURI()).getAbsolutePath();
            daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(4, scenario.getSource().getTableNames().size());
            Assert.assertTrue(scenario.getSource().getTableNames().contains("IBDBookSet"));
            Assert.assertTrue(scenario.getSource().getTableNames().contains("IBLBookSet"));
            Assert.assertTrue(scenario.getSource().getTableNames().contains("IBLPublisherSet"));
            Assert.assertTrue(scenario.getSource().getTableNames().contains("LOCSet"));
            Assert.assertEquals(3, UtilityTest.getSize(scenario.getSource().getTable("IBDBookSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("LOCSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("IBLBookSet")));
            Assert.assertEquals(2, UtilityTest.getSize(scenario.getSource().getTable("IBLPublisherSet")));
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals(2, scenario.getTarget().getTableNames().size());
            Assert.assertEquals(0, UtilityTest.getSize(scenario.getTarget().getTable("BookSet")));
            Assert.assertEquals(0, UtilityTest.getSize(scenario.getTarget().getTable("PublisherSet")));
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    public void testLoadOnlyTarget() {
        try {
            String fileScenario = new File(this.getClass().getResource(References.expenseDB).toURI()).getAbsolutePath();
            daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(0, scenario.getSource().getTableNames().size());
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals(3, scenario.getTarget().getTableNames().size());
            Assert.assertEquals(3, UtilityTest.getSize(scenario.getTarget().getTable("companies")));
            Assert.assertEquals(3, UtilityTest.getSize(scenario.getTarget().getTable("projects")));
            Assert.assertEquals(4, UtilityTest.getSize(scenario.getTarget().getTable("grants")));
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }
    
    

    public void testLoadFunction() {
        try {
            String fileScenario = new File(this.getClass().getResource(References.employees_rew).toURI()).getAbsolutePath();
            daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(1, scenario.getSource().getTableNames().size());
            Assert.assertTrue(scenario.getSource().getTableNames().contains("S_Employee"));
            Assert.assertEquals(8, UtilityTest.getSize(scenario.getSource().getTable("S_Employee")));
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals(1, scenario.getTarget().getTableNames().size());
            Assert.assertEquals(0, UtilityTest.getSize(scenario.getTarget().getTable("T_Employee")));
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }
    
    public void testLoadNegation() {
        try {
            String fileScenario = new File(this.getClass().getResource(References.companies_rew).toURI()).getAbsolutePath();
            daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            Assert.assertNotNull(scenario);
            Assert.assertNotNull(scenario.getSource());
            Assert.assertEquals(5, scenario.getSource().getTableNames().size());
            Assert.assertNotNull(scenario.getTarget());
            Assert.assertEquals(2, scenario.getTarget().getTableNames().size());
            Assert.assertNotNull(scenario.getPartialOrder());
            if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }
}
