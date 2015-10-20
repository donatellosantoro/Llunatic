package it.unibas.lunatic.test.persistence.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.CartesianProduct;
import speedy.model.algebra.Join;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.model.database.operators.IRunQuery;
import speedy.model.database.operators.dbms.SQLRunQuery;
import speedy.model.expressions.Expression;

public class TestAlgebraToSQL extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestAlgebraToSQL.class);
    private AlgebraTreeToSQL sqlGenerator = new AlgebraTreeToSQL();
    private static Scenario scenario;
    private IRunQuery queryRunner = new SQLRunQuery();

    @Override
    protected void setUp() throws Exception {
        if (scenario == null) {
            scenario = UtilityTest.loadScenarioFromResources(References.bookPublisher_dbms);
        }
    }

    public void testScan() {
        TableAlias tableAlias = new TableAlias("ibdbookset", true);
        Scan scan = new Scan(tableAlias);
        String query = sqlGenerator.treeToSQL(scan, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_ibdbookset.oid AS source_ibdbookset" + LunaticConstants.DELTA_TABLE_SEPARATOR + "oid, source_ibdbookset.title AS source_ibdbookset" + LunaticConstants.DELTA_TABLE_SEPARATOR + "title FROM source.ibdbookset AS source_ibdbookset", query);
//        Assert.assertEquals("SELECT ibdbookset.oid AS ibdbookset_oid, ibdbookset.title AS ibdbookset_title FROM source.ibdbookset", query);
        ITupleIterator result = queryRunner.run(scan, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 3\n"));
    }

    public void testSelect() {
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression = new Expression("t == \"The Hobbit\"");
        TableAlias tableAlias = new TableAlias("ibdbookset", true);
        AttributeRef attributeRef = new AttributeRef(tableAlias, "title");
        FormulaVariable tVariable = new FormulaVariable("t");
        tVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(attributeRef, "t"));
        expression.changeVariableDescription("t", tVariable);
        expressions.add(expression);
        Select select = new Select(expressions);
        Scan scan = new Scan(tableAlias);
        select.addChild(scan);
        String query = sqlGenerator.treeToSQL(select, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_ibdbookset.oid AS source" + LunaticConstants.DELTA_TABLE_SEPARATOR + "ibdbookset_oid, source_ibdbookset.title AS source" + LunaticConstants.DELTA_TABLE_SEPARATOR + "ibdbookset_title FROM source.ibdbookset AS source_ibdbookset WHERE (source_ibdbookset.title = 'The Hobbit')", query);
//        Assert.assertEquals("SELECT ibdbookset.oid AS ibdbookset_oid, ibdbookset.title AS ibdbookset_title FROM source.ibdbookset WHERE (ibdbookset.title = 'The Hobbit')", query);
        ITupleIterator result = queryRunner.run(select, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 1\n"));
    }

    public void testSelectNotNull() {
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression2 = new Expression("isNotNull(t)");
        TableAlias tableAlias = new TableAlias("ibdbookset", true);
        AttributeRef attributeRef = new AttributeRef(tableAlias, "title");
        FormulaVariable tVariable = new FormulaVariable("t");
        tVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(attributeRef, "t"));
        expression2.changeVariableDescription("t", tVariable);
        expressions.add(expression2);
        Select select = new Select(expressions);
        Scan scan = new Scan(tableAlias);
        select.addChild(scan);
        String query = sqlGenerator.treeToSQL(select, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_ibdbookset.oid AS source_ibdbookset_oid, source_ibdbookset.title AS source_ibdbookset_title FROM source.ibdbookset AS source_ibdbookset WHERE (source_ibdbookset.title = 'The Hobbit') AND source_ibdbookset.title IS NOT NULL", query);
//        Assert.assertEquals("SELECT ibdbookset.oid AS ibdbookset_oid, ibdbookset.title AS ibdbookset_title FROM source.ibdbookset WHERE (ibdbookset.title = 'The Hobbit') AND ibdbookset.title IS NOT NULL", query);
        ITupleIterator result = queryRunner.run(select, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 3\n"));
    }

    public void testMultipleSelection() {
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression1 = new Expression("t == \"The Hobbit\"");
        Expression expression2 = new Expression("isNotNull(t)");
        TableAlias tableAlias = new TableAlias("ibdbookset", true);
        AttributeRef attributeRef = new AttributeRef(tableAlias, "title");
        FormulaVariable tVariable = new FormulaVariable("t");
        tVariable.addPremiseRelationalOccurrence(new FormulaVariableOccurrence(attributeRef, "t"));
        expression1.changeVariableDescription("t", tVariable);
        expressions.add(expression1);
        expression2.changeVariableDescription("t", tVariable);
        expressions.add(expression2);
        Select select = new Select(expressions);
        Scan scan = new Scan(tableAlias);
        select.addChild(scan);
        String query = sqlGenerator.treeToSQL(select, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_ibdbookset.oid AS source_ibdbookset_oid, source_ibdbookset.title AS source_ibdbookset_title FROM source.ibdbookset AS source_ibdbookset WHERE (source_ibdbookset.title = 'The Hobbit') AND source_ibdbookset.title IS NOT NULL", query);
//        Assert.assertEquals("SELECT ibdbookset.oid AS ibdbookset_oid, ibdbookset.title AS ibdbookset_title FROM source.ibdbookset WHERE (ibdbookset.title = 'The Hobbit') AND ibdbookset.title IS NOT NULL", query);
        ITupleIterator result = queryRunner.run(select, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 1\n"));
    }

    public void testJoin() {
        TableAlias tableAliasLeft = new TableAlias("iblbookset", true);
        TableAlias tableAliasRight = new TableAlias("iblpublisherset", true);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(tableAliasLeft, "pubid"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(tableAliasRight, "id"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(tableAliasLeft));
        join.addChild(new Scan(tableAliasRight));
        String query = sqlGenerator.treeToSQL(join, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_iblbookset.oid AS source_iblbookset_oid, source_iblbookset.title AS source_iblbookset_title, source_iblbookset.pubid AS source_iblbookset_pubid, source_iblpublisherset.oid AS source_iblpublisherset_oid, source_iblpublisherset.id AS source_iblpublisherset_id, source_iblpublisherset.name AS source_iblpublisherset_name FROM source.iblbookset AS source_iblbookset JOIN source.iblpublisherset AS source_iblpublisherset ON source_iblbookset.pubid = source_iblpublisherset.id", query);
//        Assert.assertEquals("SELECT iblbookset.oid AS iblbookset_oid, iblbookset.title AS iblbookset_title, iblbookset.pubid AS iblbookset_pubid, iblpublisherset.oid AS iblpublisherset_oid, iblpublisherset.id AS iblpublisherset_id, iblpublisherset.name AS iblpublisherset_name FROM source.iblbookset JOIN source.iblpublisherset ON iblbookset.pubid = iblpublisherset.id", query);
        ITupleIterator result = queryRunner.run(join, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 2\n"));
    }

    public void testMultipleJoin() {
        TableAlias tableAliasR = new TableAlias("iblbookset", true);
        TableAlias tableAliasL = new TableAlias("iblpublisherset", true);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        leftAttributes.add(new AttributeRef(tableAliasR, "pubid"));
        leftAttributes.add(new AttributeRef(tableAliasR, "title"));
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        rightAttributes.add(new AttributeRef(tableAliasL, "id"));
        rightAttributes.add(new AttributeRef(tableAliasL, "name"));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(new Scan(tableAliasR));
        join.addChild(new Scan(tableAliasL));
        String query = sqlGenerator.treeToSQL(join, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
//        query = query.replaceAll("(\\s)+", " ");
//        Assert.assertEquals("SELECT source_iblbookset.oid AS source_iblbookset_oid, source_iblbookset.title AS source_iblbookset_title, source_iblbookset.pubid AS source_iblbookset_pubid, source_iblpublisherset.oid AS source_iblpublisherset_oid, source_iblpublisherset.id AS source_iblpublisherset_id, source_iblpublisherset.name AS source_iblpublisherset_name FROM source.iblbookset AS source_iblbookset JOIN source.iblpublisherset AS source_iblpublisherset ON source_iblbookset.pubid = source_iblpublisherset.id AND source_iblbookset.title = source_iblpublisherset.name", query);
//        Assert.assertEquals("SELECT iblbookset.oid AS iblbookset_oid, iblbookset.title AS iblbookset_title, iblbookset.pubid AS iblbookset_pubid, iblpublisherset.oid AS iblpublisherset_oid, iblpublisherset.id AS iblpublisherset_id, iblpublisherset.name AS iblpublisherset_name FROM source.iblbookset JOIN source.iblpublisherset ON iblbookset.pubid = iblpublisherset.id AND iblbookset.title = iblpublisherset.name", query);
        ITupleIterator result = queryRunner.run(join, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 0\n"));
    }

    public void testCartesianProduct() {
        TableAlias bookSet = new TableAlias("iblbookset", true);
        TableAlias publisherSet = new TableAlias("iblpublisherset", true);
        CartesianProduct cartesianProduct = new CartesianProduct();
        cartesianProduct.addChild(new Scan(bookSet));
        cartesianProduct.addChild(new Scan(publisherSet));
        String query = sqlGenerator.treeToSQL(cartesianProduct, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
        ITupleIterator result = queryRunner.run(cartesianProduct, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 4\n"));
    }

    public void testMultipleCartesianProduct() {
        TableAlias bookSet = new TableAlias("iblbookset", true);
        TableAlias publisherSet = new TableAlias("iblpublisherset", true);
        TableAlias locSet = new TableAlias("locset", true);
        CartesianProduct cartesianProduct = new CartesianProduct();
        cartesianProduct.addChild(new Scan(bookSet));
        cartesianProduct.addChild(new Scan(publisherSet));
        cartesianProduct.addChild(new Scan(locSet));
        String query = sqlGenerator.treeToSQL(cartesianProduct, scenario.getSource(), scenario.getTarget(), "");
        if (logger.isDebugEnabled()) logger.debug(query);
        ITupleIterator result = queryRunner.run(cartesianProduct, scenario.getSource(), scenario.getTarget());
        String stringResult = LunaticUtility.printTupleIterator(result);
        result.close();
        if (logger.isDebugEnabled()) logger.debug(stringResult);
        Assert.assertTrue(stringResult.startsWith("Number of tuples: 8\n"));
    }
}
