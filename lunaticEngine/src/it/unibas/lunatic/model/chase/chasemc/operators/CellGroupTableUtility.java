package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import speedy.SpeedyConstants;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.Union;
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.MaxAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.model.expressions.Expression;
import speedy.utility.SpeedyUtility;

public class CellGroupTableUtility {

    public static IAlgebraOperator buildQueryToExtractCellGroupCellsForStep(String stepId) {
        IAlgebraOperator occurrenceQuery = buildQueryToExtractOccurrences(stepId);
        IAlgebraOperator justUserQuery = buildQueryToExtractJustificationsAndUserCells(stepId);
        Union union = new Union();
        union.addChild(occurrenceQuery);
        union.addChild(justUserQuery);
        return union;
    }

    private static IAlgebraOperator buildQueryToExtractOccurrences(String stepId) {
        // select * from R_A where step
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan tableScan = new Scan(table);
        List<Expression> expressions = new ArrayList<Expression>();
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + SpeedyConstants.STEP + ")");
        stepExpression.changeVariableDescription(SpeedyConstants.STEP, new AttributeRef(table, SpeedyConstants.STEP));
        expressions.add(stepExpression);
//        Expression typeExpression = new Expression(LunaticConstants.CELL_TYPE + " == \"" + LunaticConstants.TYPE_OCCURRENCE + "\"");
        Expression typeExpression = new Expression("(" + LunaticConstants.CELL_TYPE + " == \"" + LunaticConstants.TYPE_OCCURRENCE + "\" || startswith(\"" + LunaticConstants.TYPE_ADDITIONAL + "\", " + LunaticConstants.CELL_TYPE + ") )");
        typeExpression.changeVariableDescription(LunaticConstants.CELL_TYPE, new AttributeRef(table, LunaticConstants.CELL_TYPE));
        expressions.add(typeExpression);
        Select stepSelect = new Select(expressions);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef cellOid = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef cellTable = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef cellAttr = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef step = new AttributeRef(table, SpeedyConstants.STEP);
        AttributeRef type = new AttributeRef(table, LunaticConstants.CELL_TYPE);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{cellOid, cellTable, cellAttr, type}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidAggregateValue = new ValueAggregateFunction(cellOid);
        IAggregateFunction tableAggregateValue = new ValueAggregateFunction(cellTable);
        IAggregateFunction attributeAggregateValue = new ValueAggregateFunction(cellAttr);
        IAggregateFunction typeAggregateValue = new ValueAggregateFunction(type);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidAggregateValue, tableAggregateValue, attributeAggregateValue, typeAggregateValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on step, celloid, celltable, cellattribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{step, cellOid, cellTable, cellAttr, type}));
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
        AttributeRef tableInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
        AttributeRef attrInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef stepInAlias = new AttributeRef(alias, SpeedyConstants.STEP);
        AttributeRef typeInAlias = new AttributeRef(alias, LunaticConstants.CELL_TYPE);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{stepInAlias, oidInAlias, tableInAlias, attrInAlias, typeInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        AttributeRef cellGroupId = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        AttributeRef originalValue = new AttributeRef(alias, LunaticConstants.CELL_ORIGINAL_VALUE);
        List<AttributeRef> attributes = Arrays.asList(new AttributeRef[]{cellGroupId, cellOid, cellTable, cellAttr, originalValue, type});
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(join);
        return project;
    }

    private static IAlgebraOperator buildQueryToExtractJustificationsAndUserCells(String stepId) {
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
//        TableAlias aliasJustUser = new TableAlias(table.getTableName(), "1");
        Scan aliasScanJustUser = new Scan(table);
        List<Expression> expressionsJustUser = new ArrayList<Expression>();
        Expression stepExpressionJustUser = new Expression("startswith(\"" + stepId + "\", " + SpeedyConstants.STEP + ")");
        stepExpressionJustUser.changeVariableDescription(SpeedyConstants.STEP, new AttributeRef(table, SpeedyConstants.STEP));
        expressionsJustUser.add(stepExpressionJustUser);
        Expression typeExpressionJustUser = new Expression("(" + LunaticConstants.CELL_TYPE + " != \"" + LunaticConstants.TYPE_OCCURRENCE + "\" && !(startswith(\"" + LunaticConstants.TYPE_ADDITIONAL + "\", " + LunaticConstants.CELL_TYPE + ")) )");
//        Expression typeExpressionJustUser = new Expression(LunaticConstants.CELL_TYPE + " != \"" + LunaticConstants.TYPE_OCCURRENCE + "\"");
        typeExpressionJustUser.changeVariableDescription(LunaticConstants.CELL_TYPE, new AttributeRef(table, LunaticConstants.CELL_TYPE));
        expressionsJustUser.add(typeExpressionJustUser);
        Select stepSelectJustUser = new Select(expressionsJustUser);
        stepSelectJustUser.addChild(aliasScanJustUser);
        AttributeRef groupInAliasJustUser = new AttributeRef(table, LunaticConstants.GROUP_ID);
        AttributeRef oidInAliasJustUser = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef tableInAliasJustUser = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef attrInAliasJustUser = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef valueInAliasJustUser = new AttributeRef(table, LunaticConstants.CELL_ORIGINAL_VALUE);
        AttributeRef typeInAliasJustUser = new AttributeRef(table, LunaticConstants.CELL_TYPE);
        List<AttributeRef> attributesJustUser = Arrays.asList(new AttributeRef[]{groupInAliasJustUser, oidInAliasJustUser, tableInAliasJustUser, attrInAliasJustUser, valueInAliasJustUser, typeInAliasJustUser});
        Project projectJustUser = new Project(SpeedyUtility.createProjectionAttributes(attributesJustUser));
        projectJustUser.addChild(stepSelectJustUser);
        Distinct distinct = new Distinct();
        distinct.addChild(projectJustUser);
        return distinct;
    }

    public static IAlgebraOperator buildQueryToExtractCellGroupIdsInSingleStep(String stepId) {
        // select value from table where step
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan tableScan = new Scan(table);
        Expression stepExpression;
        stepExpression = new Expression("\"" + stepId + "\" == " + SpeedyConstants.STEP);
        stepExpression.changeVariableDescription(SpeedyConstants.STEP, new AttributeRef(table, SpeedyConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        AttributeRef cellValue = new AttributeRef(table, LunaticConstants.GROUP_ID);
        List<AttributeRef> attributes = Arrays.asList(new AttributeRef[]{cellValue});
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(stepSelect);
        Distinct distinct = new Distinct();
        distinct.addChild(project);
        return distinct;
    }

    public static IAlgebraOperator generateDeleteQuery(CellGroup cellGroup, String stepId, String tableName) {
        TableAlias table = new TableAlias(tableName);
        Scan scan = new Scan(table);
        List<Expression> selections = new ArrayList<Expression>();
        Expression stepExpression = new Expression(SpeedyConstants.STEP + " == \"" + stepId + "\"");
        stepExpression.changeVariableDescription(SpeedyConstants.STEP, new AttributeRef(table, SpeedyConstants.STEP));
//        Expression valueExpression = new Expression(LunaticConstants.CELL_ORIGINAL_VALUE + " == \"" + cellGroup.getCellGroupValueFromGroupID() + "\"");
        Expression valueExpression = new Expression(LunaticConstants.GROUP_ID + " == \"" + cellGroup.getId() + "\"");
        valueExpression.changeVariableDescription(LunaticConstants.GROUP_ID, new AttributeRef(table, LunaticConstants.GROUP_ID));
        selections.add(stepExpression);
        selections.add(valueExpression);
        Select select = new Select(selections);
        select.addChild(scan);
        return select;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////                         DE SCENARIO                                  //////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static IAlgebraOperator buildQueryToExtractCellGroupCellsForStepDE() {
        TableAlias table = new TableAlias(LunaticConstants.CELLGROUP_TABLE);
        Scan tableScan = new Scan(table);
        // select max(version), oid from R_A group by oid
        AttributeRef cellOid = new AttributeRef(table, LunaticConstants.CELL_OID);
        AttributeRef cellTable = new AttributeRef(table, LunaticConstants.CELL_TABLE);
        AttributeRef cellAttr = new AttributeRef(table, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef version = new AttributeRef(table, SpeedyConstants.VERSION);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{cellOid, cellTable, cellAttr}));
        IAggregateFunction max = new MaxAggregateFunction(version);
        IAggregateFunction oidAggregateValue = new ValueAggregateFunction(cellOid);
        IAggregateFunction tableAggregateValue = new ValueAggregateFunction(cellTable);
        IAggregateFunction attributeAggregateValue = new ValueAggregateFunction(cellAttr);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidAggregateValue, tableAggregateValue, attributeAggregateValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(tableScan);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on version, celloid, celltable, cellattribute
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{version, cellOid, cellTable, cellAttr}));
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.CELL_OID);
        AttributeRef tableInAlias = new AttributeRef(alias, LunaticConstants.CELL_TABLE);
        AttributeRef attrInAlias = new AttributeRef(alias, LunaticConstants.CELL_ATTRIBUTE);
        AttributeRef versionInAlias = new AttributeRef(alias, SpeedyConstants.VERSION);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{versionInAlias, oidInAlias, tableInAlias, attrInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        AttributeRef cellGroupId = new AttributeRef(alias, LunaticConstants.GROUP_ID);
        List<AttributeRef> attributes = Arrays.asList(new AttributeRef[]{cellGroupId, cellOid, cellTable, cellAttr});
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(join);
        return project;
    }
}
