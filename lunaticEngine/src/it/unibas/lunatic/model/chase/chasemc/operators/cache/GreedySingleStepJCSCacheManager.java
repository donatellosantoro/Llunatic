package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.operators.IRunQuery;
import speedy.persistence.relational.QueryStatManager;

public class GreedySingleStepJCSCacheManager extends AbstractGreedyCacheManager {

    private static Logger logger = LoggerFactory.getLogger(GreedySingleStepJCSCacheManager.class);

    public static final String GROUPID = "standard";
    private final JCS cellGroupCache;
    private final JCS clusterIdCache;
    private Set<String> previousCachedStepIds = new HashSet<String>();
    private String currentCachedStepId;

    public GreedySingleStepJCSCacheManager(IRunQuery queryRunner) {
        super(queryRunner);
        try {
            this.cellGroupCache = JCS.getInstance("cellgroupcache");
            this.clusterIdCache = JCS.getInstance("clusteridcache");
        } catch (CacheException ex) {
            logger.error("Unable to create JCS Cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to create JCS Cache. " + ex.getLocalizedMessage());
        }
    }

    public CellGroup loadCellGroupFromId(IValue value, String stepId, IDatabase deltaDB, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Loading cellgroup for value " + value + " in step " + stepId);
        loadCacheForStep(stepId, deltaDB, scenario);
        if (logger.isDebugEnabled()) logger.debug("Cache in step " + stepId + "\n" + printCache());
        String key = buildKey(value, stepId);
        CellGroup cellGroup = (CellGroup) cellGroupCache.getFromGroup(key, GROUPID);
        if (logger.isDebugEnabled()) logger.debug("Returning " + cellGroup);
        if (isDebug(value, stepId)) {
            logger.warn("Loaded cell group in step " + stepId + "\n" + cellGroup + "\nCache:" + printCache());
        }
        return cellGroup;
    }

    private boolean isDebug(IValue value, String stepId) {
        return false;
//        return value.toString().equals("_L137") && stepId.startsWith("r.cfd1_0_f#.cfd2_0_f#.md3_0_f#");
//        return value.toString().equals("_L137") && stepId.equals("r.cfd1_0_f#.cfd2_0_f#.md3_0_f#.cfd2_0_f#");
    }

    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB, Scenario scenario) {
        try {
            loadCacheForStep(stepId, deltaDB, scenario);
            String key = buildKey(cellGroup.getId(), stepId);
            cellGroupCache.putInGroup(key, GROUPID, cellGroup);
            if (isDebug(cellGroup.getValue(), stepId)) {
                logger.warn("Put cell group in step " + stepId + "\n" + cellGroup + "\nCache:" + printCache());
            }
        } catch (CacheException ex) {
            logger.error("Unable to add objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to add objects to cache. " + ex.getLocalizedMessage());
        }
    }

    public void removeCellGroup(IValue value, String stepId) {
        String key = buildKey(value, stepId);
        this.cellGroupCache.remove(key, GROUPID);
    }

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario) {
        loadCacheForStep(stepId, deltaDB, scenario);
        String key = buildKey(cellRef, stepId);
        IValue value = (IValue) clusterIdCache.get(key);
        if (value == null) {
            return null;
        }
        return value;
    }

    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB, Scenario scenario) {
        try {
            loadCacheForStep(stepId, deltaDB, scenario);
            String key = buildKey(cellRef, stepId);
            this.clusterIdCache.put(key, value);
        } catch (CacheException ex) {
            logger.error("Unable to add objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to add objects to cache. " + ex.getLocalizedMessage());
        }
    }

    public void removeClusterId(CellRef cellRef, String stepId) {
        try {
            String key = buildKey(cellRef, stepId);
            this.clusterIdCache.remove(key);
        } catch (CacheException ex) {
            logger.error("Unable to remove objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to remove objects to cache. " + ex.getLocalizedMessage());
        }
    }

    public void reset() {
//        logger.warn("Resetting cache (Current step was : " + currentCachedStepId + ")...");
        try {
            //New step to cache... cleaning old step
            cellGroupCache.clear();
            clusterIdCache.clear();
        } catch (CacheException ex) {
            logger.error("Unable to clear cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to clear cache. " + ex.getLocalizedMessage());
        }
    }

    @Override
    protected void loadCacheForStep(String stepId, IDatabase deltaDB, Scenario scenario) {
        if (stepId.equals(currentCachedStepId)) {
            if (logger.isDebugEnabled()) logger.debug("Cache for step " + stepId + " already loaded...");
//            logger.warn("Cache for step " + stepId + " already loaded...");
            return;
        }
        if (LunaticConfiguration.sout) System.out.println("\t******Loading cache for step: " + stepId + " (current step: " + currentCachedStepId + ")");
//        if (previousCachedStepIds.contains(stepId)) {
//            if (logger.isDebugEnabled()) logger.debug("Reloading an old cached step... " + stepId);
////            throw new IllegalArgumentException("Unable to reload a previous cached step.\n\tRequired Step: " + stepId + "\n\tCurrent cached step: " + currentCachedStepId + "\n\t" + previousCachedStepIds);
//        }
        reset();
        if (logger.isDebugEnabled()) {
            logger.debug("### LOADING CACHE FOR STEP " + stepId);
            QueryStatManager.getInstance().printStatistics();
        }
        //LOAD CACHE
        currentCachedStepId = stepId;
        previousCachedStepIds.add(stepId);
        loadCellGroups(stepId, deltaDB, scenario);
        if (logger.isDebugEnabled()) logger.debug("Cell groups for step " + stepId + " loaded: " + this.cellGroupCache.getStats());
        if (logger.isDebugEnabled()) logger.debug("### CACHE FOR STEP " + stepId + " LOADED...");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getKeySet() {
        return cellGroupCache.getGroupKeys(GROUPID);
    }

    @Override
    public CellGroup getCellGroup(String key) {
        return (CellGroup) this.cellGroupCache.getFromGroup(key, GROUPID);
    }

    private String printCache() {
        StringBuilder sb = new StringBuilder();
        for (String key : getKeySet()) {
            sb.append("Key: ").append(key).append(", Value: ").append(getCellGroup(key)).append("\n");
        }
        return sb.toString();
    }
}

class CellRefStringComparator implements Comparator<CellRef> {

    public int compare(CellRef t, CellRef t1) {
        return t.toString().compareTo(t1.toString());
    }
}
