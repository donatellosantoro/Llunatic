package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSQLTreatmentsQuality extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TSQLTreatmentsQuality.class);

    public void testScenario() throws Exception {
        Scenario scenario = new DAOMCScenario().loadScenario(UtilityTest.getExternalFolder(References.treatments_quality));
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + getTestName("treatments", scenario));
        if (logger.isDebugEnabled()) logger.debug("Result: " + result.toLongString());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        logger.info("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        logger.info("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
//        Assert.assertEquals(13, resultSizer.getDuplicates(result));

        File tmpFile = File.createTempFile("lunatic", ".txt");
        tmpFile.deleteOnExit();
        String outputFile = tmpFile.getAbsolutePath();
        getModifiedCellGenerator(scenario).generate(result, outputFile);
        String fileExpectedRepairs = "/experiments/VLDB2013/treatments/data/5k/resultRepair_10.csv";
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
//        Assert.assertEquals(0.5859, computeMax(precisionAndRecall).getPrecision(), delta);
//        Assert.assertEquals(0.6410, computeMax(precisionAndRecall).getRecall(), delta);
//        Assert.assertEquals(0.6122, computeMax(precisionAndRecall).getfMeasure(), delta);
//
//        Assert.assertEquals(0.5655, computeMin(precisionAndRecall).getPrecision(), delta);
//        Assert.assertEquals(0.5897, computeMin(precisionAndRecall).getRecall(), delta);
//        Assert.assertEquals(0.5774, computeMin(precisionAndRecall).getfMeasure(), delta);
//
//        Assert.assertEquals(0.5684, computeMean(precisionAndRecall).getPrecision(), delta);
//        Assert.assertEquals(0.5970, computeMean(precisionAndRecall).getRecall(), delta);
//        Assert.assertEquals(0.5824, computeMean(precisionAndRecall).getfMeasure(), delta);
    }
}
