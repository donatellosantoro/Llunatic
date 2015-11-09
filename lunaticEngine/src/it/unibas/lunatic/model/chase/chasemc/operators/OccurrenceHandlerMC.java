package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.ITupleIterator;
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

//Assumption: CellGroup ids are consistent across steps, i.e.: if C1 belongs to cell group x at step S, 
//            it belongs to the same cell group also at step S+1
public class OccurrenceHandlerMC implements IOccurrenceHandler {

    private static Logger logger = LoggerFactory.getLogger(OccurrenceHandlerMC.class);

    private IRunQuery queryRunner;
    private IInsertTuple insertOperator;
    private IDelete deleteOperator;
    private ICacheManager cacheManager;

    private FindOriginalValuesForCellGroupCells cellGroupMerger = new FindOriginalValuesForCellGroupCells();

    public OccurrenceHandlerMC(IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, ICacheManager cacheManager) {
        this.queryRunner = queryRunner;
        this.insertOperator = insertOperator;
        this.deleteOperator = deleteOperator;
        this.cacheManager = cacheManager;
    }

    private boolean isDebug(IValue value, String stepId) {
        return false;
//        return value.toString().equals("_L137") && stepId.startsWith("r.cfd1_0_f#.cfd2_0_f#.md3_0_f#");
//        return value.toString().equals("_L137") && stepId.equals("r.cfd1_0_f#.cfd2_0_f#.md3_0_f#.cfd2_0_f#");
    }

    @Override
    public CellGroup enrichCellGroups(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Enriching cell group: " + preliminaryCellGroup + "\n In step " + step);
        IValue value = preliminaryCellGroup.getValue();
        if (logger.isDebugEnabled()) logger.debug("Value: " + value);
        List<CellGroup> cellGroupsToMerge = new ArrayList<CellGroup>();
        cellGroupsToMerge.add(preliminaryCellGroup);
        if (value instanceof NullValue || value instanceof LLUNValue) {
            if (logger.isDebugEnabled()) logger.debug("Searching cellgroup for llun or null " + value);
            addCellGroupForLLunOrNull(value, deltaDB, step, cellGroupsToMerge, scenario);
        } else {
            if (logger.isDebugEnabled()) logger.debug("Searching cellgroup for constant " + value);
            addCellGroupForConstant(preliminaryCellGroup, deltaDB, step, cellGroupsToMerge, scenario);
        }
        addOriginalValuesForAdditionalCells(preliminaryCellGroup, deltaDB, step, scenario);
        CellGroup mergedCG = cellGroupMerger.findOriginalValues(cellGroupsToMerge, value);
        if (logger.isDebugEnabled()) logger.debug("Merged cell group: " + mergedCG.toLongString());
        return mergedCG;
    }

