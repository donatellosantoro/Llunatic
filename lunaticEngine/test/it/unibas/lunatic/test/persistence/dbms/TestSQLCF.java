package it.unibas.lunatic.test.persistence.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForCertainAnswerQuery;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.IDatabase;

public class TestSQLCF extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLCF.class);
    private BuildAlgebraTreeForCertainAnswerQuery treeBuilder = new BuildAlgebraTreeForCertainAnswerQuery();

    public void test() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/chasebench/doctors/doctors-de-scenario-cf.xml", true);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        assertEquals(5, scenario.getSTTgds().size());
        assertEquals(0, scenario.getExtTGDs().size());
        assertEquals(10, scenario.getEGDs().size());
        assertEquals(21, scenario.getQueries().size());
        Dependency q9 = scenario.getQueries().get(9);
        if (logger.isDebugEnabled()) logger.debug(q9.toLogicalString());
        IAlgebraOperator operator = treeBuilder.generateOperator(q9, scenario);
        if (logger.isDebugEnabled()) logger.debug(operator.toString());
        if (logger.isDebugEnabled()) logger.debug(new AlgebraTreeToSQL().treeToSQL(operator, scenario.getSource(), scenario.getTarget(), ""));
//        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
//        if (logger.isDebugEnabled()) logger.debug(result.toString());
    }

//    public void test() throws Exception {
//        Scenario scenario = UtilityTest.loadScenarioFromResources("/de/chasebench/doctors/doctors-de-scenario-cf.xml", true);
//        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
//        assertEquals(5, scenario.getSTTgds().size());
//        assertEquals(0, scenario.getExtTGDs().size());
//        assertEquals(10, scenario.getEGDs().size());
////        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
////        if (logger.isDebugEnabled()) logger.debug(result.toString());
////        checkExpectedInstances(result, scenario);
//    }
}
