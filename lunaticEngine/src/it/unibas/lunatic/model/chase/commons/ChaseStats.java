package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseStats {

    public static final String TOTAL_TIME = "Total Time";
    public static final String EGD_TIME = "EGD Time";
    public static final String EGD_VIOLATION_QUERY_TIME = "EGD Violation Query Time";
    public static final String TGD_VIOLATION_QUERY_TIME = "TGD Violation Query Time";
    public static final String EGD_EQUIVALENCE_CLASS_TIME = "EGD Equivalence Class Time";
    public static final String EGD_REPAIR_TIME = "EGD Repairing Time";
    public static final String TGD_EQUIVALENCE_CLASS_TIME = "TGD Equivalence Class Time";
    public static final String TGD_GENERATE_UPDATE_TIME = "TGD Generate Update Time";
    public static final String TGD_REPAIR_TIME = "TGD Repairing Time";
    public static final String TGD_TIME = "TGD Time";
    public static final String STTGD_TIME = "ST-TGD Time";
    public static final String DTGD_TIME = "Denial TGD Time";
    public static final String DELTA_DB_BUILDER = "Building Delta DB";
    public static final String DELTA_DB_STEP_BUILDER = "Building Delta DB for Chase Step";
    public static final String REMOVE_DUPLICATE_TIME = "Removing Duplicate Time";
    public static final String DUPLICATE_TIME = "Finding Duplicate Time";
    public static final String COMPUTE_SIMILARITY_TIME = "Compute Similarity Time";
    public static final String CHECK_REDUNDANCY_TIME = "Check Redundancy Time";
    public static final String CACHE_LOAD_TIME = "Cache Load Time";
    public static final String CELL_GROUP_CONSISTENCY_CHECK_TIME = "CellGroup Consistency Check Time";
    public static final String NUMBER_OF_STTGDS = "#ST-TGDs";
    public static final String NUMBER_OF_EXTGDS = "#Ex-TGDs";
    public static final String NUMBER_OF_EXTEGDS = "#Ex-EGDs";
    public static final String NUMBER_OF_EGDS = "#EGDs";
    public static final String NUMBER_OF_DED_STTGDS = "#DED ST-TGDs";
    public static final String NUMBER_OF_DED_EXTGDS = "#DED Ex-TGDs";
    public static final String NUMBER_OF_DED_EGDS = "#DED EGDs";
    public static final String NUMBER_OF_DCS = "#DCs";
    public static final String NUMBER_OF_GREEDY_SCENARIOS = "#Greedy Scenarios";
    public static final String NUMBER_OF_EXECUTED_GREEDY_SCENARIOS = "#Executed greedy Scenarios";
    public static final String NUMBER_OF_FAILED_GREEDY_SCENARIOS = "#Failed greedy Scenarios";
    /////
    public static final String HASHED_CELL_GROUPS = "#Hashed cellgroup";
    public static final String HASH_CELL_GROUP_TIME = "Hash cellgroup time";
    public static final String HASHED_CELL_GROUP_CELLS = "#Hashed cellgroup-cell";
    public static final String HASH_CELL_GROUP_CELL_TIME = "Hash cell group cell time";
    /////
    private static Logger logger = LoggerFactory.getLogger(ChaseStats.class);
    private static ChaseStats singleton = new ChaseStats();
    private Map<String, Long> stats = new HashMap<String, Long>();
    /////
    private Map<Dependency, Long> dependencyStats = new HashMap<Dependency, Long>();

    public static ChaseStats getInstance() {
        return singleton;
    }

    private ChaseStats() {
    }

    public void printStatistics() {
        printStatistics("");
    }

    public void printStatistics(String prefix) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        logger.debug(prefix + this.toString());
    }

    public void addStat(String statName, long newTime) {
//        if (!logger.isDebugEnabled()) {
//            return;
//        }
        long previousTime = 0;
        if (stats.containsKey(statName)) {
            previousTime = stats.get(statName);
        }
        long totalTime = previousTime + newTime;
        stats.put(statName, totalTime);
    }

    public void addDepenendecyStat(Dependency dependency, long newTime) {
        long previousTime = 0;
        if (dependencyStats.containsKey(dependency)) {
            previousTime = dependencyStats.get(dependency);
        }
        long totalTime = previousTime + newTime;
        dependencyStats.put(dependency, totalTime);
    }

    public Long getStat(String statName) {
        return stats.get(statName);
    }

    public void resetStatistics() {
        stats.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------ SCENARIO STATS ------").append("\n");
        if (stats.containsKey(NUMBER_OF_STTGDS)) sb.append(NUMBER_OF_STTGDS + ": ").append(stats.get(NUMBER_OF_STTGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_EXTGDS)) sb.append(NUMBER_OF_EXTGDS + ": ").append(stats.get(NUMBER_OF_EXTGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_EXTEGDS)) sb.append(NUMBER_OF_EXTEGDS + ": ").append(stats.get(NUMBER_OF_EXTEGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_EGDS)) sb.append(NUMBER_OF_EGDS + ": ").append(stats.get(NUMBER_OF_EGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_DCS)) sb.append(NUMBER_OF_DCS + ": ").append(stats.get(NUMBER_OF_DCS)).append("\n");
        if (stats.containsKey(NUMBER_OF_DED_STTGDS)) sb.append(NUMBER_OF_DED_STTGDS + ": ").append(stats.get(NUMBER_OF_DED_STTGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_DED_EXTGDS)) sb.append(NUMBER_OF_DED_EXTGDS + ": ").append(stats.get(NUMBER_OF_DED_EXTGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_DED_EGDS)) sb.append(NUMBER_OF_DED_EGDS + ": ").append(stats.get(NUMBER_OF_DED_EGDS)).append("\n");
        if (stats.containsKey(NUMBER_OF_GREEDY_SCENARIOS)) sb.append(NUMBER_OF_GREEDY_SCENARIOS + ": ").append(stats.get(NUMBER_OF_GREEDY_SCENARIOS)).append("\n");
        if (stats.containsKey(NUMBER_OF_EXECUTED_GREEDY_SCENARIOS)) sb.append(NUMBER_OF_EXECUTED_GREEDY_SCENARIOS + ": ").append(stats.get(NUMBER_OF_EXECUTED_GREEDY_SCENARIOS)).append("\n");
        if (stats.containsKey(NUMBER_OF_FAILED_GREEDY_SCENARIOS)) sb.append(NUMBER_OF_FAILED_GREEDY_SCENARIOS + ": ").append(stats.get(NUMBER_OF_FAILED_GREEDY_SCENARIOS)).append("\n");
        sb.append("------ CHASE STATS ------").append("\n");
        if (stats.containsKey(TOTAL_TIME)) sb.append(TOTAL_TIME + ": ").append(stats.get(TOTAL_TIME)).append(" ms").append("\n");
        if (stats.containsKey(STTGD_TIME)) sb.append(STTGD_TIME + ": ").append(stats.get(STTGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_TIME)) sb.append(EGD_TIME + ": ").append(stats.get(EGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_VIOLATION_QUERY_TIME)) sb.append(EGD_VIOLATION_QUERY_TIME + ": ").append(stats.get(EGD_VIOLATION_QUERY_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_EQUIVALENCE_CLASS_TIME)) sb.append(EGD_EQUIVALENCE_CLASS_TIME + ": ").append(stats.get(EGD_EQUIVALENCE_CLASS_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_REPAIR_TIME)) sb.append(EGD_REPAIR_TIME + ": ").append(stats.get(EGD_REPAIR_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_VIOLATION_QUERY_TIME)) sb.append(TGD_VIOLATION_QUERY_TIME + ": ").append(stats.get(TGD_VIOLATION_QUERY_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_EQUIVALENCE_CLASS_TIME)) sb.append(TGD_EQUIVALENCE_CLASS_TIME + ": ").append(stats.get(TGD_EQUIVALENCE_CLASS_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_GENERATE_UPDATE_TIME)) sb.append(TGD_GENERATE_UPDATE_TIME + ": ").append(stats.get(TGD_GENERATE_UPDATE_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_REPAIR_TIME)) sb.append(TGD_REPAIR_TIME + ": ").append(stats.get(TGD_REPAIR_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_TIME)) sb.append(TGD_TIME + ": ").append(stats.get(TGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(DTGD_TIME)) sb.append(DTGD_TIME + ": ").append(stats.get(DTGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(DELTA_DB_BUILDER)) sb.append(DELTA_DB_BUILDER + ": ").append(stats.get(DELTA_DB_BUILDER)).append(" ms").append("\n");
        if (stats.containsKey(DELTA_DB_STEP_BUILDER)) sb.append(DELTA_DB_STEP_BUILDER + ": ").append(stats.get(DELTA_DB_STEP_BUILDER)).append(" ms").append("\n");
        if (stats.containsKey(CACHE_LOAD_TIME)) sb.append(CACHE_LOAD_TIME + ": ").append(stats.get(CACHE_LOAD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(CELL_GROUP_CONSISTENCY_CHECK_TIME)) sb.append(CELL_GROUP_CONSISTENCY_CHECK_TIME + ": ").append(stats.get(CELL_GROUP_CONSISTENCY_CHECK_TIME)).append(" ms").append("\n");
        if (stats.containsKey(DUPLICATE_TIME)) sb.append(DUPLICATE_TIME + ": ").append(stats.get(DUPLICATE_TIME)).append(" ms").append("\n");
        if (stats.containsKey(REMOVE_DUPLICATE_TIME)) sb.append(REMOVE_DUPLICATE_TIME + ": ").append(stats.get(REMOVE_DUPLICATE_TIME)).append(" ms").append("\n");
        if (stats.containsKey(COMPUTE_SIMILARITY_TIME)) sb.append(COMPUTE_SIMILARITY_TIME + ": ").append(stats.get(COMPUTE_SIMILARITY_TIME)).append(" ms").append("\n");
        if (stats.containsKey(CHECK_REDUNDANCY_TIME)) sb.append(CHECK_REDUNDANCY_TIME + ": ").append(stats.get(CHECK_REDUNDANCY_TIME)).append(" ms").append("\n");
        sb.append("-------------------------").append("\n");
        sb.append("------ CHASE STATS ------").append("\n");
        for (Dependency d : dependencyStats.keySet()) {
            sb.append(d.getId()).append(": ").append(dependencyStats.get(d)).append(" ms").append("\n");
        }
        sb.append("-------------------------").append("\n");
        sb.append("------ HASH STATS ------").append("\n");
        if (stats.containsKey(HASHED_CELL_GROUPS)) sb.append(HASHED_CELL_GROUPS + ": ").append(stats.get(HASHED_CELL_GROUPS)).append("\n");
        if (stats.containsKey(HASH_CELL_GROUP_TIME)) sb.append(HASH_CELL_GROUP_TIME + ": ").append(stats.get(HASH_CELL_GROUP_TIME)).append(" ms").append("\n");
        if (stats.containsKey(HASHED_CELL_GROUP_CELLS)) sb.append(HASHED_CELL_GROUP_CELLS + ": ").append(stats.get(HASHED_CELL_GROUP_CELLS)).append("\n");
        if (stats.containsKey(HASH_CELL_GROUP_CELL_TIME)) sb.append(HASH_CELL_GROUP_CELL_TIME + ": ").append(stats.get(HASH_CELL_GROUP_CELL_TIME)).append(" ms").append("\n");
        sb.append("-------------------------").append("\n");
        return sb.toString();
    }

}
