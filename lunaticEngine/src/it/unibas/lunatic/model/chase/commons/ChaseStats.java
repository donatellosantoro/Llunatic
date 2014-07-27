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
    public static final String DUPLICATE_TIME = "Finding Duplicate Time";
    /////
    private static Logger logger = LoggerFactory.getLogger(ChaseStats.class);
    private static ChaseStats singleton = new ChaseStats();
    private Map<String, Long> stats = new HashMap<String, Long>();
    
    public static ChaseStats getInstance() {
        return singleton;
    }
    
    private ChaseStats() {
    }
    
    public void printStats() {
        if (!logger.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("------ CHASE STATS ------").append("\n");
        if(stats.containsKey(TOTAL_TIME)) sb.append( TOTAL_TIME + ": ").append(stats.get(TOTAL_TIME)).append(" ms").append("\n");
        if(stats.containsKey(STTGD_TIME)) sb.append(STTGD_TIME + ": ").append(stats.get(STTGD_TIME)).append(" ms").append("\n");
        if(stats.containsKey(EGD_TIME)) sb.append( EGD_TIME + ": ").append(stats.get(EGD_TIME)).append(" ms").append("\n");
        if(stats.containsKey(EGD_VIOLATION_QUERY_TIME)) sb.append( EGD_VIOLATION_QUERY_TIME + ": ").append(stats.get(EGD_VIOLATION_QUERY_TIME)).append(" ms").append("\n");
        if(stats.containsKey(EGD_EQUIVALENCE_CLASS_TIME)) sb.append( EGD_EQUIVALENCE_CLASS_TIME + ": ").append(stats.get(EGD_EQUIVALENCE_CLASS_TIME)).append(" ms").append("\n");
        if(stats.containsKey(EGD_REPAIR_TIME)) sb.append( EGD_REPAIR_TIME + ": ").append(stats.get(EGD_REPAIR_TIME)).append(" ms").append("\n");
        if(stats.containsKey(TGD_TIME)) sb.append(TGD_TIME + ": ").append(stats.get(TGD_TIME)).append(" ms").append("\n");
        if(stats.containsKey(DTGD_TIME)) sb.append(DTGD_TIME + ": ").append(stats.get(DTGD_TIME)).append(" ms").append("\n");
        if(stats.containsKey(DELTA_DB_BUILDER)) sb.append( DELTA_DB_BUILDER + ": ").append(stats.get(DELTA_DB_BUILDER)).append(" ms").append("\n");
        if(stats.containsKey(DELTA_DB_STEP_BUILDER)) sb.append( DELTA_DB_STEP_BUILDER + ": ").append(stats.get(DELTA_DB_STEP_BUILDER)).append(" ms").append("\n");
        if(stats.containsKey(DUPLICATE_TIME)) sb.append(DUPLICATE_TIME + ": ").append(stats.get(DUPLICATE_TIME)).append(" ms").append("\n");
        sb.append("-------------------------").append("\n");
        logger.debug(sb.toString());
    }
    
    public void addStat(String statName, long newTime) {
        if (!logger.isDebugEnabled()) {
            return;
        }
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
}
