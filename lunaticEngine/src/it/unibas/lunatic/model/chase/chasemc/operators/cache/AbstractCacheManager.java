package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.algebra.GroupBy;
import it.unibas.lunatic.model.algebra.IAggregateFunction;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Join;
import it.unibas.lunatic.model.algebra.MaxAggregateFunction;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.algebra.Select;
import it.unibas.lunatic.model.algebra.ValueAggregateFunction;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.expressions.Expression;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCacheManager implements ICacheManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractCacheManager.class);

    private IRunQuery queryRunner;

    public AbstractCacheManager(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    protected String buildKey(Object value, String stepId) {
        return stepId + "#" + value.toString();
    }

    public void generateCellGroupStats(DeltaChaseStep step) {
    }
    
    protected void loadCellGroupsAndOccurrences(String stepId, IDatabase deltaDB) {
        if (logger.isTraceEnabled()) logger.trace("Loading occurrences value for step " + stepId);
        IAlgebraOperator query = generateQueryForOccurrence(stepId);
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            CellRef cellRef = buildCellRef(tuple);
            IValue cellGroupId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
            CellGroup cellGroup = getCellGroup(cellGroupId, stepId, deltaDB);
            if (cellGroup == null) {
                cellGroup = new CellGroup(cellGroupId, false);
                putCellGroup(cellGroup, stepId, deltaDB);
            }
            cellGroup.addOccurrenceCell(cellRef);
            if (cellGroupId instanceof ConstantValue) {
                putClusterId(cellRef, cellGroupId, stepId, deltaDB);
            }
        }
        it.close();
    }

    protected void loadProvenances(String stepId, IDatabase deltaDB) {
        if (logger.isTraceEnabled()) logger.trace("Loading occurrences value for step " + stepId);
        IAlgebraOperator query = generateQueryForProvenances(stepId);
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            CellRef cellRef = buildCellRef(tuple);
            cellRef.getAttributeRef().getTableAlias().setSource(true);
            IValue cellValue = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.PROVENANCE_CELL_VALUE);
            Cell cell = new Cell(cellRef, cellValue);
            IValue cellGroupId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
            CellGroup cellGroup = getCellGroup(cellGroupId, stepId, deltaDB);
            if (cellGroup == null) {
                if (logger.isTraceEnabled()) logger.trace("Skipping non existent cell group: " + cellGroupId);
//                throw new IllegalStateException("### UNABLE TO FIND CELL GROUP " + cellGroupValue + " IN STEP " + stepId + "\n" + deltaDB.printInstances());
                continue;
            }
            cellGroup.addProvenanceCell(cell);
        }
        it.close();
    }

    protected CellRef buildCellRef(Tuple tuple) {
        TupleOID tid = new TupleOID(LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_OID));
        String table = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TABLE) + "";
        String attribute = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ATTRIBUTE) + "";
        CellRef cellRef = new CellRef(tid, new AttributeRef(table, attribute));
        return cellRef;
    }

    protected IAlgebraOperator generateQueryForOccurrence(String stepId) {
        String occurrenceTable = LunaticConstants.OCCURRENCE_TABLE;
        Scan scan = new Scan(new TableAlias(occurrenceTable));
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.setVariableDescription(LunaticConstants.STEP, new AttributeRef(occurrenceTable, LunaticConstants.STEP));
        selections.add(stepExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        // select max(step), oid, tableName, attribute from R_A group by oid
        AttributeRef oid = new AttributeRef(occurrenceTable, LunaticConstants.CELL_OID);
        AttributeRef tableName = new AttributeRef(occurrenceTable, LunaticConstants.CELL_TABLE);
        AttributeRef attribute = new AttributeRef(occurrenceTable, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef step = new AttributeRef(occurrenceTable, LunaticConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, tableName, attribute}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
        IAggregateFunction tableValue = new ValueAggregateFunction(tableName);
        IAggregateFunction attributeValue = new ValueAggregateFunction(attribute);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue, tableValue, attributeValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(select);
        // select * from R_A_1
        TableAlias alias = new TableAlias(occurrenceTable, "1");
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
        return join;
    }
    
    protected IAlgebraOperator generateQueryForProvenances(String stepId) {
        String provenanceTable = LunaticConstants.PROVENANCE_TABLE;
        Scan scan = new Scan(new TableAlias(provenanceTable));
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.setVariableDescription(LunaticConstants.STEP, new AttributeRef(provenanceTable, LunaticConstants.STEP));
        selections.add(stepExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        // select max(step), cellgroupid, from provenance group by cellgroupid
        AttributeRef groupId = new AttributeRef(provenanceTable, LunaticConstants.GROUP_ID);
        AttributeRef step = new AttributeRef(provenanceTable, LunaticConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{groupId}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction cellGroupIdValue = new ValueAggregateFunction(groupId);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, cellGroupIdValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(select);
        // select * from R_A_1
        TableAlias alias = new TableAlias(provenanceTable, "1");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on step, oid, table, attribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, groupId}));
        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
        AttributeRef groupIdInAlias = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, groupIdInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        return join;
    }
}
