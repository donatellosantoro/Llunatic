package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import static it.unibas.lunatic.model.chase.chasemc.operators.cache.GreedySingleStepJCSCacheManager.GROUPID;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import java.util.HashSet;
import java.util.Set;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedyJCSCacheManager extends AbstractGreedyCacheManager {

    private static Logger logger = LoggerFactory.getLogger(GreedyJCSCacheManager.class);

    private final JCS cellGroupCache;
    private final JCS clusterIdCache;
    private Set<String> cachedStepIds = new HashSet<String>();

    public GreedyJCSCacheManager(IRunQuery queryRunner) {
        super(queryRunner);
        try {
            this.cellGroupCache = JCS.getInstance("cellgroupcache");
            this.clusterIdCache = JCS.getInstance("clusteridcache");
        } catch (CacheException ex) {
            logger.error("Unable to create JCS Cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to create JCS Cache. " + ex.getLocalizedMessage());
        }
            System.out.println("############################ NEW JCS CACHE");
    }

    public CellGroup getCellGroup(IValue value, String stepId, IDatabase deltaDB) {
        loadCacheForStep(stepId, deltaDB);
        String key = buildKey(value, stepId);
        CellGroup cellGroup = (CellGroup) cellGroupCache.getFromGroup(key, GROUPID);
        if (cellGroup == null) {
            return null;
        }
        return cellGroup;
    }

    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB) {
        try {
            loadCacheForStep(stepId, deltaDB);
            String key = buildKey(cellGroup.getId(), stepId);
            cellGroupCache.putInGroup(key, GROUPID, cellGroup);
        } catch (CacheException ex) {
            logger.error("Unable to add objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to add objects to cache. " + ex.getLocalizedMessage());
        }
    }

    public void removeCellGroup(IValue value, String stepId) {
        String key = buildKey(value, stepId);
        this.cellGroupCache.remove(key, GROUPID);
    }

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB) {
        loadCacheForStep(stepId, deltaDB);
        String key = buildKey(cellRef, stepId);
        IValue value = (IValue) clusterIdCache.get(key);
        if (value == null) {
            return null;
        }
        return value;
    }

    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB) {
        try {
            loadCacheForStep(stepId, deltaDB);
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
        try {
            System.out.println("############################ RESETTING JCS CACHE");
            //New step to cache... cleaning old step
            cellGroupCache.clear();
            clusterIdCache.clear();
        } catch (CacheException ex) {
            logger.error("Unable to clear objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to clear objects to cache. " + ex.getLocalizedMessage());
        }
    }

    @Override
    protected void loadCacheForStep(String stepId, IDatabase deltaDB) {
        if (cachedStepIds.contains(stepId)) {
            return;
        }
        //LOAD CACHE
        cachedStepIds.add(stepId);
        loadCellGroupsAndOccurrences(stepId, deltaDB);
        loadProvenances(stepId, deltaDB);
        if (logger.isDebugEnabled()) logger.debug("Cache loaded...\n"
                    + "\tStep loaded: " + cachedStepIds.size() + "\n"
                    + "\tCluster id cache max objects: " + clusterIdCache.getCacheAttributes().getMaxObjects() + "\n"
                    + "\tCellgroup max objects: " + cellGroupCache.getCacheAttributes().getMaxObjects());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Set<String> getKeySet() {
        return cellGroupCache.getGroupKeys(GROUPID);
    }

    @Override
    protected CellGroup getCellGroup(String key) {
        return (CellGroup) this.cellGroupCache.getFromGroup(key, GROUPID);
    }
}
