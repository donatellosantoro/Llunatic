package it.unibas.lunatic.test.de.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSQLVerticalPartition extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TSQLVerticalPartition.class);


//    public void testScenario10k() throws Exception {
//        Scenario scenario = UtilityTest.loadScenario(References.verticalPartition_10k_dbms);
//        long start = new Date().getTime();
//        DBMSDB result = (DBMSDB) DEChaserFactory.getChaser(scenario).doChase(scenario);
//        long end = new Date().getTime();
//        if (logger.isInfoEnabled()) logger.info("VerticalPartition 10k");
//        logger.info("Execution time: " + (end - start) / 1000.0 + " sec");//141 sec
//        if (logger.isDebugEnabled()) logger.debug(result.toString());
//        //checkExpectedInstances(result, scenario); // 177 minutes 51 seconds)
//    }
    public void testScenario100k() throws Exception { //64% redundancy
        Scenario scenario = UtilityTest.loadScenario(References.verticalPartition_100k_dbms);
        long start = new Date().getTime();
        DBMSDB result = (DBMSDB) DEChaserFactory.getChaser(scenario).doChase(scenario);
        long end = new Date().getTime();
        if (logger.isInfoEnabled()) logger.info("VerticalPartition 100k");
        logger.info("Execution time: " + (end - start) / 1000.0 + " sec"); //13749 sec
        if (logger.isDebugEnabled()) logger.debug(result.toString());
    }
}
