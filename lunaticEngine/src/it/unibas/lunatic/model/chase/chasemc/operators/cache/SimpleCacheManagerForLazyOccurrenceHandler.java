package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import java.util.HashMap;
import java.util.Map;

public class SimpleCacheManagerForLazyOccurrenceHandler implements ICacheManager {

    private Map<String, CellGroup> cellGroupCache = new HashMap<String, CellGroup>();
    private Map<String, IValue> clusterIdCache = new HashMap<String, IValue>();

    public CellGroup getCellGroup(IValue value, String stepId, IDatabase deltaDB) {
        String key = buildKey(value, stepId);
        return cellGroupCache.get(key);
    }

    public void putCellGroup(CellGroup cellGroup, String stepId, IDatabase deltaDB) {
        String key = buildKey(cellGroup.getValue(), stepId);
        cellGroupCache.put(key, cellGroup);
    }

    public void removeCellGroup(IValue value, String stepId) {
        String key = buildKey(value, stepId);
        this.cellGroupCache.remove(key);
    }

    public IValue getClusterId(CellRef cellRef, String stepId, IDatabase deltaDB) {
        String key = buildKey(cellRef, stepId);
        return clusterIdCache.get(key);
    }

    public void putClusterId(CellRef cellRef, IValue value, String stepId, IDatabase deltaDB) {
        String key = buildKey(cellRef, stepId);
        this.clusterIdCache.put(key, value);
    }

    public void removeClusterId(CellRef cellRef, String stepId) {
        String key = buildKey(cellRef, stepId);
        this.clusterIdCache.remove(key);
    }

    private String buildKey(Object value, String stepId) {
        return stepId + "#" + value.toString();
    }

    public void reset() {
    }

    public void generateCellGroupStats(DeltaChaseStep step) {
    }
}
