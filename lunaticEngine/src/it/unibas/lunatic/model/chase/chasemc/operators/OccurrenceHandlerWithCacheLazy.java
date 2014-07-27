package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.SimpleCacheManagerForLazyOccurrenceHandler;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceHandlerWithCacheLazy extends StandardOccurrenceHandlerMC {

    private static Logger logger = LoggerFactory.getLogger(OccurrenceHandlerWithCacheLazy.class);
    
    private ICacheManager cacheManager;

    public OccurrenceHandlerWithCacheLazy(ICacheManager cacheManager, IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, IUpdateCell cellUpdater) {
        super(queryRunner, insertOperator, deleteOperator, cellUpdater);
        this.cacheManager = cacheManager;
        assert (cacheManager instanceof SimpleCacheManagerForLazyOccurrenceHandler);
    }

    @Override
    public CellGroup loadCellGroupFromId(IValue cellGroupId, IDatabase deltaDB, String stepId) {
        CellGroup cellGroup = this.cacheManager.getCellGroup(cellGroupId, stepId, deltaDB);
        if (cellGroup != null) {
            return cellGroup;
        }
        cellGroup = super.loadCellGroupFromId(cellGroupId, deltaDB, stepId);
        this.cacheManager.putCellGroup(cellGroup, stepId, deltaDB);
        return cellGroup;
    }

    @Override
    public IValue findClusterId(CellRef cellRef, String stepId, IDatabase deltaDB) {
        IValue value = this.cacheManager.getClusterId(cellRef, stepId, deltaDB);
        if (value != null) {
            return value;
        }
        value = super.findClusterId(cellRef, stepId, deltaDB);
        this.cacheManager.putClusterId(cellRef, value, stepId, deltaDB);
        return value;
    }

    @Override
    protected void addProvenance(IDatabase deltaDB, IValue value, Cell cell, String stepId, boolean synchronizeCache) {
        CellGroup cellGroup = this.cacheManager.getCellGroup(value, stepId, deltaDB);
        if (cellGroup != null) {
            cellGroup.addProvenanceCell(cell);
        }
        super.addProvenance(deltaDB, value, cell, stepId, synchronizeCache);
    }

    @Override
    protected void addOccurrence(IDatabase deltaDB, IValue value, CellRef cellRef, String stepId, boolean synchronizeCache) {
        CellGroup cellGroup = this.cacheManager.getCellGroup(value, stepId, deltaDB);
        if (cellGroup != null) {
            cellGroup.addOccurrenceCell(cellRef);
        }
        if (value instanceof ConstantValue) {
            this.cacheManager.putClusterId(cellRef, value, stepId, deltaDB);
        }
        super.addOccurrence(deltaDB, value, cellRef, stepId, synchronizeCache);
    }

    @Override
    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        this.cacheManager.removeCellGroup(cellGroup.getValue(), stepId);
        if (cellGroup.getValue() instanceof ConstantValue) {
            for (CellRef cellRef : cellGroup.getOccurrences()) {
                this.cacheManager.removeClusterId(cellRef, stepId);
            }
        }
        super.deleteCellGroup(cellGroup, deltaDB, stepId, synchronizeCache);
    }

    @Override
    public void updateCellGroup(CellGroup cellGroup, IValue newValue, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        CellGroup oldCellGroup = this.cacheManager.getCellGroup(cellGroup.getValue(), stepId, deltaDB);
        IValue oldValue = cellGroup.getValue();
        if (oldCellGroup != null) {
            this.cacheManager.removeCellGroup(oldValue, stepId);
            if (oldValue instanceof ConstantValue) {
                for (CellRef cellRef : cellGroup.getOccurrences()) {
                    this.cacheManager.removeClusterId(cellRef, stepId);
                }
            }
        }
        CellGroup newCellGroup = this.cacheManager.getCellGroup(newValue, stepId, deltaDB);
        if (newCellGroup == null) {
            newCellGroup = cellGroup.clone();
            newCellGroup.setValue(newValue);
            this.cacheManager.putCellGroup(newCellGroup, stepId, deltaDB);
        } else {
            newCellGroup.getOccurrences().addAll(cellGroup.getOccurrences());
            newCellGroup.getProvenances().addAll(cellGroup.getProvenances());
        }
        if (newValue instanceof ConstantValue) {
            for (CellRef cellRef : cellGroup.getOccurrences()) {
                this.cacheManager.putClusterId(cellRef, newValue, stepId, deltaDB);
            }
        }
        super.updateCellGroup(cellGroup, newValue, deltaDB, stepId, synchronizeCache);
    }

    @Override
    public void reset() {
        this.cacheManager.reset();
    }
}
