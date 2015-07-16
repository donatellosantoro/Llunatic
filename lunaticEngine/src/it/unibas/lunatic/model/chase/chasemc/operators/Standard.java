package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.Distinct;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Project;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.algebra.Select;
import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.expressions.Expression;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Standard implements IValueOccurrenceHandlerMC {

    private static Logger logger = LoggerFactory.getLogger(Standard.class);

    private IRunQuery queryRunner;
    private IInsertTuple insertOperator;
    private IDelete deleteOperator;

    public Standard(IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator) {
        this.queryRunner = queryRunner;
        this.insertOperator = insertOperator;
        this.deleteOperator = deleteOperator;
    }

    @Override
    public CellGroup loadCellGroupFromId(IValue cellGroupId, IDatabase deltaDB, String stepId, Scenario scenario) {
        CellGroup cellGroup = new CellGroup(cellGroupId, false);
        IAlgebraOperator select = buildQuery(cellGroupId, stepId);
        ITupleIterator resultTuples = queryRunner.run(select, null, deltaDB);
        Map<String, Set<CellGroupCell>> cellMap = extractCellsWithType(cellGroupId, resultTuples, scenario);
        resultTuples.close();
        cellGroup.setOccurrences(cellMap.get(LunaticConstants.TYPE_OCCURRENCE));
        cellGroup.setJustifications(cellMap.get(LunaticConstants.TYPE_JUSTIFICATION));
        cellGroup.setUserCells(cellMap.get(LunaticConstants.TYPE_USER));
        cellGroup.setInvalidCell((cellMap.get(LunaticConstants.TYPE_INVALID).isEmpty() ? null : LunaticConstants.INVALID_CELL));
        addAdditionalCell(cellGroup, cellMap);
        return cellGroup;
    }

    private Map<String, Set<CellGroupCell>> extractCellsWithType(IValue cellGroupId, ITupleIterator resultTuples, Scenario scenario) {
        Map<String, Set<CellGroupCell>> result = new HashMap<String, Set<CellGroupCell>>();
        result.put(LunaticConstants.TYPE_OCCURRENCE, new HashSet<CellGroupCell>());
        result.put(LunaticConstants.TYPE_JUSTIFICATION, new HashSet<CellGroupCell>());
        result.put(LunaticConstants.TYPE_USER, new HashSet<CellGroupCell>());
        result.put(LunaticConstants.TYPE_INVALID, new HashSet<CellGroupCell>());
        while (resultTuples.hasNext()) {
            Tuple tuple = resultTuples.next();
            TupleOID tid = new TupleOID(LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_OID));
            String table = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TABLE) + "";
            String attribute = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ATTRIBUTE) + "";
            CellRef cellRef = new CellRef(tid, new AttributeRef(table, attribute));
            String type = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TYPE) + "";
            if (type.equals(LunaticConstants.TYPE_JUSTIFICATION)) {
                TableAlias tableAlias = cellRef.getAttributeRef().getTableAlias();
                tableAlias.setSource(true);
                tableAlias.setAuthoritative(LunaticUtility.isAuthoritative(tableAlias.getTableName(), scenario));
            }
            IValue originalValue = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ORIGINAL_VALUE);
            IValue cellGroupValue = CellGroupIDGenerator.getCellGroupValueFromGroupID(cellGroupId);
            CellGroupCell cell = new CellGroupCell(cellRef, cellGroupValue, originalValue, type, false);
            cell.setOriginalCellGroupId(cellGroupId);
            Set<CellGroupCell> cellForType = getCellForType(result, type);
            cellForType.add(cell);
        }
        return result;
    }

    private void addAdditionalCell(CellGroup cellGroup, Map<String, Set<CellGroupCell>> cellMap) {
        Map<AttributeRef, Set<CellGroupCell>> cellsByAttributeRef = new HashMap<AttributeRef, Set<CellGroupCell>>();
        for (String key : cellMap.keySet()) {
            if (!key.startsWith(LunaticConstants.TYPE_ADDITIONAL)) {
                continue;
            }
            AttributeRef attributeRef = ChaseUtility.extractAttributeRef(key);
            cellsByAttributeRef.put(attributeRef, cellMap.get(key));
        }
        cellGroup.addAllAdditionalCells(cellsByAttributeRef);
    }

    private Set<CellGroupCell> getCellForType(Map<String, Set<CellGroupCell>> cellsMap, String type) {
        Set<CellGroupCell> result = cellsMap.get(type);
        if (result == null) {
            result = new HashSet<CellGroupCell>();
            cellsMap.put(type, result);
        }
        return result;
    }

    @Override
    public CellGroup enrichCellGroups(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Searching occurrences and provenances for cell group: " + preliminaryCellGroup);
        IValue value = preliminaryCellGroup.getValue();
        if (logger.isDebugEnabled()) logger.debug("Value: " + value);
        List<CellGroup> cellGroupsToMerge = new ArrayList<CellGroup>();
        cellGroupsToMerge.add(preliminaryCellGroup);
        if (value instanceof NullValue || value instanceof LLUNValue) {
            addCellGroupForLLunOrNull(value, deltaDB, step, cellGroupsToMerge, scenario);
        } else {
            addCellGroupForConstant(preliminaryCellGroup, deltaDB, step, cellGroupsToMerge, scenario);
        }
        addOriginalValuesForAdditionalCells(preliminaryCellGroup, deltaDB, step, scenario);
        MergeCellGroup cellGroupMerger = new MergeCellGroup();
        CellGroup mergedCG = cellGroupMerger.mergeCells(cellGroupsToMerge, value);
        if (logger.isDebugEnabled()) logger.debug("Merged cell group: " + mergedCG);
        return mergedCG;
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
                IValue originalValue = findOriginalValueForAdditionalCell(additionalCell, cellGroupForAdditionalCell);
                additionalCell.setOriginalValue(originalValue);
            }
        }
    }

    private IValue findOriginalValueForAdditionalCell(CellGroupCell cell, CellGroup cellGroup) {
        CellRef cellRef = new CellRef(cell);
        for (CellGroupCell occurrence : cellGroup.getAllCells()) {
            if (cellRef.equals(new CellRef(occurrence))) {
                return occurrence.getOriginalValue();
            }
        }
        throw new IllegalArgumentException("Unable to find original value for cell " + cellRef + " in cell group\n\t" + cellGroup);
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
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToFindCellGroupFromCellRef(cellRef, stepId);
        if (logger.isDebugEnabled()) logger.debug("Query to extract cell value:\n" + query);
        ITupleIterator result = queryRunner.run(query, null, deltaDB);
        if (!result.hasNext()) {
            if (logger.isDebugEnabled()) logger.debug("Query result is empty, returning null...");
            result.close();
            return null;
        }
        Tuple firstTuple = result.next();
        TableAlias alias = new TableAlias(LunaticConstants.CELLGROUP_TABLE, "1");
        AttributeRef valueInAlias = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        IValue occurrenceValue = firstTuple.getCell(valueInAlias).getValue();
        if (logger.isDebugEnabled()) logger.debug("Result: " + occurrenceValue);
        result.close();
        return occurrenceValue;
    }

    @Override
    public void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Adding new cell group " + cellGroup);
        for (CellGroupCell cellRef : cellGroup.getOccurrences()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cellRef, stepId, LunaticConstants.TYPE_OCCURRENCE, scenario);
        }
        for (CellGroupCell cell : cellGroup.getJustifications()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cell, stepId, LunaticConstants.TYPE_JUSTIFICATION, scenario);
        }
        for (CellGroupCell cell : cellGroup.getUserCells()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), cell, stepId, LunaticConstants.TYPE_USER, scenario);
        }
        if (cellGroup.hasInvalidCell()) {
            saveCellGroupCell(deltaDB, cellGroup.getId(), LunaticConstants.INVALID_CELL, stepId, LunaticConstants.TYPE_INVALID, scenario);
        }
        for (AttributeRef attributeRef : cellGroup.getAdditionalCells().keySet()) {
            Set<CellGroupCell> additionalCells = cellGroup.getAdditionalCells().get(attributeRef);
            for (CellGroupCell additionalCell : additionalCells) {
                saveCellGroupCell(deltaDB, cellGroup.getId(), additionalCell, stepId, LunaticConstants.TYPE_ADDITIONAL, scenario);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("DeltaDB\n" + deltaDB.printInstances());
    }

    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId) {
        IAlgebraOperator deleteCellGroupQuery = generateDeleteQuery(cellGroup, stepId, LunaticConstants.CELLGROUP_TABLE);
        deleteOperator.execute(LunaticConstants.CELLGROUP_TABLE, deleteCellGroupQuery, null, deltaDB);
    }

    public void updateOccurrencesForNewTuple(Tuple tuple, IValue occurrenceValue, IDatabase deltaDB, String tableName, String attributeName) {
        //TODO++ (TGD)
        throw new UnsupportedOperationException();
//        IValue tid = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.TID);
//        String stepId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.STEP).toString();
//        CellRef cellRef = new CellRef(new TupleOID(tid), new AttributeRef(tableName, attributeName));
//        addOccurrence(deltaDB, occurrenceValue, cellRef, stepId, forceSave);
    }

    public void reset() {
    }

    public void generateCellGroupStats(DeltaChaseStep step) {
    }

    /////////////////////////////////////////////////////////////////////////   
    protected void saveCellGroupCell(IDatabase deltaDB, IValue groupId, CellGroupCell cell, String stepId, String type, Scenario scenario) {
        assert (groupId != null) : "Trying to save occurrence with null groupid " + cell;
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
        Cell stepCell = new Cell(oid, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.STEP), new ConstantValue(stepId));
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

    private IAlgebraOperator generateDeleteQuery(CellGroup cellGroup, String stepId, String tableName) {
        TableAlias table = new TableAlias(tableName);
        Scan scan = new Scan(table);
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression(LunaticConstants.STEP + " == \"" + stepId + "\"");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
//        Expression valueExpression = new Expression(LunaticConstants.CELL_ORIGINAL_VALUE + " == \"" + cellGroup.getCellGroupValueFromGroupID() + "\"");
        Expression valueExpression = new Expression(LunaticConstants.GROUP_ID + " == \"" + cellGroup.getId() + "\"");
        valueExpression.changeVariableDescription(LunaticConstants.GROUP_ID, new AttributeRef(table, LunaticConstants.GROUP_ID));
        selections.add(stepExpression);
        selections.add(valueExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        return select;
    }

    private IAlgebraOperator buildQuery(IValue value, String stepId) {
        assert (value != null) : "Trying to build query for cell group value null...";
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan scan = new Scan(table);
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        selections.add(stepExpression);
        Expression valueExpression = new Expression(LunaticConstants.GROUP_ID + " == \"" + value + "\"");
        valueExpression.changeVariableDescription(LunaticConstants.GROUP_ID, new AttributeRef(table, LunaticConstants.GROUP_ID));
        selections.add(valueExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        return select;
    }

    private IAlgebraOperator buildQueryToExtractCellGroupIdsInSingleStep(String stepId) {
        // select value from table where step
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan tableScan = new Scan(table);
        Expression stepExpression;
        stepExpression = new Expression("\"" + stepId + "\" == " + LunaticConstants.STEP);
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        AttributeRef cellValue = new AttributeRef(table, LunaticConstants.GROUP_ID);
        Project project = new Project(Arrays.asList(new AttributeRef[]{cellValue}));
        project.addChild(stepSelect);
        Distinct distinct = new Distinct();
        distinct.addChild(project);
        return distinct;
    }

    /////////////////////////////////////////////////////////
    //////    DEBUGGING
    /////////////////////////////////////////////////////////
    @Override
    public List<CellGroup> loadAllCellGroupsForDebugging(IDatabase deltaDB, String stepId, Scenario scenario) {
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToExtractCellGroupIds(stepId);
        if (logger.isDebugEnabled()) logger.debug("Query for extract cell groups ids\n" + query);
        List<CellGroup> cellGroups = loadCellGroupsFromQueryForDebugging(query, deltaDB, stepId, scenario);
        return cellGroups;
    }

    @Override
    public List<CellGroup> loadAllCellGroupsInStepForDebugging(IDatabase deltaDB, String stepId, Scenario scenario) {
        IAlgebraOperator query = buildQueryToExtractCellGroupIdsInSingleStep(stepId);
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
