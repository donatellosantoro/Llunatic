package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.FindOriginalValuesForCellGroupCells;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.operators.IRunQuery;

public class OccurrenceHandlerDE extends OccurrenceHandlerMC {

    private final static Logger logger = LoggerFactory.getLogger(OccurrenceHandlerDE.class);
    private FindOriginalValuesForCellGroupCells cellGroupMerger = new FindOriginalValuesForCellGroupCells();

    public OccurrenceHandlerDE(IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, ICacheManager cacheManager) {
        super(queryRunner, insertOperator, deleteOperator, cacheManager);
    }

    @Override
    public CellGroup enrichCellGroups(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, Scenario scenario) {
        if (preliminaryCellGroup.getValue() instanceof LLUNValue) {
            throw new ChaseException("CellGroup with Llun value in DE");
        }
        if (preliminaryCellGroup.getValue() instanceof ConstantValue) {
            for (CellGroupCell occurrence : preliminaryCellGroup.getOccurrences()) {
                if (occurrence.getValue() instanceof NullValue) {
                    throw new ChaseException("CellGroup with ConstantValues and NullCells in DE");
                }
                occurrence.setOriginalValue(occurrence.getValue());
            }
            return preliminaryCellGroup;
        }
        if (logger.isDebugEnabled()) logger.debug("Enriching cell group: " + preliminaryCellGroup + "\n In step " + step);
        IValue value = preliminaryCellGroup.getValue();
        if (logger.isDebugEnabled()) logger.debug("Value: " + value);
        List<CellGroup> cellGroupsToMerge = new ArrayList<CellGroup>();
        cellGroupsToMerge.add(preliminaryCellGroup);
        if (logger.isDebugEnabled()) logger.debug("Searching cellgroup for llun or null " + value);
        CellGroup existingCellGroup = loadCellGroupFromId(value, deltaDB, step, scenario);
        if (existingCellGroup != null) {
            cellGroupsToMerge.add(existingCellGroup);
        }
        CellGroup mergedCG = cellGroupMerger.findOriginalValues(cellGroupsToMerge, value);
        if (logger.isDebugEnabled()) logger.debug("Merged cell group: " + mergedCG.toLongString());
        return mergedCG;
    }

    @Override
    public void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Adding new cell group " + cellGroup + " in step " + stepId);
        this.cacheManager.putCellGroup(cellGroup, stepId, deltaDB, scenario);
        for (CellGroupCell cellRef : cellGroup.getOccurrences()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cellRef, stepId, scenario);
        }
        if (logger.isTraceEnabled()) logger.trace("DeltaDB\n" + deltaDB.printInstances());
    }

    @Override
    public void saveCellGroupCell(IDatabase deltaDB, IValue groupId, CellGroupCell cell, String stepId, Scenario scenario) {
        assert (groupId != null) : "Trying to save occurrence with null groupid " + cell;
        if (groupId instanceof ConstantValue) {
            this.cacheManager.putClusterId(new CellRef(cell), groupId, stepId, deltaDB, scenario);
        }
        if (cell.isToSave() != null && !cell.isToSave()) {
            return;
        }
        if (queryRunner.isUseTrigger()) {
            //Managed by trigger
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Adding occurrence for value " + groupId + ": " + cell);
        TupleOID oid = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(oid);
        Cell stepCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, SpeedyConstants.STEP), new ConstantValue(stepId));
        Cell valueCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.GROUP_ID), groupId);
        Cell tidCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_OID), new ConstantValue(cell.getTupleOID()));
        Cell tableCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_TABLE), new ConstantValue(cell.getAttributeRef().getTableName()));
        Cell attributeCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_ATTRIBUTE), new ConstantValue(cell.getAttributeRef().getName()));
        tuple.addCell(valueCell);
        tuple.addCell(stepCell);
        tuple.addCell(tidCell);
        tuple.addCell(tableCell);
        tuple.addCell(attributeCell);
        ITable table = deltaDB.getTable(LunaticConstants.CELLGROUP_TABLE);
        insertOperator.execute(table, tuple, null, deltaDB);
    }
}
