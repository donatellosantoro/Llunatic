package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceHandlerWithCacheGreedy extends Standard {

    private static Logger logger = LoggerFactory.getLogger(OccurrenceHandlerWithCacheGreedy.class);

    private ICacheManager cacheManager;

    public OccurrenceHandlerWithCacheGreedy(ICacheManager cacheManager, IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator) {
        super(queryRunner, insertOperator, deleteOperator);
        this.cacheManager = cacheManager;
    }

    @Override
    public CellGroup loadCellGroupFromId(IValue value, IDatabase deltaDB, String stepId, Scenario scenario) {
        CellGroup cellGroup = this.cacheManager.loadCellGroupFromId(value, stepId, deltaDB, scenario);
        return cellGroup;
    }

    @Override
    public IValue findClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario) {
        IValue clusterId = this.cacheManager.getClusterId(cellRef, stepId, deltaDB, scenario);
        return clusterId;
    }

    @Override
    public void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario) {
        this.cacheManager.putCellGroup(cellGroup, stepId, deltaDB, scenario);
        super.saveNewCellGroup(cellGroup, deltaDB, stepId, scenario);
    }

    @Override
    protected void saveCellGroupCell(IDatabase deltaDB, IValue groupId, CellGroupCell cell, String stepId, String type, Scenario scenario) {
        if (LunaticConstants.TYPE_OCCURRENCE.equals(type) && groupId instanceof ConstantValue) {
            this.cacheManager.putClusterId(new CellRef(cell), groupId, stepId, deltaDB, scenario);
        }
        super.saveCellGroupCell(deltaDB, groupId, cell, stepId, type, scenario);
    }

    @Override
    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId) {
        this.cacheManager.removeCellGroup(cellGroup.getValue(), stepId);
        for (Cell cell : cellGroup.getOccurrences()) {
            this.cacheManager.removeClusterId(new CellRef(cell), stepId);
        }
        super.deleteCellGroup(cellGroup, deltaDB, stepId);
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
