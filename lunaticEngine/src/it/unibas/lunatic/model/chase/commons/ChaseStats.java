package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseStats {

    public static final String TOTAL_TIME = "Chasing Time";
    public static final String LOAD_TIME = "Loading Time";
    public static final String WRITE_TIME = "Writing Time";
    public static final String BUILD_SOLUTION_TIME = "Building Final Solution Time";
    public static final String FINAL_QUERY_TIME = "Final Query Time";
    public static final String EGD_TIME = "EGD Time";
    public static final String EGD_VIOLATION_QUERY_TIME = "EGD Violation Query Time";
    public static final String TGD_VIOLATION_QUERY_TIME = "TGD Violation Query Time";
    public static final String EGD_EQUIVALENCE_CLASS_TIME = "EGD Equivalence Class Time";
    public static final String EGD_CHOOSING_REPAIR_TIME = "EGD Choosing Repair Time";
    public static final String EGD_FIND_PREFERRED_VALUE_TIME = "EGD Find Preferred Value Time";
    public static final String EGD_REPAIR_TIME = "EGD Repairing Time";
    public static final String TGD_EQUIVALENCE_CLASS_TIME = "TGD Equivalence Class Time";
    public static final String TGD_GENERATE_UPDATE_TIME = "TGD Generate Update Time";
    public static final String TGD_REPAIR_TIME = "TGD Repairing Time";
    public static final String TGD_TIME = "TGD Time";
    public static final String STTGD_TIME = "ST-TGD Time";
    public static final String DTGD_TIME = "Denial TGD Time";
    public static final String DELTA_DB_BUILDER = "Building Delta DB";
    public static final String STEP_DB_BUILDER = "Building DB for Chase Step";
    public static final String ANALYZE_DB = "Analyze Database Time";
    public static final String REMOVE_DUPLICATE_TIME = "Removing Duplicate Time";
    public static final String DUPLICATE_TIME = "Finding Duplicate Time";
    public static final String COMPUTE_SIMILARITY_TIME = "Compute Similarity Time";
    public static final String CHECK_REDUNDANCY_TIME = "Check Redundancy Time";
    public static final String CACHE_LOAD_TIME = "Cache Load Time";
    public static final String CELL_GROUP_CONSISTENCY_CHECK_TIME = "CellGroup Consistency Check Time";
    public static final String NUMBER_OF_SOURCE_TABLES = "#S-TABLES";
    public static final String NUMBER_OF_SOURCE_ATTRIBUTES = "#S-ATTRIBUTES";
    public static final String NUMBER_OF_TARGET_TABLES = "#T-TABLES";
    public static final String NUMBER_OF_TARGET_ATTRIBUTES = "#T-ATTRIBUTES";
    public static final String NUMBER_OF_STTGDS = "#ST-TGDs";
    public static final String NUMBER_OF_EXTGDS = "#Ex-TGDs";
    public static final String NUMBER_OF_INCLUSION_DEPS = "#INC-DEPs";
    public static final String NUMBER_OF_EXTEGDS = "#Ex-EGDs";
    public static final String NUMBER_OF_EGDS = "#EGDs";
    public static final String NUMBER_OF_EXTGDS_FDS = "#Ex-GDS-FDs";
    public static final String NUMBER_OF_EGDS_FDS = "#EGDS-FDs";
    public static final String NUMBER_OF_QUERIES = "#QUERIES";
    public static final String NUMBER_OF_DCS = "#DCs";
    public static final String NUMBER_OF_DED_STTGDS = "#DED ST-TGDs";
    public static final String NUMBER_OF_DED_EXTGDS = "#DED Ex-TGDs";
    public static final String NUMBER_OF_DED_EGDS = "#DED EGDs";
    public static final String NUMBER_OF_GREEDY_SCENARIOS = "#Greedy Scenarios";
    public static final String NUMBER_OF_EXECUTED_GREEDY_SCENARIOS = "#Executed greedy Scenarios";
    public static final String NUMBER_OF_FAILED_GREEDY_SCENARIOS = "#Failed greedy Scenarios";
    /////
    public static final String PARSING_TIME = "Parsing Time";
    public static final String LOAD_XML_SCENARIO_TIME = "Load XML Scenario Time";
    public static final String PROCESS_DEPENDENCIES_TIME = "Process Dependencies Time";
    public static final String INIT_DB_TIME = "Init DB Time";
    /////
    public static final String HASHED_CELL_GROUPS = "#Hashed cellgroup";
    public static final String HASH_CELL_GROUP_TIME = "Hash cellgroup time";
    public static final String HASHED_CELL_GROUP_CELLS = "#Hashed cellgroup-cell";
    public static final String HASH_CELL_GROUP_CELL_TIME = "Hash cell group cell time";
    public static final String DICTIONARY_LOADING_TIME = "Dictionary loading time";
    public static final String DICTIONARY_WRITING_TIME = "Dictionary writing time";
    public static final String DICTIONARY_ENCODING_TIME = "Dictionary encoding time";
    public static final String DICTIONARY_DECODING_TIME = "Dictionary decoding time";
    public static final String DICTIONARY_CLOSING_TIME = "Dictionary closing time";
    /////
    public static final String CHECK_CONS_CELL_GROUPS = "Check consistency of cell groups time";
    /////
    public static final String TEMP_1 = "TEMP_1";
    public static final String TEMP_2 = "TEMP_2";
    public static final String TEMP_3 = "TEMP_3";
    public static final String TEMP_4 = "TEMP_4";
    public static final String TEMP_5 = "TEMP_5";
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
        Set<String> printedStats = new HashSet<String>();
        appendStat(NUMBER_OF_STTGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_EXTGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_EXTEGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_EGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_DCS, "", sb, printedStats);
        appendStat(NUMBER_OF_DED_STTGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_DED_EXTGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_DED_EGDS, "", sb, printedStats);
        appendStat(NUMBER_OF_GREEDY_SCENARIOS, "", sb, printedStats);
        appendStat(NUMBER_OF_EXECUTED_GREEDY_SCENARIOS, "", sb, printedStats);
        appendStat(NUMBER_OF_FAILED_GREEDY_SCENARIOS, "", sb, printedStats);
        sb.append("------ CHASE STATS ------").append("\n");
        appendStat(TOTAL_TIME, "ms", sb, printedStats);
        appendStat(LOAD_TIME, "ms", sb, printedStats);
        appendStat(INIT_DB_TIME, "ms", sb, printedStats);
        appendStat(BUILD_SOLUTION_TIME, "ms", sb, printedStats);
        appendStat(FINAL_QUERY_TIME, "ms", sb, printedStats);
        appendStat(STTGD_TIME, "ms", sb, printedStats);
        appendStat(EGD_TIME, "ms", sb, printedStats);
        appendStat(EGD_VIOLATION_QUERY_TIME, "ms", sb, printedStats);
        appendStat(EGD_EQUIVALENCE_CLASS_TIME, "ms", sb, printedStats);
        appendStat(EGD_CHOOSING_REPAIR_TIME, "ms", sb, printedStats);
        appendStat(EGD_REPAIR_TIME, "ms", sb, printedStats);
        appendStat(TGD_VIOLATION_QUERY_TIME, "ms", sb, printedStats);
        appendStat(TGD_EQUIVALENCE_CLASS_TIME, "ms", sb, printedStats);
        appendStat(TGD_GENERATE_UPDATE_TIME, "ms", sb, printedStats);
        appendStat(TGD_REPAIR_TIME, "ms", sb, printedStats);
        appendStat(TGD_TIME, "ms", sb, printedStats);
        appendStat(DTGD_TIME, "ms", sb, printedStats);
        appendStat(DELTA_DB_BUILDER, "ms", sb, printedStats);
        appendStat(STEP_DB_BUILDER, "ms", sb, printedStats);
        appendStat(CACHE_LOAD_TIME, "ms", sb, printedStats);
        appendStat(CELL_GROUP_CONSISTENCY_CHECK_TIME, "ms", sb, printedStats);
        appendStat(DUPLICATE_TIME, "ms", sb, printedStats);
        appendStat(REMOVE_DUPLICATE_TIME, "ms", sb, printedStats);
        appendStat(COMPUTE_SIMILARITY_TIME, "ms", sb, printedStats);
        appendStat(CHECK_REDUNDANCY_TIME, "ms", sb, printedStats);
        appendStat(WRITE_TIME, "ms", sb, printedStats);
        sb.append("-------------------------").append("\n");
        if (!dependencyStats.isEmpty()) {
            sb.append("------ DEPENDENCIES STATS ------").append("\n");
            for (Dependency d : dependencyStats.keySet()) {
                sb.append(d.getId()).append(": ").append(dependencyStats.get(d)).append(" ms").append("\n");
            }
            sb.append("-------------------------").append("\n");
        }
        appendOtherStats(printedStats, sb);
        return sb.toString();
    }

    private void appendStat(String key, String suffix, StringBuilder sb, Set<String> printedStats) {
        if (stats.containsKey(key)) {
            sb.append(key).append(": ").append(stats.get(key)).append(" ").append(suffix).append("\n");
            printedStats.add(key);
        }
    }

    private void appendOtherStats(Set<String> printedStats, StringBuilder sb) {
        List<String> otherStats = new ArrayList<String>(this.stats.keySet());
        otherStats.removeAll(printedStats);
        Collections.sort(otherStats);
        if (!otherStats.isEmpty()) {
            sb.append("------ OTHER ------").append("\n");
            for (String key : otherStats) {
                appendStat(key, "", sb, printedStats);
            }
        }
    }

}
