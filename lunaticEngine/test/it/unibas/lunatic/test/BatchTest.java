package it.unibas.lunatic.test;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IValueOccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLRunQuery;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.persistence.relational.QueryStatManager;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BatchTest extends CheckExpectedSolutionsTest {

    protected static Logger logger = LoggerFactory.getLogger(BatchTest.class);
    protected GenerateModifiedCells modifiedCellsGenerator = new GenerateModifiedCells(new SQLRunQuery());
    private Map<String, Object> configuration = new HashMap<String, Object>();
    //
    private double valueM1 = 0.5;
    private double valueM2 = 0.75;
    private double valueM3 = 1.0;

    private void initConfiguration() {
        configuration.put("chaseScenario", true);
        configuration.put("computeQuality", false);
        configuration.put("computeQualityOfInstances", false);
        configuration.put("computeQualityOfRoot", false);
        configuration.put("deleteDB", false);
        configuration.put("importDataFromCSV", false);
        configuration.put("cleanWorkSchema", true);
        configuration.put("baseDir", "");
        configuration.put("resultDir", System.getProperty("user.home") + "/Temp/lunatic_results/");
        configuration.put("tmpDir", System.getProperty("user.home") + "/Temp/lunatic_tmp/");
        configuration.put("experimentDir", "");
        configuration.put("scenarioName", "");
        configuration.put("experimentName", "");
        configuration.put("suffix", "");
        configuration.put("fileNameSuffix", "p-mcscenario-dbms");
        configuration.put("dataDir", "/data/");
//        configuration.put("sizes", new String[]{""});
        configuration.put("sizes", new String[]{"5k"});
//        configuration.put("perturbations", new int[]{});
        configuration.put("perturbations", new int[]{1, 2, 3, 4, 5});
//        configuration.put("perturbations", new int[]{2});
        configuration.put("attributesToExclude", Arrays.asList(new String[]{LunaticConstants.OID, LunaticConstants.TID}));
        configureScenario(configuration);
        //OVERRIDE CUSTOM VALUES
    }

    public Scenario getScenario(String fileScenario) throws Exception {
        this.tearDown();
        DAOMCScenario daoScenario = new DAOMCScenario();
        Scenario scenario = daoScenario.loadScenario(fileScenario);
//        System.out.println("Configuration: " + scenario.getConfiguration());
//        System.out.println("Cost manager: " + scenario.getCostManager());
//        System.out.println("User manager: " + scenario.getUserManager());
//        System.out.println("Partial order: " + scenario.getPartialOrder());
        return scenario;
    }

    private BatchTestResult executeScenario(Scenario scenario, String fileOutput) throws IOException {
        BatchTestResult result;
        preExecute(scenario);
        if (scenario.isMCScenario() || scenario.getConfiguration().isForceMCChaser()) {
            if (logger.isDebugEnabled()) logger.debug("Executing scenario with MC chaser");
            result = executeMCScenario(scenario, fileOutput);
        } else {
            if (logger.isDebugEnabled()) logger.debug("Executing scenario with DE chaser");
            result = executeDEScenario(scenario);
        }
        postExecute(result);
        QueryStatManager.getInstance().printStatistics();
        return result;
    }

    public BatchTestResult executeMCScenario(Scenario scenario, String fileOutput) throws IOException {
        ChaseMCScenario chaser = scenario.getCostManager().getChaser(scenario);
        long start = new Date().getTime();
        DeltaChaseStep result = chaser.doChase(scenario);
//        System.out.println(result.toShortString());
        long end = new Date().getTime();
        if (isComputeQualityOfRepairs()) {
            modifiedCellsGenerator.generate(result, fileOutput);
        }
        String testName = (String) configuration.get("experimentName");
        double executionTime = (end - start) / 1000.0;
        int solutions = resultSizer.getSolutions(result);
        int duplicateSolutions = resultSizer.getDuplicates(result);
        return new BatchTestResult(scenario, testName, executionTime, solutions, duplicateSolutions, result);
    }

    private BatchTestResult executeDEScenario(Scenario scenario) throws IOException {
        IDEChaser chaser = DEChaserFactory.getChaser(scenario);
//        IDEChaser chaser = DEChaserFactory.getClassicDEChaser(scenario);
        long start = new Date().getTime();
        IDatabase result = chaser.doChase(scenario);
        long end = new Date().getTime();
//        System.out.println(result.printInstances());
        if (isComputeQualityOfRepairs()) {
            throw new IllegalArgumentException("Compute quality is allowed only for MC scenario");
        }
        String testName = (String) configuration.get("experimentName");
        double executionTime = (end - start) / 1000.0;
        int solutions = 1;
        int duplicateSolutions = 0;
        return new BatchTestResult(scenario, testName, executionTime, solutions, duplicateSolutions, result);
    }

    public void testScenario() throws Exception {
        initConfiguration();
        String scenarioName = (String) configuration.get("scenarioName");
        //MEASURE 1
        StringBuilder resultPRMaxM1 = initPRResult("Value", valueM1, "MAX");
        StringBuilder resultPRMeanM1 = initPRResult("Value", valueM1, "AVG");
        StringBuilder resultPRMinM1 = initPRResult("Value", valueM1, "MIN");
        //MEASURE 2
        StringBuilder resultPRMaxM2 = initPRResult("Value", valueM2, "MAX");
        StringBuilder resultPRMeanM2 = initPRResult("Value", valueM2, "AVG");
        StringBuilder resultPRMinM2 = initPRResult("Value", valueM2, "MIN");
        //MEASURE 3
        StringBuilder resultPRMaxM3 = initPRResult("Value", valueM3, "MAX");
        StringBuilder resultPRMeanM3 = initPRResult("Value", valueM3, "AVG");
        StringBuilder resultPRMinM3 = initPRResult("Value", valueM3, "MIN");
        //CELLS
        StringBuilder resultPRMaxCell = initPRResult("Cell", 1, "MAX");
        StringBuilder resultPRMeanCell = initPRResult("Cell", 1, "AVG");
        StringBuilder resultPRMinCell = initPRResult("Cell", 1, "MIN");
        StringBuilder resultTime = new StringBuilder();
        StringBuilder resultSolutions = new StringBuilder();
        StringBuilder resultDuplicates = new StringBuilder();
        StringBuilder resultNodes = new StringBuilder();
        StringBuilder resultQualityRoot = new StringBuilder();
        StringBuilder resultRepairRate = new StringBuilder();
        String experimentName = (String) configuration.get("experimentName");
        String testName;
        for (String size : getSizes()) {
            if (size.equals("")) {
                continue;
            }
            for (int perturbation : getPerturbations()) {
                String fileName = scenarioName + "-" + size + "-" + perturbation;
                String experimentSubFolder = (experimentName.isEmpty()) ? "" : "/" + experimentName;
                String fileScenario = generateFileScenario(scenarioName, size, perturbation, experimentSubFolder + "/", ".xml");
                String fileOutput = configuration.get("resultDir") + "/" + configuration.get("experimentName") + configuration.get("suffix") + "/" + fileName + ".txt";
                String fileExpectedReparis = generateFileExpected(scenarioName, size, perturbation);
                System.out.println("Scenario: " + fileScenario);
                System.out.println("Output: " + fileOutput);
                System.out.println("ExpectedReparis: " + fileExpectedReparis);
                String instance = size + "-" + perturbation;
                if (isDeleteDB()) {
                    Scenario scenario = getScenario(fileScenario);
                    deleteDB(scenario);
                }
                if (isCleanWorkSchema()) {
                    Scenario scenario = getScenario(fileScenario);
                    cleanWorkSchema(scenario);
                }
                if (isImportDataFromCSV()) {
                    if (!isDeleteDB()) {
                        throw new IllegalArgumentException("Import data into a not-empty db is not allowed!");
                    }
                    Scenario scenario = getScenario(fileScenario);
                    importDataFromCSV(scenario, size, perturbation);
                }
                BatchTestResult result = null;
                if (isChaseScenario()) {
                    Scenario scenario = getScenario(fileScenario);
                    if (!scenario.isDBMS()) {
                        System.out.println(scenario.getCostManager().toString());
                    }
                    testName = getTestName(scenarioName, scenario);
                    if (!experimentName.isEmpty() && !testName.equals(experimentName)) {
                        System.out.println("############################################################################################");
                        System.out.println("############################################################################################");
                        System.out.println("  This configuration is for a " + testName + " test, instead of " + experimentName);
                        System.out.println("############################################################################################");
                        System.out.println("############################################################################################");
//                        throw new IllegalArgumentException("This configuration is for a " + testName + " test, instead of " + experimentName);
                    }
                    System.out.println("----------------------------------------------------------------");
                    System.out.println("--    " + scenarioName + " Size " + size + ((perturbation > 0) ? " Perturbation " + perturbation : "") + "     --");
                    System.out.println("----------------------------------------------------------------");
                    try {
                        result = executeScenario(scenario, fileOutput);
                        result.printResult();
                        resultSolutions.append(instance).append("\t").append(result.getSolutions()).append("\n");
                        resultDuplicates.append(instance).append("\t").append(result.getDuplicateSolutions()).append("\n");
                        resultNodes.append(instance).append("\t").append(result.getNumberOfNodes()).append("\n");
                        resultTime.append(instance).append("\t").append((int) result.getExecutionTime()).append("\n");
                    } catch (ChaseException ex) {
                        logger.error(ex.getLocalizedMessage());
                        resultSolutions.append(instance).append("\t").append("Chase Exception").append("\n");
                        resultDuplicates.append(instance).append("\t").append("Chase Exception").append("\n");
                        resultNodes.append(instance).append("\t").append("Chase Exception").append("\n");
                        resultTime.append(instance).append("\t").append("Chase Exception").append("\n");
                    }
                }
                if (isComputeQualityOfRepairs()) {
                    List<PrecisionAndRecall> listPrecisionAndRecallM1 = comparator.calculatePrecisionAndRecallValue(fileOutput, fileExpectedReparis, 0.5, true);
                    addPRResult(instance, listPrecisionAndRecallM1, resultPRMaxM1, resultPRMeanM1, resultPRMinM1);
                    List<PrecisionAndRecall> listPrecisionAndRecallM2 = comparator.calculatePrecisionAndRecallValue(fileOutput, fileExpectedReparis, 0.75, true);
                    addPRResult(instance, listPrecisionAndRecallM2, resultPRMaxM2, resultPRMeanM2, resultPRMinM2);
                    List<PrecisionAndRecall> listPrecisionAndRecallM3 = comparator.calculatePrecisionAndRecallValue(fileOutput, fileExpectedReparis, 1, true);
                    addPRResult(instance, listPrecisionAndRecallM3, resultPRMaxM3, resultPRMeanM3, resultPRMinM3);
                    List<PrecisionAndRecall> listPrecisionAndRecallCell = comparator.calculatePrecisionAndRecallCell(fileOutput, fileExpectedReparis, true);
                    addPRResult(instance, listPrecisionAndRecallCell, resultPRMaxCell, resultPRMeanCell, resultPRMinCell);
                }
                if (isComputeQualityOfInstances()) {
                    List<String> generatedInstances = exportResults(result.getChaseTreeRoot(), false);
                    List<PrecisionAndRecall> listPrecisionAndRecallM1 = compareWithSingleExpectedInstance(generatedInstances, fileExpectedReparis, getAttributesToExclude(), 0.5, false);
                    addPRResult(instance, listPrecisionAndRecallM1, resultPRMaxM1, resultPRMeanM1, resultPRMinM1);
//                    List<PrecisionAndRecall> listPrecisionAndRecallM2 = compareWithSingleExpectedInstance(generatedInstances, fileExpectedReparis, getAttributesToExclude(), 0.75, false);
//                    addPRResult(instance, listPrecisionAndRecallM2, resultPRMaxM2, resultPRMeanM2, resultPRMinM2);
//                    List<PrecisionAndRecall> listPrecisionAndRecallM3 = compareWithSingleExpectedInstance(generatedInstances, fileExpectedReparis, getAttributesToExclude(), 1.0, false);
//                    addPRResult(instance, listPrecisionAndRecallM3, resultPRMaxM3, resultPRMeanM3, resultPRMinM3);
                    if (isComputeQualityOfRoot()) {
                        String rootInstance = configuration.get("tmpDir") + "root.csv";
                        ExportChaseStepResultsCSV resultExporter = OperatorFactory.getInstance().getResultExporter(result.getScenario());
                        resultExporter.exportDatabase(result.getChaseTreeRoot(), "r", rootInstance);
                        List<PrecisionAndRecall> listPrecisionAndRecallRoot = compareWithSingleExpectedInstance(rootInstance, fileExpectedReparis, getAttributesToExclude(), 0.5, false);
                        PrecisionAndRecall rootSimilarity = computeMax(listPrecisionAndRecallRoot);
                        resultQualityRoot.append(instance).append("\t").append(rootSimilarity).append("\n");
                        computeRepairRate(instance, resultRepairRate, computeMax(listPrecisionAndRecallM1), rootSimilarity);
                    }
                }
                String resultString = buildStringResults(experimentName, resultTime, resultSolutions,
                        resultDuplicates, resultNodes, resultQualityRoot, resultRepairRate,
                        resultPRMaxM1, resultPRMinM1, resultPRMeanM1,
                        resultPRMaxM2, resultPRMinM2, resultPRMeanM2,
                        resultPRMaxM3, resultPRMinM3, resultPRMeanM3,
                        resultPRMaxCell, resultPRMinCell, resultPRMeanCell);
                System.out.println(resultString);
                if (logger.isDebugEnabled()) {
                    writeResults(experimentName, resultString, getSizes());
                }
            }
        }
    }

    private void computeRepairRate(String instance, StringBuilder resultRepairRate, PrecisionAndRecall prRepair, PrecisionAndRecall prDirty) {
        //1 ? (1 ? sim(Rep, DBexp ))/(1 ? sim(DBdirty,DBexp))
        double simRepair = prRepair.getfMeasure();
        double simDirty = prDirty.getfMeasure();
        double repairRate = 1 - (1 - simRepair) / (1 - simDirty);
        System.out.println("### Similarity dirty: " + simDirty);
        System.out.println("### Similarity repair: " + simRepair);
        System.out.println("### Repair rate: " + repairRate);
        resultRepairRate.append(instance).append("\t").append(repairRate).append("\n");
    }

    private void addPRResult(String instance, List<PrecisionAndRecall> listPrecisionAndRecall, StringBuilder resultPRMax, StringBuilder resultPRMean, StringBuilder resultPRMin) {
        if (listPrecisionAndRecall.isEmpty()) {
            return;
//            throw new IllegalArgumentException("Empty results!");
        }
        Collections.sort(listPrecisionAndRecall);
        PrecisionAndRecall max = computeMax(listPrecisionAndRecall);
        resultPRMax.append(instance).append("\t").append(max).append("\n");
        PrecisionAndRecall min = computeMin(listPrecisionAndRecall);
        resultPRMin.append(instance).append("\t").append(min).append("\n");
        PrecisionAndRecall mean = computeMean(listPrecisionAndRecall);
        resultPRMean.append(instance).append("\t").append(mean).append("\n");
    }

    private String buildStringResults(String testName, StringBuilder resultTime, StringBuilder resultSolutions,
            StringBuilder resultDuplicates, StringBuilder resultNodes, StringBuilder resultQualityRoot, StringBuilder resultRepairRate,
            StringBuilder resultPRMaxM1, StringBuilder resultPRMinM1, StringBuilder resultPRMeanM1,
            StringBuilder resultPRMaxM2, StringBuilder resultPRMinM2, StringBuilder resultPRMeanM2,
            StringBuilder resultPRMaxM3, StringBuilder resultPRMinM3, StringBuilder resultPRMeanM3,
            StringBuilder resultPRMaxCell, StringBuilder resultPRMinCell, StringBuilder resultPRMeanCell) {
        StringBuilder sb = new StringBuilder().append("\n");
        sb.append("----------------------------------------------------------------").append("\n");
        sb.append("--                ").append(testName).append("                    --").append("\n");
        sb.append("----------------------------------------------------------------").append("\n");
        if (isChaseScenario()) {
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append("--                         Times                       --").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultTime.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append("--                    Number of solutions                -").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultSolutions.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append("--                    Number of duplicates                -").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultDuplicates.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append("--                    Number of nodes                -").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultNodes.toString()).append("\n");
        }
        if (isComputeQualityOfRepairs() || isComputeQualityOfInstances()) {
            sb.append(resultPRMaxM1.toString()).append("\n");
            sb.append(resultPRMinM1.toString()).append("\n");
            sb.append(resultPRMeanM1.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");

//            sb.append(resultPRMaxM2.toString()).append("\n");
//            sb.append(resultPRMinM2.toString()).append("\n");
//            sb.append(resultPRMeanM2.toString()).append("\n");
//            sb.append("----------------------------------------------------------------").append("\n");

//            sb.append(resultPRMaxM3.toString()).append("\n");
//            sb.append(resultPRMinM3.toString()).append("\n");
//            sb.append(resultPRMeanM3.toString()).append("\n");
//            sb.append("----------------------------------------------------------------").append("\n");

//            sb.append(resultPRMaxCell.toString()).append("\n");
//            sb.append(resultPRMinCell.toString()).append("\n");
//            sb.append(resultPRMeanCell.toString()).append("\n");
//            sb.append("----------------------------------------------------------------").append("\n");
        }
        if (isComputeQualityOfRoot()) {
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(" QUALITY OF THE ROOT INSTANCE").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultQualityRoot.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(" REPAIR RATE").append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
            sb.append(resultRepairRate.toString()).append("\n");
            sb.append("----------------------------------------------------------------").append("\n");
        }
        return sb.toString();
    }

    public String generateFileScenario(String scenarioName, String size, int perturbation, String outputSubfolder, String suffix) {
        StringBuilder path = new StringBuilder();
        path.append(getBaseDir());
        path.append(configuration.get("experimentDir"));
        path.append(outputSubfolder);
        path.append(scenarioName);
        path.append("-");
        path.append(size);
//        if (perturbation != 0) {
        path.append("-");
        path.append(perturbation);
//        }
        path.append(configuration.get("fileNameSuffix"));
        path.append(suffix);
        return path.toString();
    }

    public String generateFileExpected(String scenarioName, String size, int perturbation) {
        if (isComputeQualityOfRepairs()) {
            return generateFileExpectedRepair(scenarioName, size, perturbation);
        } else {
            return generateFileExpectedInstance(scenarioName, size, perturbation);
        }
    }

    private String generateFileExpectedRepair(String scenarioName, String size, int perturbation) {
        StringBuilder path = new StringBuilder();
        path.append(getBaseDir());
        path.append(getDataDir());
        path.append(size);
        path.append("/");
        path.append("resultRepair_");
        path.append(perturbation);
        path.append("0");
        path.append(".csv");
        return path.toString();
    }

    protected String generateFileExpectedInstance(String scenarioName, String size, int perturbation) {
        StringBuilder path = new StringBuilder();
        path.append(getBaseDir());
        path.append(getDataDir());
        path.append(size);
        path.append("/core/core.csv");
        return path.toString();
    }

    private void writeResults(String testName, String resultString, String[] size) {
        String sizeString = "_";
        for (String s : size) {
            sizeString += s;
        }
        Writer out = null;
        try {
            String path = configuration.get("resultDir") + testName + configuration.get("suffix") + sizeString + ".txt";
            File outFile = new File(path);
            outFile.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            out.write(DateFormat.getDateTimeInstance().format(new Date()) + "\n");
            out.write(resultString);
        } catch (Exception ex) {
            logger.error("Unable to write results to string. " + ex);
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
            }
        }
    }

    public void deleteDB(Scenario scenario) {
        String script = "DROP DATABASE " + ((DBMSDB) scenario.getTarget()).getAccessConfiguration().getDatabaseName() + ";\n";
        System.out.println("Executing script " + script);
        QueryManager.executeScript(script, DBMSUtility.getTempAccessConfiguration(((DBMSDB) scenario.getTarget()).getAccessConfiguration()), true, true, true);
    }

    public void cleanWorkSchema(Scenario scenario) {
        String script = "DROP SCHEMA " + LunaticConstants.WORK_SCHEMA + " CASCADE;\n";
        System.out.println("Executing script " + script);
        QueryManager.executeScript(script, ((DBMSDB) scenario.getTarget()).getAccessConfiguration(), true, false, true);
    }

    public void importDataFromCSV(Scenario scenario, String size, int perturbation) {
        throw new UnsupportedOperationException();
    }

    private boolean isDeleteDB() {
        return (Boolean) configuration.get("deleteDB");
    }

    private boolean isComputeQualityOfRepairs() {
        return (Boolean) configuration.get("computeQuality");
    }

    private boolean isComputeQualityOfInstances() {
        return (Boolean) configuration.get("computeQualityOfInstances");
    }

    private boolean isComputeQualityOfRoot() {
        return (Boolean) configuration.get("computeQualityOfRoot");
    }

    private boolean isChaseScenario() {
        return (Boolean) configuration.get("chaseScenario");
    }

    private boolean isImportDataFromCSV() {
        return (Boolean) configuration.get("importDataFromCSV");
    }

    private boolean isCleanWorkSchema() {
        return (Boolean) configuration.get("cleanWorkSchema");
    }

    private String[] getSizes() {
        return (String[]) configuration.get("sizes");
    }

    private int[] getPerturbations() {
        return (int[]) configuration.get("perturbations");
    }

    public String getBaseDir() {
        return (String) configuration.get("baseDir") + configuration.get("scenarioName") + "/";
    }

    public String getDataDir() {
        return (String) configuration.get("dataDir");
    }

    @SuppressWarnings("unchecked")
    public List<String> getAttributesToExclude() {
        return (List<String>) configuration.get("attributesToExclude");
    }

    private StringBuilder initPRResult(String name, double value, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------------------------").append("\n");
        sb.append("--         ").append(name).append(" ").append(value).append(" ").append(type).append("                  --").append("\n");
        sb.append("----------------------------------------------------------------").append("\n");
        return sb;
    }

    public abstract void configureScenario(Map<String, Object> configuration);

    public void postExecute(BatchTestResult result) {
        IValueOccurrenceHandlerMC occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(result.getScenario());
        if (occurrenceHandler != null) {
            occurrenceHandler.reset();
        }
    }

    public void preExecute(Scenario scenario) {
    }
}