    private CellGroup loadCellGroupFromId(IValue value, IDatabase deltaDB, String stepId, Scenario scenario) {
        CellGroup cellGroup = this.cacheManager.loadCellGroupFromId(value, stepId, deltaDB, scenario);
        if (isDebug(value, stepId)) {
            if (cellGroup != null && cellGroup.getJustifications().size() < 3) {
                throw new IllegalArgumentException("Wrong cell group " + cellGroup);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("CellGroup for id " + value + ": " + cellGroup);
        return cellGroup;
    }

    private void addCellGroupForLLunOrNull(IValue cellGroupId, IDatabase deltaDB, String step, List<CellGroup> cellGroupsToMerge, Scenario scenario) {
        CellGroup existingCellGroup = loadCellGroupFromId(cellGroupId, deltaDB, step, scenario);
        if (existingCellGroup != null) {
            cellGroupsToMerge.add(existingCellGroup);
        }
    }

    private void addCellGroupForConstant(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, List<CellGroup> cellGroupsToMerge, Scenario scenario) {
        Set<Cell> occurrenceCells = new HashSet<Cell>(preliminaryCellGroup.getOccurrences());
        for (Cell cell : occurrenceCells) {
            if (cell.getAttributeRef().getTableAlias().isSource()) {
                throw new IllegalArgumentException("Cell group to enrich contains source cells as occurrences: " + preliminaryCellGroup);
            }
            IValue cellGroupId = findClusterId(new CellRef(cell), step, deltaDB, scenario);
            if (cellGroupId != null) {
                CellGroup existingCellGroup = loadCellGroupFromId(cellGroupId, deltaDB, step, scenario);
                if (existingCellGroup != null) {
                    cellGroupsToMerge.add(existingCellGroup);
                }
            }
        }
    }

    private void addOriginalValuesForAdditionalCells(CellGroup cellGroup, IDatabase deltaDB, String step, Scenario scenario) {
        for (AttributeRef additionalAttribute : cellGroup.getAdditionalCells().keySet()) {
            Set<CellGroupCell> additionalCells = cellGroup.getAdditionalCells().get(additionalAttribute);
            for (CellGroupCell additionalCell : additionalCells) {
                IValue value = additionalCell.getValue();
                if (additionalCell.isSource()) {
                    additionalCell.setOriginalValue(value);
                    continue;
                }
                CellGroup cellGroupForAdditionalCell = findCellGroupForAdditionalCell(additionalCell, deltaDB, step, scenario);
                if (cellGroupForAdditionalCell == null) {
                    additionalCell.setOriginalValue(value);
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("DeltaDB\n" + deltaDB);
                IValue originalValue = findOriginalValueForAdditionalCell(additionalCell, cellGroupForAdditionalCell);
                additionalCell.setOriginalValue(originalValue);
            }
        }
    }

    private IValue findOriginalValueForAdditionalCell(CellGroupCell cell, CellGroup cellGroup) {
        CellRef cellRef = new CellRef(cell);
        for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
            if (cellRef.equals(new CellRef(occurrence))) {
                return occurrence.getOriginalValue();
            }
        }
        throw new IllegalArgumentException("Unable to find original value for cell " + cellRef + " in cell group\n\t" + cellGroup.toStringWithAdditionalCells());
    }

    private CellGroup findCellGroupForAdditionalCell(CellGroupCell additionalCell, IDatabase deltaDB, String step, Scenario scenario) {
        IValue value = additionalCell.getValue();
        if (value instanceof NullValue || value instanceof LLUNValue) {
            return loadCellGroupFromId(value, deltaDB, step, scenario);
        }
        IValue cellGroupId = findClusterId(new CellRef(additionalCell), step, deltaDB, scenario);
        if (cellGroupId == null) {
            return null;
        }
        if (logger.isDebugEnabled()) logger.debug("CellGroupID for additional cell " + additionalCell + ": " + cellGroupId);
        return loadCellGroupFromId(cellGroupId, deltaDB, step, scenario);
    }

    @Override
    public CellGroup loadCellGroupFromValue(Cell cell, IDatabase deltaDB, String stepId, Scenario scenario) {
        IValue cellGroupId = cell.getValue();
        if (cellGroupId instanceof ConstantValue) {
            cellGroupId = findClusterId(new CellRef(cell), stepId, deltaDB, scenario);
        }
        if (cellGroupId == null) {
            return null;
        }
        return loadCellGroupFromId(cellGroupId, deltaDB, stepId, scenario);
    }

    @Override
    public IValue findClusterId(CellRef cellRef, String stepId, IDatabase deltaDB, Scenario scenario) {
        IValue clusterId = this.cacheManager.getClusterId(cellRef, stepId, deltaDB, scenario);
        return clusterId;
    }

    @Override
    public void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Adding new cell group " + cellGroup + " in step " + stepId);
        this.cacheManager.putCellGroup(cellGroup, stepId, deltaDB, scenario);
        for (CellGroupCell cellRef : cellGroup.getOccurrences()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cellRef, stepId, scenario);
        }
        for (CellGroupCell cell : cellGroup.getJustifications()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cell, stepId, scenario);
        }
        for (CellGroupCell cell : cellGroup.getUserCells()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cell, stepId, scenario);
        }
        if (cellGroup.hasInvalidCell()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cellGroup.getInvalidCell(), stepId, scenario);
        }
        for (AttributeRef attributeRef : cellGroup.getAdditionalCells().keySet()) {
            Set<CellGroupCell> additionalCells = cellGroup.getAdditionalCells().get(attributeRef);
            for (CellGroupCell additionalCell : additionalCells) {
                saveCellGroupCell(deltaDB, cellGroup.getId(), additionalCell, stepId, scenario);
            }
        }
        if (logger.isTraceEnabled()) logger.trace("DeltaDB\n" + deltaDB.printInstances());
    }

    @Override
    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId) {
        this.cacheManager.removeCellGroup(cellGroup.getValue(), stepId);
        for (Cell cell : cellGroup.getOccurrences()) {
            this.cacheManager.removeClusterId(new CellRef(cell), stepId);
        }
        IAlgebraOperator deleteCellGroupQuery = CellGroupTableUtility.generateDeleteQuery(cellGroup, stepId, LunaticConstants.CELLGROUP_TABLE);
        deleteOperator.execute(LunaticConstants.CELLGROUP_TABLE, deleteCellGroupQuery, null, deltaDB);
    }

    @Override
    public void reset() {
        this.cacheManager.reset();
    }

    @Override
    public void generateCellGroupStats(DeltaChaseStep step) {
        this.cacheManager.generateCellGroupStats(step);
    }

    /////////////////////////////////////////////////////////////////////////   
    @Override
    public void saveCellGroupCell(IDatabase deltaDB, IValue groupId, CellGroupCell cell, String stepId, Scenario scenario) {
        assert (groupId != null) : "Trying to save occurrence with null groupid " + cell;
        String type = cell.getType();
        if (LunaticConstants.TYPE_OCCURRENCE.equals(type) && groupId instanceof ConstantValue) {
            this.cacheManager.putClusterId(new CellRef(cell), groupId, stepId, deltaDB, scenario);
        }
        if (cell.isToSave() != null && !cell.isToSave()) {
            return;
        }
        if (type.equals(LunaticConstants.TYPE_OCCURRENCE) && queryRunner.isUseTrigger()) {
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
        Cell originalValueCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_ORIGINAL_VALUE), cell.getOriginalValue());
        String typeValue = type;
        if (type.equals(LunaticConstants.TYPE_ADDITIONAL)) {
            typeValue += cell.getAttributeRef().getTableName() + "." + cell.getAttributeRef().getName();
        }
        Cell typeCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_TYPE), new ConstantValue(typeValue));
        tuple.addCell(valueCell);
        tuple.addCell(stepCell);
        tuple.addCell(tidCell);
        tuple.addCell(tableCell);
        tuple.addCell(attributeCell);
        tuple.addCell(originalValueCell);
        tuple.addCell(typeCell);
        ITable table = deltaDB.getTable(LunaticConstants.CELLGROUP_TABLE);
        insertOperator.execute(table, tuple, null, deltaDB);
    }

    /////////////////////////////////////////////////////////
    //////    DEBUGGING
    /////////////////////////////////////////////////////////
    @Override
    public List<CellGroup> loadAllCellGroupsForDebugging(IDatabase deltaDB, String stepId, Scenario scenario) {
        cacheManager.loadCellGroups(stepId, deltaDB, scenario);
        Set<String> keys = cacheManager.getKeySet();
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (String key : keys) {
            CellGroup cellGroup = cacheManager.getCellGroup(key);
            result.add(cellGroup);
        }
        return result;
    }

    @Override
    public List<CellGroup> loadAllCellGroupsInStepForDebugging(IDatabase deltaDB, String stepId, Scenario scenario) {
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToExtractCellGroupIdsInSingleStep(stepId);
        if (logger.isDebugEnabled()) logger.debug("Query for extract cell groups ids in step\n" + query);
        List<CellGroup> cellGroups = loadCellGroupsFromQueryForDebugging(query, deltaDB, stepId, scenario);
        return cellGroups;
    }

    private List<CellGroup> loadCellGroupsFromQueryForDebugging(IAlgebraOperator query, IDatabase deltaDB, String stepId, Scenario scenario) {
        List<CellGroup> result = new ArrayList<CellGroup>();
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            IValue cellId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
            CellGroup cellGroup = loadCellGroupFromId(cellId, deltaDB, stepId, scenario);
            result.add(cellGroup);
        }
        it.close();
        return result;
    }

}
