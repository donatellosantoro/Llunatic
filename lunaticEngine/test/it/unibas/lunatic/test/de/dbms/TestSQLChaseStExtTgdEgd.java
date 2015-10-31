package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLChaseStExtTgdEgd extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLChaseStExtTgdEgd.class);

    public void test() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.RS_st_exttgd_egd_dbms);
        IDatabase result = DEChaserFactory.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toString());
//        checkExpectedInstances(result, scenario);
    }
    
}
