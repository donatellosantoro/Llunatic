package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceHandlerWithCacheGreedy extends StandardOccurrenceHandlerMC {

    private static Logger logger = LoggerFactory.getLogger(OccurrenceHandlerWithCacheGreedy.class);

    private ICacheManager cacheManager;

    public OccurrenceHandlerWithCacheGreedy(ICacheManager cacheManager, IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, IUpdateCell cellUpdater) {
        super(queryRunner, insertOperator, deleteOperator, cellUpdater);
        this.cacheManager = cacheManager;
    }

    @Override
    public CellGroup loadCellGroupFromId(IValue value, IDatabase deltaDB, String stepId) {
        CellGroup cellGroup = this.cacheManager.getCellGroup(value, stepId, deltaDB);
        return cellGroup;
    }

    @Override
    public IValue findClusterId(CellRef cellRef, String stepId, IDatabase deltaDB) {
        IValue clusterId = this.cacheManager.getClusterId(cellRef, stepId, deltaDB);
        return clusterId;
    }

    @Override
    protected void addOccurrence(IDatabase deltaDB, IValue cellGroupId, CellRef cellRef, String stepId, boolean synchronizeCache) {
        if (synchronizeCache) {
            CellGroup cellGroup = this.cacheManager.getCellGroup(cellGroupId, stepId, deltaDB);
            if (cellGroup == null) {
                cellGroup = new CellGroup(cellGroupId, false);
                this.cacheManager.putCellGroup(cellGroup, stepId, deltaDB);
            }
            cellGroup.addOccurrenceCell(cellRef);
            if (cellGroupId instanceof ConstantValue) {
                this.cacheManager.putClusterId(cellRef, cellGroupId, stepId, deltaDB);
            }
        }
        super.addOccurrence(deltaDB, cellGroupId, cellRef, stepId, synchronizeCache);
    }

    @Override
    protected void addProvenance(IDatabase deltaDB, IValue cellGroupId, Cell cell, String stepId, boolean synchronizeCache) {
        if (synchronizeCache) {
            CellGroup cellGroup = this.cacheManager.getCellGroup(cellGroupId, stepId, deltaDB);
            if (cellGroup == null) {
                throw new IllegalArgumentException("Unable to add provenances in step " + stepId + " to non-existing cell group " + cellGroupId + "\nDelta DB: " + deltaDB.printInstances());
            }
            cellGroup.addProvenanceCell(cell);
        }
        super.addProvenance(deltaDB, cellGroupId, cell, stepId, synchronizeCache);
    }

    @Override
    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        if (synchronizeCache) {
            this.cacheManager.removeCellGroup(cellGroup.getValue(), stepId);
            for (CellRef cellRef : cellGroup.getOccurrences()) {
                this.cacheManager.removeClusterId(cellRef, stepId);
            }
        }
        super.deleteCellGroup(cellGroup, deltaDB, stepId, synchronizeCache);
    }

    @Override
    public void updateCellGroup(CellGroup cellGroup, IValue newId, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        if (synchronizeCache) {
            IValue oldId = cellGroup.getId();
            CellGroup oldCellGroup = this.cacheManager.getCellGroup(oldId, stepId, deltaDB);
            if (oldCellGroup != null) {
                this.cacheManager.removeCellGroup(oldId, stepId);
                if (oldId instanceof ConstantValue) {
                    for (CellRef cellRef : cellGroup.getOccurrences()) {
                        this.cacheManager.removeClusterId(cellRef, stepId);
                    }
                }
            }
            CellGroup newCellGroup = this.cacheManager.getCellGroup(newId, stepId, deltaDB);
            if (newCellGroup == null) {
                newCellGroup = cellGroup.clone();
                newCellGroup.setId(newId);
                this.cacheManager.putCellGroup(newCellGroup, stepId, deltaDB);
            } else {
                newCellGroup.getOccurrences().addAll(cellGroup.getOccurrences());
                newCellGroup.getProvenances().addAll(cellGroup.getProvenances());
            }
            if (newId instanceof ConstantValue) {
                for (CellRef cellRef : cellGroup.getOccurrences()) {
                    this.cacheManager.putClusterId(cellRef, newId, stepId, deltaDB);
                }
            }
        }
        super.updateCellGroup(cellGroup, newId, deltaDB, stepId, synchronizeCache);
    }

    @Override
    public void reset() {
        this.cacheManager.reset();
    }

    @Override
    public void generateCellGroupStats(DeltaChaseStep step) {
        this.cacheManager.generateCellGroupStats(step);
    }


}
