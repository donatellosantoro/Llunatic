package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCache extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TCache.class);

    private String STRATEGY_TO_TEST = LunaticConstants.GREEDY_EHCACHE;

    public void testSyntetic() throws Exception {
        testScenario(References.synthetic_01_dbms);
        testScenario(References.synthetic_02_dbms);
        testScenario(References.synthetic_03_dbms);
        testScenario(References.synthetic_04_dbms);
    }

    public void testSynteticTGD() throws Exception {
        testScenario(References.synthetic_T01_dbms);
        testScenario(References.synthetic_T02_dbms);
        testScenario(References.synthetic_T04_dbms);
        testScenario(References.synthetic_T05_dbms);
    }

    public void testCustomers() throws Exception {
        testScenario(References.customers_cfd_dbms);
    }
//    public void testDoctors() throws Exception {
//        testScenario(References.doctors_dbms);
//    }
//
//    public void testPersons() throws Exception {
//        testScenario(References.persons_dbms);
//    }
//
//    public void testTreatments() throws Exception {
//        testScenario(References.treatments_dbms);
//    }
//
//    public void testHospital() throws Exception {
//        testScenario(References.hospital_0_2p_dbms);
//    }

    private void testScenario(String scenarioPath) throws Exception {
        Scenario scenarioNoCache = UtilityTest.loadScenarioFromResources(scenarioPath, true);
        scenarioNoCache.getConfiguration().setUseCache(LunaticConstants.NO_CACHE);
        Results resultNoCache = executeTest(scenarioNoCache);

        Scenario scenarioWithCache = UtilityTest.loadScenarioFromResources(scenarioPath, true);
        scenarioWithCache.getConfiguration().setUseCache(STRATEGY_TO_TEST);
        Results resultWithCache = executeTest(scenarioWithCache);

        Assert.assertEquals("Solutions", resultSizer.getPotentialSolutions(resultNoCache.getResult()), resultSizer.getPotentialSolutions(resultWithCache.getResult()));
        Assert.assertEquals("Solutions", resultSizer.getDuplicates(resultNoCache.getResult()), resultSizer.getDuplicates(resultWithCache.getResult()));

        Scenario scenarioNoCacheExpectedRepair = UtilityTest.loadScenarioFromResources(scenarioPath, true);
        scenarioNoCacheExpectedRepair.getCostManager().setDoBackward(false); //NoCache is the expected solution, and so must be a single repair
        scenarioNoCacheExpectedRepair.getCostManager().setDoPermutations(false);
        scenarioNoCacheExpectedRepair.getConfiguration().setUseCache(LunaticConstants.NO_CACHE);
        Results resultNoCacheExpectedRepair = executeTest(scenarioNoCacheExpectedRepair);

        List<PrecisionAndRecall> precisionAndRecall = comparator.calculatePrecisionAndRecallValue(resultWithCache.getGeneratedRepairsFile(), resultNoCacheExpectedRepair.getGeneratedRepairsFile(), 0.5, true);
        Collections.sort(precisionAndRecall);
        float delta = 0.0f;
        Assert.assertEquals(1.0, computeMax(precisionAndRecall).getPrecision(), delta);
        Assert.assertEquals(1.0, computeMax(precisionAndRecall).getRecall(), delta);
        Assert.assertEquals(1.0, computeMax(precisionAndRecall).getfMeasure(), delta);
    }

    private Results executeTest(Scenario scenario) throws IOException {
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        File tmpFile = File.createTempFile("lunatic", ".txt");
        tmpFile.deleteOnExit();
        String outputFile = tmpFile.getAbsolutePath();
        getModifiedCellGenerator(scenario).generate(result, outputFile);
        return new Results(result, outputFile);
    }
}

class Results {

    private DeltaChaseStep result;
    private String generatedRepairsFile;

    public Results(DeltaChaseStep result, String generatedRepairsFile) {
        this.result = result;
        this.generatedRepairsFile = generatedRepairsFile;
    }

    public DeltaChaseStep getResult() {
        return result;
    }

    public String getGeneratedRepairsFile() {
        return generatedRepairsFile;
    }
}