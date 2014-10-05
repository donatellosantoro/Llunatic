package it.unibas.lunatic.model.chase.commons;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseStats {

    public static final String TOTAL_TIME = "Total Time";
    public static final String EGD_TIME = "EGD Time";
    public static final String EGD_VIOLATION_QUERY_TIME = "EGD Violation Query Time";
    public static final String EGD_EQUIVALENCE_CLASS_TIME = "EGD Equivalence Class Time";
    public static final String EGD_REPAIR_TIME = "EGD Repairing Time";
    public static final String TGD_TIME = "TGD Time";
    public static final String STTGD_TIME = "ST-TGD Time";
    public static final String DTGD_TIME = "Denial TGD Time";
    public static final String DELTA_DB_BUILDER = "Building Delta DB";
    public static final String DELTA_DB_STEP_BUILDER = "Building Delta DB for Chase Step";
    public static final String REMOVE_DUPLICATE_TIME = "Removing Duplicate Time";
    public static final String DUPLICATE_TIME = "Finding Duplicate Time";
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
    /////
    private static Logger logger = LoggerFactory.getLogger(ChaseStats.class);
    private static ChaseStats singleton = new ChaseStats();
    private Map<String, Long> stats = new HashMap<String, Long>();

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
        sb.append("------ CHASE STATS ------").append("\n");
        if (stats.containsKey(TOTAL_TIME)) sb.append(TOTAL_TIME + ": ").append(stats.get(TOTAL_TIME)).append(" ms").append("\n");
        if (stats.containsKey(STTGD_TIME)) sb.append(STTGD_TIME + ": ").append(stats.get(STTGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_TIME)) sb.append(EGD_TIME + ": ").append(stats.get(EGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_VIOLATION_QUERY_TIME)) sb.append(EGD_VIOLATION_QUERY_TIME + ": ").append(stats.get(EGD_VIOLATION_QUERY_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_EQUIVALENCE_CLASS_TIME)) sb.append(EGD_EQUIVALENCE_CLASS_TIME + ": ").append(stats.get(EGD_EQUIVALENCE_CLASS_TIME)).append(" ms").append("\n");
        if (stats.containsKey(EGD_REPAIR_TIME)) sb.append(EGD_REPAIR_TIME + ": ").append(stats.get(EGD_REPAIR_TIME)).append(" ms").append("\n");
        if (stats.containsKey(TGD_TIME)) sb.append(TGD_TIME + ": ").append(stats.get(TGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(DTGD_TIME)) sb.append(DTGD_TIME + ": ").append(stats.get(DTGD_TIME)).append(" ms").append("\n");
        if (stats.containsKey(DELTA_DB_BUILDER)) sb.append(DELTA_DB_BUILDER + ": ").append(stats.get(DELTA_DB_BUILDER)).append(" ms").append("\n");
        if (stats.containsKey(DELTA_DB_STEP_BUILDER)) sb.append(DELTA_DB_STEP_BUILDER + ": ").append(stats.get(DELTA_DB_STEP_BUILDER)).append(" ms").append("\n");
        if (stats.containsKey(DUPLICATE_TIME)) sb.append(DUPLICATE_TIME + ": ").append(stats.get(DUPLICATE_TIME)).append(" ms").append("\n");
        if (stats.containsKey(REMOVE_DUPLICATE_TIME)) sb.append(REMOVE_DUPLICATE_TIME + ": ").append(stats.get(REMOVE_DUPLICATE_TIME)).append(" ms").append("\n");
        sb.append("-------------------------").append("\n");
        return sb.toString();
    }

}
