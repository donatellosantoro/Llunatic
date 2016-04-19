package it.unibas.lunatic;

public class LunaticConfiguration {

//    private static boolean printSteps = true;
    private static boolean printSteps = false;
    private boolean debugMode = false;
    private boolean recreateDBOnStart = false;
    private boolean cleanSchemasOnStartForDEScenarios = true;
    private Integer iterationLimit = null;
    private boolean useLimit1ForEGDs = false;
    private boolean deProxyMode = false; //MCProxy for DE chase
    private boolean checkGroundSolutions = false;
    private boolean checkSolutions = false;
    private boolean checkSolutionsQuery = false;
    private boolean checkConsistencyOfDBs = false;
    private boolean removeDuplicates = true;
    private boolean checkAllNodesForEGDSatisfaction = false;
    private boolean forceMCChaserInTests = false;
    private boolean useSymmetricOptimization = true;
    private boolean checkDCDuringChase = false;
    private boolean chaseDEDGreedyExecuteAllScenarios = false;
    private boolean chaseDEDGreedyRandomScenarios = false;
    private boolean discardDuplicateTuples = false;
    private double numberCellsWeightForRanking = 0.7;
    private boolean useHashForSkolem = true;
    private boolean useBatchInsert = true;
    private boolean exportSolutions = false;
    private String exportSolutionsPath;
    private String exportSolutionsType;
    private boolean exportChanges = false;
    private String exportChangesPath;

    private int maxNumberOfThreads = 12;
//
//    private String deChaser = LunaticConstants.CLASSIC_DE_CHASER;
//    private final String deChaser = LunaticConstants.PROXY_MC_CHASER;
    private final String deChaser = LunaticConstants.OPTIMIZED_CHASER;
//
//    private String cacheType = LunaticConstants.NO_CACHE;
//    private String cacheType = LunaticConstants.LAZY_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_JCS;
    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_JCS_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_SIMPLE_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_SIMPLE_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_EHCACHE;
//    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_EHCACHE_CACHE;

    public static boolean isPrintSteps() {
        return printSteps;
    }

    public static void setPrintSteps(boolean aPrintSteps) {
        printSteps = aPrintSteps;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isRecreateDBOnStart() {
        return recreateDBOnStart;
    }

    public void setRecreateDBOnStart(boolean recreateDBOnStart) {
        this.recreateDBOnStart = recreateDBOnStart;
    }

    public boolean isRemoveDuplicates() {
        return removeDuplicates;
    }

    public void setRemoveDuplicates(boolean removeDuplicates) {
        this.removeDuplicates = removeDuplicates;
    }

    public Integer getIterationLimit() {
        return iterationLimit;
    }

    public void setIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }

    public boolean isUseLimit1ForEGDs() {
        return useLimit1ForEGDs;
    }

    public void setUseLimit1ForEGDs(boolean useLimit1ForEGDs) {
        this.useLimit1ForEGDs = useLimit1ForEGDs;
    }

    public boolean isDeProxyMode() {
        return deProxyMode;
    }

    public void setDeProxyMode(boolean deProxyMode) {
        this.deProxyMode = deProxyMode;
    }

    public boolean isCheckGroundSolutions() {
        return checkGroundSolutions;
    }

    public void setCheckGroundSolutions(boolean checkGroundSolutions) {
        this.checkGroundSolutions = checkGroundSolutions;
    }

    public boolean isCheckAllNodesForEGDSatisfaction() {
        return checkAllNodesForEGDSatisfaction;
    }

    public void setCheckAllNodesForEGDSatisfaction(boolean checkAllNodesForEGDSatisfaction) {
        this.checkAllNodesForEGDSatisfaction = checkAllNodesForEGDSatisfaction;
    }

    public boolean isCheckSolutionsQuery() {
        return checkSolutionsQuery;
    }

    public void setCheckSolutionsQuery(boolean checkSolutionsQuery) {
        this.checkSolutionsQuery = checkSolutionsQuery;
    }

    public boolean isCheckSolutions() {
        return checkSolutions;
    }

