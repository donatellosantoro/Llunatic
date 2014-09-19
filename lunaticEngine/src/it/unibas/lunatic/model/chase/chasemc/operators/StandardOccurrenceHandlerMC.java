package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.algebra.Distinct;
import it.unibas.lunatic.model.algebra.GroupBy;
import it.unibas.lunatic.model.algebra.IAggregateFunction;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Join;
import it.unibas.lunatic.model.algebra.MaxAggregateFunction;
import it.unibas.lunatic.model.algebra.Project;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.algebra.Select;
import it.unibas.lunatic.model.algebra.ValueAggregateFunction;
import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardOccurrenceHandlerMC implements IValueOccurrenceHandlerMC {

    private static Logger logger = LoggerFactory.getLogger(StandardOccurrenceHandlerMC.class);

    private IRunQuery queryRunner;
    private IInsertTuple insertOperator;
    private IDelete deleteOperator;
    private IUpdateCell cellUpdater;

    public StandardOccurrenceHandlerMC(IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, IUpdateCell cellUpdater) {
        this.queryRunner = queryRunner;
        this.insertOperator = insertOperator;
        this.deleteOperator = deleteOperator;
        this.cellUpdater = cellUpdater;
    }

    public CellGroup loadCellGroupFromId(IValue cellGroupId, IDatabase deltaDB, String step) {
        CellGroup cellGroup = new CellGroup(cellGroupId, false);
        cellGroup.setOccurrences(getOccurrences(deltaDB, cellGroupId, step));
        cellGroup.setProvenances(getProvenances(deltaDB, cellGroupId, step));
        return cellGroup;
    }

    public CellGroup loadCellGroupFromValue(IValue value, CellRef cellRef, IDatabase deltaDB, String stepId) {
        IValue cellGroupId = value;
        if (value instanceof ConstantValue) {
            cellGroupId = findClusterId(cellRef, stepId, deltaDB);
        }
        if (cellGroupId == null) {
            return null;
        }
        return loadCellGroupFromId(cellGroupId, deltaDB, stepId);
    }

    public IValue findClusterId(CellRef cell, String stepId, IDatabase deltaDB) {
        return findClusterIdInTable(LunaticConstants.OCCURRENCE_TABLE, cell, stepId, deltaDB);
    }

    public void saveNewCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean forceSave) {
        if (logger.isDebugEnabled()) logger.debug("Adding new cell group " + cellGroup);
        for (CellRef cellRef : cellGroup.getOccurrences()) {
//            addOccurrence(deltaDB, cellGroup.getValue(), cellRef, stepId);
            addOccurrence(deltaDB, cellGroup.getId(), cellRef, stepId, forceSave);
        }
        for (Cell cell : cellGroup.getProvenances()) {
//            addProvenance(deltaDB, cellGroup.getValue(), cell, stepId);
            addProvenance(deltaDB, cellGroup.getId(), cell, stepId, forceSave);
        }
    }

    public void deleteCellGroup(CellGroup cellGroup, IDatabase deltaDB, String stepId, boolean forceSave) {
//        if (!queryRunner.isUseTrigger()) {
        IAlgebraOperator deleteOccurrences = generateDeleteQuery(cellGroup, stepId, LunaticConstants.OCCURRENCE_TABLE);
        deleteOperator.execute(LunaticConstants.OCCURRENCE_TABLE, deleteOccurrences, null, deltaDB);
//        }
        IAlgebraOperator deleteProvenances = generateDeleteQuery(cellGroup, stepId, LunaticConstants.PROVENANCE_TABLE);
        deleteOperator.execute(LunaticConstants.PROVENANCE_TABLE, deleteProvenances, null, deltaDB);
    }

    public void enrichOccurrencesAndProvenances(CellGroup cellGroup, IDatabase deltaDB, String step) {
        if (logger.isDebugEnabled()) logger.debug("Searching occurrences and provenances for cell group: " + cellGroup);
        IValue value = cellGroup.getValue();
        if (logger.isDebugEnabled()) logger.debug("Value: " + value);
        if (value instanceof NullValue || value instanceof LLUNValue) {
            IValue cellGroupId = value;
            CellGroup existingCellGroup = loadCellGroupFromId(cellGroupId, deltaDB, step);
            mergeCellGroups(existingCellGroup, cellGroup);
        } else {
            Set<CellRef> occurrenceCells = new HashSet<CellRef>(cellGroup.getOccurrences());
            for (CellRef cell : occurrenceCells) {
                if (cell.getAttributeRef().getTableAlias().isSource()) {
                    throw new IllegalArgumentException("Cell group to enrich contains source cells as occurrences: " + cellGroup);
                }
//                if (cell.getAttributeRef().getTableAlias().isSource()) {
//                    cellGroup.getOccurrences().remove(cell);
//                    cellGroup.getProvenances().add(new Cell(cell, id));
//                }
                IValue cellGroupId = findClusterId(cell, step, deltaDB);
                if (cellGroupId != null) {
                    CellGroup existingCellGroup = loadCellGroupFromId(cellGroupId, deltaDB, step);
                    mergeCellGroups(existingCellGroup, cellGroup);
                }
            }
        }
    }

    public void updateCellGroup(CellGroup cellGroup, IValue newId, IDatabase deltaDB, String stepId, boolean forceSave) {
        for (CellRef cellRef : cellGroup.getOccurrences()) {
            IAlgebraOperator query = generateUpdateQuery(cellRef, LunaticConstants.OCCURRENCE_TABLE, stepId);
            executeUpdate(query, newId, LunaticConstants.OCCURRENCE_TABLE, deltaDB);
        }
        for (Cell cell : cellGroup.getProvenances()) {
            IAlgebraOperator query = generateUpdateQuery(new CellRef(cell), LunaticConstants.PROVENANCE_TABLE, stepId);
            executeUpdate(query, newId, LunaticConstants.OCCURRENCE_TABLE, deltaDB);
        }
    }

    public void updateOccurrencesForNewTuple(Tuple tuple, IValue occurrenceValue, IDatabase deltaDB, String tableName, String attributeName, boolean forceSave) {
        IValue tid = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.TID);
        String stepId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.STEP).toString();
        CellRef cellRef = new CellRef(new TupleOID(tid), new AttributeRef(tableName, attributeName));
        addOccurrence(deltaDB, occurrenceValue, cellRef, stepId, forceSave);
    }

    public List<CellGroup> loadAllCellGroups(IDatabase deltaDB, String stepId) {
        IAlgebraOperator query = buildTreeForExtractCellGroupIds(LunaticConstants.OCCURRENCE_TABLE, stepId);
        if (logger.isDebugEnabled()) logger.debug("Query for extract cell groups ids\n" + query);
        List<CellGroup> cellGroups = loadCellGroupsFromQuery(query, deltaDB, stepId);
        return cellGroups;
    }

    public List<CellGroup> loadAllCellGroupsInStep(IDatabase deltaDB, String stepId) {
        IAlgebraOperator query = buildTreeForExtractCellGroupIdsInSigleStep(LunaticConstants.OCCURRENCE_TABLE, stepId);
        if (logger.isDebugEnabled()) logger.debug("Query for extract cell groups ids in step\n" + query);
        List<CellGroup> cellGroups = loadCellGroupsFromQuery(query, deltaDB, stepId);
        return cellGroups;
    }

    public void reset() {
    }

    public void generateCellGroupStats(DeltaChaseStep step) {
    }

    /////////////////////////////////////////////////////////////////////////   
    protected void addOccurrence(IDatabase deltaDB, IValue groupId, CellRef cellRef, String stepId, boolean forceSave) {
        assert (groupId != null) : "Trying to save occurrence with null groupid " + cellRef;
//        if (queryRunner.isUseTrigger() && ((groupId instanceof LLUNValue) || (groupId instanceof NullValue))) {
        if (!forceSave && queryRunner.isUseTrigger()) {
//        if (queryRunner.isUseTrigger( )) {
            //Managed by trigger
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Adding occurrence for value " + groupId + ": " + cellRef);
        TupleOID oid = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(oid);
        Cell stepCell = new Cell(oid, new AttributeRef(LunaticConstants.OCCURRENCE_TABLE, LunaticConstants.STEP), new ConstantValue(stepId));
        Cell valueCell = new Cell(oid, new AttributeRef(LunaticConstants.OCCURRENCE_TABLE, LunaticConstants.GROUP_ID), groupId);
        Cell tidCell = new Cell(oid, new AttributeRef(LunaticConstants.OCCURRENCE_TABLE, LunaticConstants.CELL_OID), new ConstantValue(cellRef.getTupleOID()));
        Cell tableCell = new Cell(oid, new AttributeRef(LunaticConstants.OCCURRENCE_TABLE, LunaticConstants.CELL_TABLE), new ConstantValue(cellRef.getAttributeRef().getTableName()));
        Cell attributeCell = new Cell(oid, new AttributeRef(LunaticConstants.OCCURRENCE_TABLE, LunaticConstants.CELL_ATTRIBUTE), new ConstantValue(cellRef.getAttributeRef().getName()));
        tuple.addCell(valueCell);
        tuple.addCell(stepCell);
        tuple.addCell(tidCell);
        tuple.addCell(tableCell);
        tuple.addCell(attributeCell);
        ITable table = deltaDB.getTable(LunaticConstants.OCCURRENCE_TABLE);
        insertOperator.execute(table, tuple);
    }

    protected void addProvenance(IDatabase deltaDB, IValue groupId, Cell cell, String stepId, boolean synchronizeCache) {
        assert (groupId != null) : "Trying to save provenance with null groupid " + cell;
        TupleOID oid = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(oid);
        Cell stepCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.STEP), new ConstantValue(stepId));
        Cell valueCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.GROUP_ID), groupId);
        Cell tidCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.CELL_OID), new ConstantValue(cell.getTupleOID()));
        Cell tableCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.CELL_TABLE), new ConstantValue(cell.getAttributeRef().getTableName()));
        Cell attributeCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.CELL_ATTRIBUTE), new ConstantValue(cell.getAttributeRef().getName()));
        Cell occurrenceValueCell = new Cell(oid, new AttributeRef(LunaticConstants.PROVENANCE_TABLE, LunaticConstants.PROVENANCE_CELL_VALUE), new ConstantValue(cell.getValue()));
        tuple.addCell(valueCell);
        tuple.addCell(stepCell);
        tuple.addCell(tidCell);
        tuple.addCell(tableCell);
        tuple.addCell(attributeCell);
        tuple.addCell(occurrenceValueCell);
        ITable table = deltaDB.getTable(LunaticConstants.PROVENANCE_TABLE);
        insertOperator.execute(table, tuple);
    }

    /////////////////////////////////////////////////////////////////////////
    private IValue findClusterIdInTable(String table, CellRef cell, String stepId, IDatabase deltaDB) {
        Scan scan = new Scan(new TableAlias(table));
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        Expression oidExpression = new Expression(LunaticConstants.CELL_OID + " == \"" + cell.getTupleOID() + "\"");
        oidExpression.changeVariableDescription(LunaticConstants.CELL_OID, new AttributeRef(table, LunaticConstants.CELL_OID));
        Expression tableExpression = new Expression(LunaticConstants.CELL_TABLE + " == \"" + cell.getAttributeRef().getTableName() + "\"");
        tableExpression.changeVariableDescription(LunaticConstants.CELL_TABLE, new AttributeRef(table, LunaticConstants.CELL_TABLE));
        Expression attributeExpression = new Expression(LunaticConstants.CELL_ATTRIBUTE + " == \"" + cell.getAttributeRef().getName() + "\"");
        attributeExpression.changeVariableDescription(LunaticConstants.CELL_ATTRIBUTE, new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE));
        selections.add(stepExpression);
        selections.add(oidExpression);
        selections.add(tableExpression);
        selections.add(attributeExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        // select max(step), oid, tableName, attribute from R_A group by oid
        AttributeRef oid = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef tableName = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef attribute = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef step = new AttributeRef(table, LunaticConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, tableName, attribute}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
        IAggregateFunction tableValue = new ValueAggregateFunction(tableName);
        IAggregateFunction attributeValue = new ValueAggregateFunction(attribute);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue, tableValue, attributeValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(select);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table, "1");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on step, oid, table, attribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, oid, tableName, attribute}));
        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
        AttributeRef tableNameInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
        AttributeRef attributeInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, oidInAlias, tableNameInAlias, attributeInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        // select value from (join)
        AttributeRef valueInAlias = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{valueInAlias}));
        Project project = new Project(projectionAttributes);
        project.addChild(join);
        if (logger.isDebugEnabled()) logger.debug("Query to extract cell value:\n" + project);
        ITupleIterator result = queryRunner.run(project, null, deltaDB);
        if (!result.hasNext()) {
            if (logger.isDebugEnabled()) logger.debug("Query result is empty, returning null...");
            result.close();
            return null;
        }
        Tuple firstTuple = result.next();
        IValue occurrenceValue = firstTuple.getCell(valueInAlias).getValue();
        if (logger.isDebugEnabled()) logger.debug("Result: " + occurrenceValue);
        result.close();
        return occurrenceValue;
    }

    private Set<CellRef> getOccurrences(IDatabase deltaDB, IValue value, String stepId) {
        IAlgebraOperator select = buildQuery(LunaticConstants.OCCURRENCE_TABLE, value, stepId);
        ITupleIterator resultTuples = queryRunner.run(select, null, deltaDB);
        Set<CellRef> result = new HashSet<CellRef>();
        while (resultTuples.hasNext()) {
            Tuple tuple = resultTuples.next();
            CellRef cellRef = buildCellRef(tuple);
            result.add(cellRef);
        }
        resultTuples.close();
        return result;
    }

    private Set<Cell> getProvenances(IDatabase deltaDB, IValue value, String stepId) {
        IAlgebraOperator select = buildQuery(LunaticConstants.PROVENANCE_TABLE, value, stepId);
        ITupleIterator resultTuples = queryRunner.run(select, null, deltaDB);
        Set<Cell> result = new HashSet<Cell>();
        while (resultTuples.hasNext()) {
            Tuple tuple = resultTuples.next();
            CellRef cellRef = buildCellRef(tuple);
            cellRef.getAttributeRef().getTableAlias().setSource(true);
            IValue cellValue = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.PROVENANCE_CELL_VALUE);
            Cell cell = new Cell(cellRef, cellValue);
            result.add(cell);
        }
        resultTuples.close();
        return result;
    }

    private IAlgebraOperator generateDeleteQuery(CellGroup cellGroup, String stepId, String tableName) {
        TableAlias table = new TableAlias(tableName);
        Scan scan = new Scan(table);
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression(LunaticConstants.STEP + " == \"" + stepId + "\"");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
//        Expression valueExpression = new Expression(LunaticConstants.CELL_VALUE + " == \"" + cellGroup.getValue() + "\"");
        Expression valueExpression = new Expression(LunaticConstants.GROUP_ID + " == \"" + cellGroup.getId() + "\"");
        valueExpression.changeVariableDescription(LunaticConstants.GROUP_ID, new AttributeRef(table, LunaticConstants.GROUP_ID));
        selections.add(stepExpression);
        selections.add(valueExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        return select;
    }

    private void mergeCellGroups(CellGroup src, CellGroup dest) {
        if (src == null) {
            return;
        }
        for (CellRef cellRef : src.getOccurrences()) {
            dest.addOccurrenceCell(cellRef);
        }
        for (Cell cell : src.getProvenances()) {
            dest.getProvenances().add(cell);
        }
    }

    private IAlgebraOperator buildQuery(String tableName, IValue value, String stepId) {
        assert (value != null) : "Trying to build query for cell group value null...";
        TableAlias table = new TableAlias(tableName);
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

    private CellRef buildCellRef(Tuple tuple) {
        TupleOID tid = new TupleOID(LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_OID));
        String table = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TABLE) + "";
        String attribute = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ATTRIBUTE) + "";
        CellRef cellRef = new CellRef(tid, new AttributeRef(table, attribute));
        return cellRef;
    }

    private IAlgebraOperator generateUpdateQuery(CellRef cellRef, String table, String stepId) {
        TableAlias tableAlias = new TableAlias(table);
        Scan scan = new Scan(tableAlias);
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression(LunaticConstants.STEP + " == \"" + stepId + "\"");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(tableAlias, LunaticConstants.STEP));
        Expression oidExpression = new Expression(LunaticConstants.CELL_OID + " == \"" + cellRef.getTupleOID() + "\"");
        oidExpression.changeVariableDescription(LunaticConstants.CELL_OID, new AttributeRef(tableAlias, LunaticConstants.CELL_OID));
        Expression tableExpression = new Expression(LunaticConstants.CELL_TABLE + " == \"" + cellRef.getAttributeRef().getTableName() + "\"");
        tableExpression.changeVariableDescription(LunaticConstants.CELL_TABLE, new AttributeRef(tableAlias, LunaticConstants.CELL_TABLE));
        Expression attributeExpression = new Expression(LunaticConstants.CELL_ATTRIBUTE + " == \"" + cellRef.getAttributeRef().getName() + "\"");
        attributeExpression.changeVariableDescription(LunaticConstants.CELL_ATTRIBUTE, new AttributeRef(tableAlias, LunaticConstants.CELL_ATTRIBUTE));
        selections.add(stepExpression);
        selections.add(oidExpression);
        selections.add(tableExpression);
        selections.add(attributeExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        AttributeRef attributeRefOID = new AttributeRef(table, LunaticConstants.OID);
        Project oidProject = new Project(Arrays.asList(new AttributeRef[]{attributeRefOID}), Arrays.asList(new AttributeRef[]{attributeRefOID}), false);
        oidProject.addChild(select);
        return oidProject;
    }

    private void executeUpdate(IAlgebraOperator query, IValue newId, String table, IDatabase deltaDB) {
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            TupleOID tupleOID = tuple.getOid();
            CellRef cellValue = new CellRef(tupleOID, new AttributeRef(table, LunaticConstants.GROUP_ID));
            cellUpdater.execute(cellValue, newId, deltaDB);
        }
        it.close();
    }

    private List<CellGroup> loadCellGroupsFromQuery(IAlgebraOperator query, IDatabase deltaDB, String stepId) {
        List<CellGroup> result = new ArrayList<CellGroup>();
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            IValue cellId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
            CellGroup cellGroup = loadCellGroupFromId(cellId, deltaDB, stepId);
            result.add(cellGroup);
        }
        it.close();
        return result;
    }

    private IAlgebraOperator buildTreeForExtractCellGroupIdsInSigleStep(String tableName, String stepId) {
        // select value from table where step
        TableAlias table = new TableAlias(tableName);
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

    private IAlgebraOperator buildTreeForExtractCellGroupIds(String tableName, String stepId) {
        // select * from R_A where step
        TableAlias table = new TableAlias(tableName);
        Scan tableScan = new Scan(table);
        Expression stepExpression;
        stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef cellOid = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef cellTable = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef cellAttr = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef step = new AttributeRef(table, LunaticConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{cellOid, cellTable, cellAttr}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(cellOid);
        IAggregateFunction tableValue = new ValueAggregateFunction(cellTable);
        IAggregateFunction attributeValue = new ValueAggregateFunction(cellAttr);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue, tableValue, attributeValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on step, celloid, celltable, cellattribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, cellOid, cellTable, cellAttr}));
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
        AttributeRef tableInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
        AttributeRef attrInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, oidInAlias, tableInAlias, attrInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        AttributeRef cellValue = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        Project project = new Project(Arrays.asList(new AttributeRef[]{cellValue}));
        project.addChild(join);
        Distinct distinct = new Distinct();
        distinct.addChild(project);
        return distinct;
    }
}
