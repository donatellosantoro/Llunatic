package it.unibas.lunatic;

public class LunaticConfiguration {

    public static boolean sout = false;
    private boolean debugMode = false;
    private Integer iterationLimit = null;
    private boolean useLimit1 = false;
    private boolean useCellGroupsForTGDs = true;
    private boolean checkGroundSolutions = false;
    private boolean checkSolutions = false;
    private boolean checkSolutionsQuery = false;
    private boolean removeDuplicates = true;
    private boolean removeSuspiciousSolutions = true;
    private boolean checkAllNodesForEGDSatisfaction = false;
    private boolean forceMCChaserInTests = false;
    private boolean useSymmetricOptimization = true;
    private boolean chaseDEDGreedyExecuteAllScenarios = false;
//
//    private String deChaser = LunaticConstants.CLASSIC_DE_CHASER;
    private String deChaser = LunaticConstants.PROXY_MC_CHASER;
//
//    private String cacheType = LunaticConstants.NO_CACHE;
//    private String cacheType = LunaticConstants.LAZY_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_JCS;
    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_JCS_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_SIMPLE_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_SIMPLE_CACHE;
//    private String cacheType = LunaticConstants.GREEDY_EHCACHE;
//    private String cacheType = LunaticConstants.GREEDY_SINGLESTEP_EHCACHE_CACHE;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
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

    public boolean isUseLimit1() {
        return useLimit1;
    }

    public void setUseLimit1(boolean useLimit1) {
        this.useLimit1 = useLimit1;
    }

    public boolean isUseCellGroupsForTGDs() {
        return useCellGroupsForTGDs;
    }

    public void setUseCellGroupsForTGDs(boolean useCellGroupsForTGDs) {
        this.useCellGroupsForTGDs = useCellGroupsForTGDs;
    }

    public boolean isCheckGroundSolutions() {
        return checkGroundSolutions;
    }

    public void setCheckGroundSolutions(boolean checkGroundSolutions) {
        this.checkGroundSolutions = checkGroundSolutions;
    }

    public boolean isRemoveSuspiciousSolutions() {
        return removeSuspiciousSolutions;
    }

    public void setRemoveSuspiciousSolutions(boolean removeSuspiciousSolutions) {
        this.removeSuspiciousSolutions = removeSuspiciousSolutions;
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
        this.removeSuspiciousSolutions = false;
    }

    public String getDeChaser() {
        return deChaser;
    }

    public void setDeChaser(String deChaser) {
        this.deChaser = deChaser;
    }

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

    @Override
    public String toString() {
        return "\tDebugMode: " + debugMode
                + "\n\tIterationLimit: " + iterationLimit
                + "\n\tUseLimit1: " + useLimit1
                + "\n\tUseCellGroupsForTGDs: " + useCellGroupsForTGDs
                + "\n\tCheckSolutions: " + checkSolutions
                + "\n\tCheckSolutionsQuery: " + checkSolutionsQuery
                + "\n\tCheckGroundSolutions: " + checkGroundSolutions
                + "\n\tRemoveDuplicates: " + removeDuplicates
                + "\n\tRemoveSuspiciousSolutions: " + removeSuspiciousSolutions
                + "\n\tCheckAllNodesForEGDSatisfaction: " + checkAllNodesForEGDSatisfaction
                + "\n\tUseSymmetricOptimization: " + useSymmetricOptimization
                + "\n\tCache type: " + cacheType
                + "\n\tDeChaser: " + deChaser;
    }
}
