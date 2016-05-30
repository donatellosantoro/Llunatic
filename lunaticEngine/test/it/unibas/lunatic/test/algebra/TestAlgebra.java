package it.unibas.lunatic.test.algebra;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.OrderBy;
import speedy.model.algebra.Project;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.SelectIn;
import speedy.model.algebra.SelectNotIn;
import speedy.model.algebra.aggregatefunctions.CountAggregateFunction;
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.expressions.Expression;
import speedy.utility.SpeedyUtility;

@SuppressWarnings("unchecked")
public class TestAlgebra extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestAlgebra.class);

    public void testScan() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias tableAlias = new TableAlias("IBDBookSet", true);
        Scan scan = new Scan(tableAlias);
        if (logger.isTraceEnabled()) logger.debug(scan.toString());
        Iterator<Tuple> result = scan.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 3\n"));
    }

    public void testEmptySelect() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias tableAlias = new TableAlias("IBDBookSet", true);
        Scan scan = new Scan(tableAlias);
        Select select = new Select(Collections.EMPTY_LIST);
        select.addChild(scan);
        if (logger.isTraceEnabled()) logger.debug(scan.toString());
        Iterator<Tuple> result = select.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 3\n"));
    }

    public void testSelect() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression = new Expression("t == \"TheHobbit\"");
        TableAlias tableAlias = new TableAlias("IBDBookSet", true);
        AttributeRef attributeRef = new AttributeRef(tableAlias, "title");
        FormulaVariable tVariable = new FormulaVariable("t");
        tVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(attributeRef, "t"));
        expression.changeVariableDescription("t", tVariable);
        expressions.add(expression);
        Select select = new Select(expressions);
        Scan scan = new Scan(tableAlias);
        select.addChild(scan);
        if (logger.isTraceEnabled()) logger.debug(select.toString());
        Iterator<Tuple> result = select.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 1\n"));
    }

    public void testSelectIn() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias innerTableAlias = new TableAlias("IBDBookSet", true);
        List<AttributeRef> attributeToProject = new ArrayList<AttributeRef>();
        attributeToProject.add(new AttributeRef(innerTableAlias, "title"));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributeToProject));
        project.addChild(new Scan(innerTableAlias));
        TableAlias tableAlias = new TableAlias("IBLBookSet", true);
        List<AttributeRef> attributesToSelect = new ArrayList<AttributeRef>();
        attributesToSelect.add(new AttributeRef(tableAlias, "title"));
        List<IAlgebraOperator> selectionOperators = new ArrayList<IAlgebraOperator>();
        selectionOperators.add(project);
        SelectIn selectIn = new SelectIn(attributesToSelect, selectionOperators);
        Scan scan = new Scan(tableAlias);
        selectIn.addChild(scan);
        if (logger.isTraceEnabled()) logger.debug(selectIn.toString());
        Iterator<Tuple> result = selectIn.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 1\n"));
    }

    public void testSelectNotIn() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_plain);
        TableAlias innerTableAlias = new TableAlias("IBLBookSet", true);
        List<AttributeRef> attributeToProject = new ArrayList<AttributeRef>();
        attributeToProject.add(new AttributeRef(innerTableAlias, "title"));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributeToProject));
        project.addChild(new Scan(innerTableAlias));
        TableAlias tableAlias = new TableAlias("IBDBookSet", true);
        List<AttributeRef> attributesToSelect = new ArrayList<AttributeRef>();
        attributesToSelect.add(new AttributeRef(tableAlias, "title"));
        List<IAlgebraOperator> selectionOperators = new ArrayList<IAlgebraOperator>();
        selectionOperators.add(project);
        SelectNotIn selectIn = new SelectNotIn(attributesToSelect, selectionOperators);
        Scan scan = new Scan(tableAlias);
        selectIn.addChild(scan);
        if (logger.isTraceEnabled()) logger.debug(selectIn.toString());
        Iterator<Tuple> result = selectIn.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
    }

    public void testProjectSingleAttribute() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_rew);
        TableAlias tableAlias = new TableAlias("S_Employee", true);
        List<AttributeRef> attributes = new ArrayList<AttributeRef>();
        attributes.add(new AttributeRef(tableAlias, "name"));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(new Scan(tableAlias));
        if (logger.isTraceEnabled()) logger.debug(project.toString());
        Iterator<Tuple> result = project.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 5\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 8\n"));
    }

    public void testProjectMultipleAttribute() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_rew);
        TableAlias tableAlias = new TableAlias("S_Employee", true);
        List<AttributeRef> attributes = new ArrayList<AttributeRef>();
        attributes.add(new AttributeRef(tableAlias, "name"));
        attributes.add(new AttributeRef(tableAlias, "age"));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(new Scan(tableAlias));
        if (logger.isTraceEnabled()) logger.debug(project.toString());
        Iterator<Tuple> result = project.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 8\n"));
    }

    public void testJoinProjectRS() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.testRS);
        TableAlias tableAliasR = new TableAlias("R");
        TableAlias tableAliasS = new TableAlias("S");
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(tableAliasR, "c"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(tableAliasS, "a"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(tableAliasR));
        join.addChild(new Scan(tableAliasS));
        List<AttributeRef> attributes = new ArrayList<AttributeRef>();
        attributes.add(new AttributeRef(tableAliasR, "a"));
        attributes.add(new AttributeRef(tableAliasS, "a"));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(join);
        if (logger.isTraceEnabled()) logger.debug(project.toString());
        Iterator<Tuple> result = project.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isTraceEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
    }

    public void testCount() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_egd);
        TableAlias tableAlias = new TableAlias("S_Employee", true);
        Scan scan = new Scan(tableAlias);
        List<AttributeRef> groupingAttribute = new ArrayList<AttributeRef>();
        AttributeRef nameAttribute = new AttributeRef(tableAlias, "name");
        groupingAttribute.add(nameAttribute);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>();
        aggregateFunctions.add(new ValueAggregateFunction(nameAttribute));
        aggregateFunctions.add(new CountAggregateFunction(new AttributeRef(tableAlias, "count")));
        GroupBy groupBy = new GroupBy(groupingAttribute, aggregateFunctions);
        groupBy.addChild(scan);
        if (logger.isTraceEnabled()) logger.debug(scan.toString());
        Iterator<Tuple> result = groupBy.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 5\n"));
    }

    public void testHaving() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.employees_egd);
        TableAlias tableAlias = new TableAlias("S_Employee", true);
        Scan scan = new Scan(tableAlias);
        List<AttributeRef> groupingAttribute = new ArrayList<AttributeRef>();
        AttributeRef nameAttribute = new AttributeRef(tableAlias, "name");
        groupingAttribute.add(nameAttribute);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>();
        aggregateFunctions.add(new ValueAggregateFunction(nameAttribute));
        AttributeRef countAttribute = new AttributeRef(tableAlias, "count");
        aggregateFunctions.add(new CountAggregateFunction(countAttribute));
        GroupBy groupBy = new GroupBy(groupingAttribute, aggregateFunctions);
        groupBy.addChild(scan);
        Expression expression = new Expression("count > 1");
        FormulaVariable countVariable = new FormulaVariable("count");
        countVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(countAttribute, "count"));
        expression.changeVariableDescription("count", countVariable);
        Select select = new Select(expression);
        select.addChild(groupBy);
        Project project = new Project(SpeedyUtility.createProjectionAttributes(groupingAttribute));
        project.addChild(select);
        if (logger.isTraceEnabled()) logger.debug(project.toString());
        Iterator<Tuple> result = project.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
    }

    public void testViolation() {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.algebra_employees);
        TableAlias tableAlias = new TableAlias("S_Employee", true);
        Scan violationScan = new Scan(tableAlias);
        List<AttributeRef> firstGroupingAttribute = new ArrayList<AttributeRef>();
        AttributeRef nameAttribute = new AttributeRef(tableAlias, "name");
        AttributeRef ageAttribute = new AttributeRef(tableAlias, "age");
        firstGroupingAttribute.add(nameAttribute);
        firstGroupingAttribute.add(ageAttribute);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>();
        aggregateFunctions.add(new ValueAggregateFunction(nameAttribute));
        aggregateFunctions.add(new ValueAggregateFunction(ageAttribute));
        AttributeRef countAttribute = new AttributeRef(tableAlias, "count");
        GroupBy firstGroupBy = new GroupBy(firstGroupingAttribute, aggregateFunctions);
        firstGroupBy.addChild(violationScan);
        Expression expression = new Expression("count > 1");
        FormulaVariable countVariable = new FormulaVariable("count");
        countVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(countAttribute, "count"));
        expression.changeVariableDescription("count", countVariable);

        List<AttributeRef> secondGroupingAttribute = new ArrayList<AttributeRef>();
        List<IAggregateFunction> secondAggregateFunction = new ArrayList<IAggregateFunction>();
        secondAggregateFunction.add(new ValueAggregateFunction(nameAttribute));
        secondAggregateFunction.add(new CountAggregateFunction(countAttribute));
        secondGroupingAttribute.add(nameAttribute);
        GroupBy secondGroupBy = new GroupBy(secondGroupingAttribute, secondAggregateFunction);
        secondGroupBy.addChild(firstGroupBy);
        Select select = new Select(expression);
        select.addChild(secondGroupBy);
        Project violationProject = new Project(SpeedyUtility.createProjectionAttributes(secondGroupingAttribute));
        violationProject.addChild(select);

        Scan scan = new Scan(tableAlias);
        List<AttributeRef> selectInAttribute = new ArrayList<AttributeRef>();
        selectInAttribute.add(nameAttribute);
        List<IAlgebraOperator> selectionOperators = new ArrayList<IAlgebraOperator>();
        selectionOperators.add(violationProject);
        SelectIn selectIn = new SelectIn(selectInAttribute, selectionOperators);
        selectIn.addChild(scan);
        OrderBy orderBy = new OrderBy(selectInAttribute);
        orderBy.addChild(selectIn);
        if (logger.isTraceEnabled()) logger.debug(violationProject.toString());
        Iterator<Tuple> result = orderBy.execute(scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        if (logger.isDebugEnabled()) logger.debug(stringResult);
//        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
    }
}
