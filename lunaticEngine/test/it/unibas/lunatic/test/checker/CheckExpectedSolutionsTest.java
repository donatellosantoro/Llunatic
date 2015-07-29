package it.unibas.lunatic.test.checker;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import it.unibas.lunatic.test.comparator.instances.CompareInstances;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckExpectedSolutionsTest extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(CheckExpectedSolutionsTest.class);

    protected CompareInstances instancesComparator = new CompareInstances();

    protected List<PrecisionAndRecall> compareWithSingleExpectedInstance(List<String> generatedInstances, String expectedInstanceFile, List<String> exclude, double precisionForVariable, boolean materializeFKJoins) {
        List<PrecisionAndRecall> precisionAndRecall = instancesComparator.calculatePrecisionAndRecallValue(expectedInstanceFile, generatedInstances, exclude, precisionForVariable);
        Collections.sort(precisionAndRecall);
        return precisionAndRecall;
    }

    protected List<PrecisionAndRecall> compareWithSingleExpectedInstance(String generatedInstances, String expectedInstanceFile, List<String> exclude, double precisionForVariable, boolean materializeFKJoins) {
        List<PrecisionAndRecall> precisionAndRecall = instancesComparator.calculatePrecisionAndRecallValue(expectedInstanceFile, generatedInstances, exclude, precisionForVariable);
        Collections.sort(precisionAndRecall);
        return precisionAndRecall;
    }

    protected Map<String, List<PrecisionAndRecall>> compareWithExpectedInstances(DeltaChaseStep result, String expectedSubfolder, List<String> exclude, double precisionForVariable, boolean materializeFKJoins) {
        List<String> expectedInstances = readExpected(result.getScenario().getAbsolutePath(), expectedSubfolder);
        List<String> generatedInstances = exportResults(result, materializeFKJoins);
        Map<String, List<PrecisionAndRecall>> precisionAndRecallMap = instancesComparator.calculatePrecisionAndRecallValue(expectedInstances, generatedInstances, exclude, precisionForVariable);
        for (String expectedInstance : expectedInstances) {
            List<PrecisionAndRecall> precisionAndRecall = precisionAndRecallMap.get(expectedInstance);
            Collections.sort(precisionAndRecall);
        }
        return precisionAndRecallMap;
    }

    protected String printPrecisionAndRecall(Map<String, List<PrecisionAndRecall>> precisionAndRecallMap) {
        StringBuilder sb = new StringBuilder();
        for (String expectedInstance : precisionAndRecallMap.keySet()) {
            List<PrecisionAndRecall> precisionAndRecall = precisionAndRecallMap.get(expectedInstance);
            sb.append("############################").append("\n");
            sb.append("  Expected ").append(expectedInstance).append("\n");
            sb.append("############################").append("\n");
            sb.append(printPrecisionAndRecall(precisionAndRecall));
        }
        return sb.toString();
    }

    protected String printPrecisionAndRecall(List<PrecisionAndRecall> precisionAndRecall) {
        StringBuilder sb = new StringBuilder();
        sb.append("MAX").append("\n");
        sb.append("   With ").append(computeMax(precisionAndRecall).getGeneratedInstance()).append("\n");
        sb.append("   Pr: ").append(computeMax(precisionAndRecall).getPrecision()).append("\n");
        sb.append("   Re: ").append(computeMax(precisionAndRecall).getRecall()).append("\n");
        sb.append("   FM: ").append(computeMax(precisionAndRecall).getfMeasure()).append("\n");
        sb.append("MIN").append("\n");
        sb.append("   With ").append(computeMin(precisionAndRecall).getGeneratedInstance()).append("\n");
        sb.append("   Pr: ").append(computeMin(precisionAndRecall).getPrecision()).append("\n");
        sb.append("   Re: ").append(computeMin(precisionAndRecall).getRecall()).append("\n");
        sb.append("   FM: ").append(computeMin(precisionAndRecall).getfMeasure()).append("\n");
        sb.append("MEAN").append("\n");
        sb.append("   Pr: ").append(computeMean(precisionAndRecall).getPrecision()).append("\n");
        sb.append("   Re: ").append(computeMean(precisionAndRecall).getRecall()).append("\n");
        sb.append("   FM: ").append(computeMean(precisionAndRecall).getfMeasure()).append("\n");
        return sb.toString();
    }

    public List<String> exportResults(DeltaChaseStep result, boolean materializeFKJoins) {
        String dateString = new SimpleDateFormat("-yyyy-MM-dd_HHmmss").format(new Date());
        String tmpFilePath = org.apache.commons.io.FileUtils.getUserDirectory() + File.separator + "Temp" + File.separator + "lunatic_tmp" + File.separator + getClass().getSimpleName() + dateString;
        File tmpFile = new File(tmpFilePath);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        ExportChaseStepResultsCSV resultExporter = OperatorFactory.getInstance().getResultExporter(result.getScenario());
        List<String> resultFiles = resultExporter.exportResult(result, tmpFilePath, materializeFKJoins);
        if (logger.isTraceEnabled()) logger.debug("Exported solutions:\n" + LunaticUtility.printCollection(resultFiles));
        return resultFiles;
    }

    private List<String> readExpected(String scenarioPath, String subfolder) {
        File fileScenario = new File(scenarioPath);
        File expectedFolder = new File(fileScenario.getParent() + File.separator + subfolder);
        List<String> expectedFiles = new ArrayList<String>();
        for (String fileName : expectedFolder.list()) {
            if (fileName.toLowerCase().endsWith(".csv")) {
                expectedFiles.add(expectedFolder.getAbsolutePath() + File.separator + fileName);
            }
        }
        return expectedFiles;
    }

    protected void checkQuality(Map<String, List<PrecisionAndRecall>> quality) {
        for (String expected : quality.keySet()) {
            List<PrecisionAndRecall> qualityForExpected = quality.get(expected);
            if (logger.isDebugEnabled()) logger.debug("Comparing expected: "+ expected + " with: "+ qualityForExpected.get(0).getGeneratedInstance());
            Assert.assertEquals("Instance " + expected + " was not generated!", 1.0, qualityForExpected.get(0).getfMeasure());
        }
    }

    protected void checkQuality(List<PrecisionAndRecall> quality) {
        Assert.assertEquals("Expected instance was not generated!", 1.0, quality.get(0).getfMeasure());
    }

    protected void checkExpectedSolutions(String folderName, DeltaChaseStep result) {
        if (logger.isDebugEnabled()) logger.debug("Checking expected solutions...");
        Map<String, List<PrecisionAndRecall>> quality = compareWithExpectedInstances(result, folderName, Arrays.asList(new String[]{LunaticConstants.OID, LunaticConstants.TID}), 0.0, false);
        if (logger.isTraceEnabled()) logger.debug(printPrecisionAndRecall(quality));
        checkQuality(quality);
    }

    protected void exportResults(String folderName, DeltaChaseStep result) {
        OperatorFactory.getInstance().getResultExporter(result.getScenario()).exportResult(result, folderName, false);
    }
}