    public void setCheckSolutions(boolean checkSolutions) {
        this.checkSolutions = checkSolutions;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setUseCache(String cacheType) {
        this.cacheType = cacheType;
    }

    public boolean isForceMCChaserInTests() {
        return forceMCChaserInTests;
    }

    public void setForceMCChaserInTests(boolean forceMCChaser) {
        this.forceMCChaserInTests = forceMCChaser;
    }

    public void changeParametersForScalabilityTests() {
        this.checkGroundSolutions = false;
        this.removeDuplicates = false;
    }

    public String getDeChaser() {
        return deChaser;
    }

//    public void setDeChaser(String deChaser) {
//        this.deChaser = deChaser;
//    }
    public boolean isUseSymmetricOptimization() {
        return useSymmetricOptimization;
    }

    public void setUseSymmetricOptimization(boolean useSymmetricOptimization) {
        this.useSymmetricOptimization = useSymmetricOptimization;
    }

    public boolean isChaseDEDGreedyExecuteAllScenarios() {
        return chaseDEDGreedyExecuteAllScenarios;
    }

    public void setChaseDEDGreedyExecuteAllScenarios(boolean chaseDEDGreedyExecuteAllScenarios) {
        this.chaseDEDGreedyExecuteAllScenarios = chaseDEDGreedyExecuteAllScenarios;
    }

    public boolean isCheckDCDuringChase() {
        return checkDCDuringChase;
    }

    public void setCheckDCDuringChase(boolean checkDCDuringChase) {
        this.checkDCDuringChase = checkDCDuringChase;
    }

    public boolean isCheckConsistencyOfDBs() {
        return checkConsistencyOfDBs;
    }

    public void setCheckConsistencyOfDBs(boolean checkConsistencyOfDBs) {
        this.checkConsistencyOfDBs = checkConsistencyOfDBs;
    }

    public boolean isChaseDEDGreedyRandomScenarios() {
        return chaseDEDGreedyRandomScenarios;
    }

    public void setChaseDEDGreedyRandomScenarios(boolean chaseDEDGreedyRandomScenarios) {
        this.chaseDEDGreedyRandomScenarios = chaseDEDGreedyRandomScenarios;
    }

    public boolean isDiscardDuplicateTuples() {
        return discardDuplicateTuples;
    }

    public void setDiscardDuplicateTuples(boolean discardDuplicateTuples) {
        this.discardDuplicateTuples = discardDuplicateTuples;
    }

    public double getNumberCellsWeightForRanking() {
        return numberCellsWeightForRanking;
    }

    public void setNumberCellsWeightForRanking(double numberCellsWeightForRanking) {
        this.numberCellsWeightForRanking = numberCellsWeightForRanking;
    }

    public boolean isUseHashForSkolem() {
        return useHashForSkolem;
    }

    public void setUseHashForSkolem(boolean useHashForSkolem) {
        this.useHashForSkolem = useHashForSkolem;
    }

    public boolean isUseBatchInsert() {
        return useBatchInsert;
    }

    public void setUseBatchInsert(boolean useBatchInsert) {
        this.useBatchInsert = useBatchInsert;
    }

    public boolean isExportSolutions() {
        return exportSolutions;
    }

    public void setExportSolutions(boolean exportSolutions) {
        this.exportSolutions = exportSolutions;
    }

    public String getExportSolutionsPath() {
        return exportSolutionsPath;
    }

    public void setExportSolutionsPath(String exportSolutionsPath) {
        this.exportSolutionsPath = exportSolutionsPath;
    }

    public String getExportSolutionsType() {
        return exportSolutionsType;
    }

    public void setExportSolutionsType(String exportSolutionsType) {
        this.exportSolutionsType = exportSolutionsType;
    }

    public boolean isExportChanges() {
        return exportChanges;
    }

    public void setExportChanges(boolean exportChanges) {
        this.exportChanges = exportChanges;
    }

    public String getExportChangesPath() {
        return exportChangesPath;
    }

    public void setExportChangesPath(String exportChangesPath) {
        this.exportChangesPath = exportChangesPath;
    }

    public boolean isCleanSchemasOnStartForDEScenarios() {
        return cleanSchemasOnStartForDEScenarios;
    }

    public void setCleanSchemasOnStartForDEScenarios(boolean cleanSchemasOnStartForDEScenarios) {
        this.cleanSchemasOnStartForDEScenarios = cleanSchemasOnStartForDEScenarios;
    }

    public int getMaxNumberOfThreads() {
        return maxNumberOfThreads;
    }

    public void setMaxNumberOfThreads(int maxNumberOfThreads) {
        this.maxNumberOfThreads = maxNumberOfThreads;
    }

    @Override
    public String toString() {
        return "\tDebugMode: " + debugMode
                + "\n\t PrintSteps: " + printSteps
                + "\n\t IterationLimit: " + iterationLimit
                + "\n\t RecreateDBOnStart: " + recreateDBOnStart
                + "\n\t UseLimit1: " + useLimit1ForEGDs
                + "\n\t DEProxyMode: " + deProxyMode
                + "\n\t CheckSolutions: " + checkSolutions
                + "\n\t CheckSolutionsQuery: " + checkSolutionsQuery
                + "\n\t CheckGroundSolutions: " + checkGroundSolutions
                + "\n\t CheckConsistencyOfDBs: " + checkConsistencyOfDBs
                + "\n\t RemoveDuplicates: " + removeDuplicates
                + "\n\t CheckAllNodesForEGDSatisfaction: " + checkAllNodesForEGDSatisfaction
                + "\n\t UseSymmetricOptimization: " + useSymmetricOptimization
                + "\n\t NumberCellsWeightForRanking: " + numberCellsWeightForRanking
                + "\n\t DiscardDuplicateTuples: " + discardDuplicateTuples
                + "\n\t MaxNumberOfThreads: " + maxNumberOfThreads
                + "\n\t UseBatchInsert: " + useBatchInsert
                + "\n\t ExportSolutions: " + exportSolutions
                + "\n\t ExportSolutionsPath: " + exportSolutionsPath
                + "\n\t ExportSolutionsType: " + exportSolutionsType
                + "\n\t Cache type: " + cacheType
                + "\n\t DeChaser: " + deChaser;
    }
}
