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
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.expressions.Expression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CellGroupTableUtility {

    public static IAlgebraOperator buildQueryToExtractCellGroupCellsForStep(String stepId) {
        // select * from R_A where step
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan tableScan = new Scan(table);
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef cellOid = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef cellTable = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef cellAttr = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef step = new AttributeRef(table, LunaticConstants.STEP);
        AttributeRef originalValue = new AttributeRef(table, LunaticConstants.CELL_ORIGINAL_VALUE);
        AttributeRef type = new AttributeRef(table, LunaticConstants.CELL_TYPE);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{cellOid, cellTable, cellAttr, originalValue, type}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidAggregateValue = new ValueAggregateFunction(cellOid);
        IAggregateFunction tableAggregateValue = new ValueAggregateFunction(cellTable);
        IAggregateFunction attributeAggregateValue = new ValueAggregateFunction(cellAttr);
        IAggregateFunction originalAggregateValue = new ValueAggregateFunction(originalValue);
        IAggregateFunction typeAggregateValue = new ValueAggregateFunction(type);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidAggregateValue, tableAggregateValue, attributeAggregateValue, originalAggregateValue, typeAggregateValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on step, celloid, celltable, cellattribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, cellOid, cellTable, cellAttr, originalValue, type}));
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
        AttributeRef tableInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
        AttributeRef attrInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
        AttributeRef originalValueInAlias = new AttributeRef(alias, LunaticConstants.CELL_ORIGINAL_VALUE);
        AttributeRef typeInAlias = new AttributeRef(alias, LunaticConstants.CELL_TYPE);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, oidInAlias, tableInAlias, attrInAlias, originalValueInAlias, typeInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
//        return join;
        AttributeRef cellGroupId = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        Project project = new Project(Arrays.asList(new AttributeRef[]{cellGroupId, cellOid, cellTable, cellAttr, originalValue, type}));
        project.addChild(join);
        return project;
//        Distinct distinct = new Distinct();
//        distinct.addChild(project);
//        return distinct;
    }

    public static IAlgebraOperator buildQueryToExtractCellGroupIdsInSingleStep(String stepId) {
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

    public static IAlgebraOperator generateDeleteQuery(CellGroup cellGroup, String stepId, String tableName) {
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

//    public static IAlgebraOperator buildQueryToFindCellGroupFromCellRef(CellRef cellRef, String stepId) {
//        Scan scan = new Scan(new TableAlias(LunaticConstants.CELLGROUP_TABLE));
//        List<Expression> selections = new ArrayList<Expression>();
//        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + LunaticConstants.STEP + ")");
//        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.STEP));
//        Expression oidExpression = new Expression(LunaticConstants.CELL_OID + " == \"" + cellRef.getTupleOID() + "\"");
//        oidExpression.changeVariableDescription(LunaticConstants.CELL_OID, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_OID));
//        Expression tableExpression = new Expression(LunaticConstants.CELL_TABLE + " == \"" + cellRef.getAttributeRef().getTableName() + "\"");
//        tableExpression.changeVariableDescription(LunaticConstants.CELL_TABLE, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_TABLE));
//        Expression attributeExpression = new Expression(LunaticConstants.CELL_ATTRIBUTE + " == \"" + cellRef.getAttributeRef().getName() + "\"");
//        attributeExpression.changeVariableDescription(LunaticConstants.CELL_ATTRIBUTE, new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_ATTRIBUTE));
//        selections.add(stepExpression);
//        selections.add(oidExpression);
//        selections.add(tableExpression);
//        selections.add(attributeExpression);
//        Select select = new Select(selections);
//        select.addChild(scan);
//        // select max(step), oid, tableName, attribute from R_A group by oid
//        AttributeRef oid = new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_OID);
//        AttributeRef tableName = new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_TABLE);
//        AttributeRef attribute = new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.CELL_ATTRIBUTE);
//        AttributeRef step = new AttributeRef(LunaticConstants.CELLGROUP_TABLE, LunaticConstants.STEP);
//        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, tableName, attribute}));
//        IAggregateFunction max = new MaxAggregateFunction(step);
//        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
//        IAggregateFunction tableValue = new ValueAggregateFunction(tableName);
//        IAggregateFunction attributeValue = new ValueAggregateFunction(attribute);
//        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue, tableValue, attributeValue}));
//        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
//        groupBy.addChild(select);
//        // select * from R_A_1
//        TableAlias alias = new TableAlias(LunaticConstants.CELLGROUP_TABLE, "1");
//        Scan aliasScan = new Scan(alias);
//        // select * from (group-by) join R_A_1 on step, oid, table, attribute
//        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, oid, tableName, attribute}));
//        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
//        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
//        AttributeRef tableNameInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
//        AttributeRef attributeInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
//        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, oidInAlias, tableNameInAlias, attributeInAlias}));
//        Join join = new Join(leftAttributes, rightAttributes);
//        join.addChild(groupBy);
//        join.addChild(aliasScan);
//        // select value from (join)
//        AttributeRef valueInAlias = new AttributeRef(alias, LunaticConstants.GROUP_ID);
//        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{valueInAlias}));
//        Project project = new Project(projectionAttributes);
//        project.addChild(join);
//        return project;
//    }
}
