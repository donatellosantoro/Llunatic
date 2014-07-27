package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLRunQuery;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.test.GenerateModifiedCells;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.io.File;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSQLHospitalQuality extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TSQLHospitalQuality.class);

    private GenerateModifiedCells modifiedCellsGenerator = new GenerateModifiedCells(new SQLRunQuery());

    public void testScenario() throws Exception {
        Scenario scenario = new DAOMCScenario().loadScenario(UtilityTest.getExternalFolder(References.hospital_fr10_5k1p_quality));
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
//        List<PrecisionAndRecall> quality = compareWithSingleExpectedInstance(result, "/Users/donatello/Projects/lunatic/lunaticEngine/misc/experiments/VLDB2013/hospital/data_scenario_quality/5k/groundDB_mt_expected.csv", Arrays.asList(new String[]{LunaticConstants.OID, LunaticConstants.TID}), 0.5);
//        if (logger.isDebugEnabled()) logger.debug(printPrecisionAndRecall(quality));

        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        logger.info("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        logger.info("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(4, resultSizer.getPotentialSolutions(result));
//        Assert.assertEquals(6, resultSizer.getDuplicates(result));

        File tmpFile = File.createTempFile("lunatic", ".txt");
//        tmpFile.deleteOnExit();
        String outputFile = tmpFile.getAbsolutePath();
        System.out.println("### Output file " + outputFile);
        modifiedCellsGenerator.generate(result, outputFile);
        String fileExpectedRepairs = "/experiments/VLDB2013/hospital/data_scenario_quality/5k/resultRepair_10.csv";
        String absoluteFileExpectedRepairs = new File(UtilityTest.getExternalFolder(fileExpectedRepairs)).getAbsolutePath();
        List<PrecisionAndRecall> precisionAndRecall = comparator.calculatePrecisionAndRecallValue(outputFile, absoluteFileExpectedRepairs, 0.5, true);
        Collections.sort(precisionAndRecall);
//        
        System.out.println("Max Pr: " + computeMax(precisionAndRecall).getPrecision());
        System.out.println("Max R: " + computeMax(precisionAndRecall).getRecall());
        System.out.println("Max F: " + computeMax(precisionAndRecall).getfMeasure());

        System.out.println("Min Pr: " + computeMin(precisionAndRecall).getPrecision());
        System.out.println("Min R: " + computeMin(precisionAndRecall).getRecall());
        System.out.println("Min F: " + computeMin(precisionAndRecall).getfMeasure());

        System.out.println("Mean Pr: " + computeMean(precisionAndRecall).getPrecision());
        System.out.println("Mean R: " + computeMean(precisionAndRecall).getRecall());
        System.out.println("Mean F: " + computeMean(precisionAndRecall).getfMeasure());
//

        float delta = 0.0001f;
        Assert.assertEquals(0.5278, computeMax(precisionAndRecall).getPrecision(), delta);
        Assert.assertEquals(0.5506, computeMax(precisionAndRecall).getRecall(), delta);
        Assert.assertEquals(0.5390, computeMax(precisionAndRecall).getfMeasure(), delta);

//        Assert.assertEquals(0.4609, computeMin(precisionAndRecall).getPrecision(), delta);
//        Assert.assertEquals(0.5290, computeMin(precisionAndRecall).getRecall(), delta);
//        Assert.assertEquals(0.4926, computeMin(precisionAndRecall).getfMeasure(), delta);

//        Assert.assertEquals(0.4852, computeMean(precisionAndRecall).getPrecision(), delta);
//        Assert.assertEquals(0.5369, computeMean(precisionAndRecall).getRecall(), delta);
//        Assert.assertEquals(0.5097, computeMean(precisionAndRecall).getfMeasure(), delta);
    }
}
