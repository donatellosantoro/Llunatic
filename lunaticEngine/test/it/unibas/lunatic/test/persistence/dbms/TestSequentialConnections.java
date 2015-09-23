package it.unibas.lunatic.test.persistence.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.dbms.DBMSDB;

public class TestSequentialConnections extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestSequentialConnections.class);
    private DBMSDB database;

    public void testScenarioClassic() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms);
        this.database = (DBMSDB) scenario.getTarget();
    }

    public void testScenarioProxy() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms);
        this.database = (DBMSDB) scenario.getTarget();
    }

    public void tearDown() {
        UtilityTest.deleteDB(database.getAccessConfiguration());
    }
}
