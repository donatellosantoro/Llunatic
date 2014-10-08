package it.unibas.lunatic.test.checker;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeSize;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.persistence.relational.QueryStatManager;
import it.unibas.lunatic.test.GenerateModifiedCells;
import it.unibas.lunatic.test.comparator.repairs.RepairsComparator;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;

public class CheckTest extends TestCase {

    protected ChaseTreeSize resultSizer = new ChaseTreeSize();
    protected RepairsComparator comparator = new RepairsComparator();
    protected ChaseStats chaseStats = ChaseStats.getInstance();
    protected QueryStatManager queryStats = QueryStatManager.getInstance();

    protected GenerateModifiedCells getModifiedCellGenerator(Scenario scenario) {
        return new GenerateModifiedCells(OperatorFactory.getInstance().getQueryRunner(scenario));
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        IntegerOIDGenerator.resetCounter();
        IntegerOIDGenerator.clearCache();
        CellGroupIDGenerator.resetCounter();
        OperatorFactory.getInstance().reset();
        chaseStats.resetStatistics();
        queryStats.resetStatistics();
    }

    protected void setConfigurationForTest(Scenario scenario) {
        scenario.getConfiguration().setCheckSolutions(true);
        scenario.getConfiguration().setCheckSolutionsQuery(true);
        if (scenario.isMainMemory()) scenario.getConfiguration().setDebugMode(true);
    }

    protected void setCheckEGDsAfterEachStep(Scenario scenario) {
        scenario.getConfiguration().setCheckAllNodesForEGDSatisfaction(true);
    }

    protected void checkSolutions(DeltaChaseStep result) {
        Assert.assertTrue("No solution...", resultSizer.getSolutions(result) > 0);
        Assert.assertTrue("No solution...", resultSizer.getAllNodes(result) > 0);
        Assert.assertEquals("Expected solutions", resultSizer.getPotentialSolutions(result), resultSizer.getSolutions(result));
    }

    protected String getTestName(String scenarioName, Scenario scenario) {
        StringBuilder name = new StringBuilder(scenarioName.toUpperCase());
        if (scenario.isDEScenario()) {
            return name.toString() + "-DE";
        }
        if (scenario.getPartialOrder() instanceof FrequencyPartialOrder) {
            name.append("-FR");
        }
        ICostManager costManager = scenario.getCostManager();
        if (costManager.getDependencyLimit() == 1) {
            name.append("-SP");
        } else {
//            name.append("-").append(costManager.getChaseTreeSizeThreshold());
            name.append("-").append(costManager.getPotentialSolutionsThreshold());
        }
        if (!costManager.isDoBackward()) {
            name.append("-FO");
        }
        return name.toString();
    }

    protected PrecisionAndRecall computeMin(List<PrecisionAndRecall> listPrecisionAndRecall) {
        return listPrecisionAndRecall.get(listPrecisionAndRecall.size() - 1);
    }

    protected PrecisionAndRecall computeMax(List<PrecisionAndRecall> listPrecisionAndRecall) {
        return listPrecisionAndRecall.get(0);
    }

    protected PrecisionAndRecall computeMean(List<PrecisionAndRecall> listPrecisionAndRecall) {
        double sumP = 0.0;
        double sumR = 0.0;
        for (PrecisionAndRecall precisionAndRecall : listPrecisionAndRecall) {
            sumP += precisionAndRecall.getPrecision();
            sumR += precisionAndRecall.getRecall();
        }
        double precision = sumP / ((double) listPrecisionAndRecall.size());
        double recall = sumR / ((double) listPrecisionAndRecall.size());
        double fMeasure = (2 * precision * recall) / (precision + recall);
        return new PrecisionAndRecall(precision, recall, fMeasure);
    }

    @SuppressWarnings("unchecked")
    protected void checkExpectedInstances(IDatabase result, Scenario scenario) throws Exception {
        String expectedResultFile = generateExpectedFileName(scenario);
        DataSourceTxtInstanceChecker checker = new DataSourceTxtInstanceChecker();
        checker.checkInstance(result, expectedResultFile);
    }

    private String generateExpectedFileName(Scenario scenario) {
        String fileName = scenario.getFileName().substring(0, scenario.getFileName().lastIndexOf("."));
        return fileName + "-expectedSolution.txt";
    }

    protected String generateOutputFileName(Scenario scenario) {
        String fileName = scenario.getFileName().substring(0, scenario.getFileName().lastIndexOf("."));
        fileName = fileName.replace("build/classes", "misc");
        return fileName + ".out";
    }

    protected String generateOutputFileName(Scenario scenario, String suffix) {
        String fileName = scenario.getFileName().substring(0, scenario.getFileName().lastIndexOf("."));
        fileName = fileName.replace("build/classes", "misc");
        return fileName + "." + suffix + ".out";
    }

    public void testDummy() {
    }
}
