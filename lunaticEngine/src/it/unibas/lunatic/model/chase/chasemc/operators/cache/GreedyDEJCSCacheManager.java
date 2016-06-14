package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.Set;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.operators.IRunQuery;

public class GreedyDEJCSCacheManager extends AbstractGreedyCacheManager {

    private static Logger logger = LoggerFactory.getLogger(GreedyDEJCSCacheManager.class);

    private final CacheAccess<String, CellGroup> cellGroupCache;
    private boolean rootCellGroupsLoaded = false;

    public GreedyDEJCSCacheManager(IRunQuery queryRunner) {
        super(queryRunner);
        try {
            this.cellGroupCache = JCS.getInstance("cellgroupcache");
        } catch (CacheException ex) {
            logger.error("Unable to create JCS Cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to create JCS Cache. " + ex.getLocalizedMessage());
        }
    }

    @Override
    public CellGroup loadCellGroupFromId(IValue value, String stepId, IDatabase deltaDB, Scenario scenario) {
        loadCacheForStep(stepId, deltaDB, scenario);
        //For DE only the Root step is used for the entire chase
        String key = buildKey(value, LunaticConstants.CHASE_STEP_ROOT);
        CellGroup cellGroup = (CellGroup) cellGroupCache.get(key);
        return cellGroup;
    }

    @Override
    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB, Scenario scenario) {
        try {
            loadCacheForStep(stepId, deltaDB, scenario);
            String key = buildKey(cellGroup.getId(), LunaticConstants.CHASE_STEP_ROOT);
            cellGroupCache.put(key, cellGroup);
        } catch (CacheException ex) {
            logger.error("Unable to add objects to cache. " + ex.getLocalizedMessage());
            throw new IllegalStateException("Unable to add objects to cache. " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void removeCellGroup(IValue value, String stepId) {
        String key = buildKey(value, LunaticConstants.CHASE_STEP_ROOT);
        this.cellGroupCache.remove(key);
    }

    @Override
    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario) {
        throw new UnsupportedOperationException("Operation not possibile in DE scenarios");
    }

    @Override
    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB, Scenario scenario) {
//        throw new UnsupportedOperationException("Operation not possibile in DE scenarios");
    }

    @Override
    public void removeClusterId(CellRef cellRef, String stepId) {
//        throw new UnsupportedOperationException("Operation not possibile in DE scenarios");
    }

    public void reset() {
    }

    @Override
    protected void loadCacheForStep(String stepId, IDatabase deltaDB, Scenario scenario) {
        if (!stepId.equals(LunaticConstants.CHASE_STEP_ROOT) || rootCellGroupsLoaded) {
            return;
        }
        rootCellGroupsLoaded = true;
        //LOAD CACHE
        loadCellGroups(stepId, deltaDB, scenario);
        if (logger.isDebugEnabled()) logger.debug("Cache loaded...\n"
                    + "\tCellgroup max objects: " + cellGroupCache.getCacheAttributes().getMaxObjects());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getKeySet() {
        return cellGroupCache.getCacheControl().getKeySet();
    }

    @Override
    public void generateCellGroupStats(DeltaChaseStep step) {
        throw new UnsupportedOperationException("Operation not possibile in DE scenarios");
    }

    @Override
    public CellGroup getCellGroup(String key) {
        throw new UnsupportedOperationException("Operation not possibile in DE scenarios");
    }

}
