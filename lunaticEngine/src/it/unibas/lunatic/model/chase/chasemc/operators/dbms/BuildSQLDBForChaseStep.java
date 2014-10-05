package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.algebra.CreateTable;
import it.unibas.lunatic.model.algebra.Distinct;
import it.unibas.lunatic.model.algebra.GroupBy;
import it.unibas.lunatic.model.algebra.IAggregateFunction;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.Join;
import it.unibas.lunatic.model.algebra.MaxAggregateFunction;
import it.unibas.lunatic.model.algebra.Project;
import it.unibas.lunatic.model.algebra.RestoreOIDs;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.algebra.Select;
import it.unibas.lunatic.model.algebra.ValueAggregateFunction;
import it.unibas.lunatic.model.algebra.sql.AlgebraTreeToSQL;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.dbms.DBMSVirtualDB;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.expressions.Expression;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildSQLDBForChaseStep implements IBuildDatabaseForChaseStep {

    private static Logger logger = LoggerFactory.getLogger(BuildSQLDBForChaseStep.class);

    private AlgebraTreeToSQL sqlGenerator = new AlgebraTreeToSQL();
    private boolean useHash = true;

    @Override
    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        return extractDatabase(stepId, deltaDB, originalDB, false);
    }

    @Override
    public IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        if (logger.isDebugEnabled()) logger.debug("Extracting database with distinct for step " + stepId);
        return extractDatabase(stepId, deltaDB, originalDB, true);
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, boolean distinct) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating database for step " + stepId);
        //Materialize join
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        for (String tableName : originalDB.getTableNames()) {
            attributeMap.put(tableName, buildAttributeRefs(originalDB.getTable(tableName)));
        }
        StringBuilder script = new StringBuilder();
        Map<String, String> tableViews = extractDatabase("\"" + stepId + "\"", "", deltaDB, originalDB, attributeMap, true, distinct);
        for (String tableName : tableViews.keySet()) {
            String viewScript = tableViews.get(tableName);
            script.append(viewScript).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("View paramized script:\n" + script);
        QueryManager.executeScript(script.toString(), ((DBMSDB) originalDB).getAccessConfiguration(), true, true, true);
        AccessConfiguration accessConfiguration = ((DBMSDB) deltaDB).getAccessConfiguration();
        String cleanStepId = stepId.replaceAll("\\.", "_");
        if (useHash) {
            cleanStepId = getHash(cleanStepId);
        }
        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "__" + cleanStepId, accessConfiguration);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DELTA_DB_STEP_BUILDER, end - start);
        return virtualDB;
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating database for step " + stepId + " and depedency " + dependency);
        //Materialize join
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        List<AttributeRef> requestedAttributesForDependency = DependencyUtility.extractRequestedAttributes(dependency);
        if (requestedAttributesForDependency.isEmpty()) {
            throw new IllegalArgumentException("Unable to find relevant attributes for dependency " + dependency);
        }
        for (AttributeRef attribute : requestedAttributesForDependency) {
            List<AttributeRef> attributesForTable = attributeMap.get(attribute.getTableName());
            if (attributesForTable == null) {
                attributesForTable = new ArrayList<AttributeRef>();
                attributeMap.put(attribute.getTableName(), attributesForTable);
            }
            attributesForTable.add(attribute);
        }
        StringBuilder script = new StringBuilder();
//        script.append("ANALYZE;\n");
        Map<String, String> tableViews = extractDatabase("\"" + stepId + "\"", dependency.getId(), deltaDB, originalDB, attributeMap, true, false);
        for (String tableName : tableViews.keySet()) {
            String viewScript = tableViews.get(tableName);
            script.append(viewScript).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("View paramized script:\n" + script);
        QueryManager.executeScript(script.toString(), ((DBMSDB) originalDB).getAccessConfiguration(), true, true, true);
        AccessConfiguration accessConfiguration = ((DBMSDB) deltaDB).getAccessConfiguration();
        String cleanStepId = stepId.replaceAll("\\.", "_");
        if (useHash) {
            cleanStepId = getHash(cleanStepId);
        }
        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "_" + dependency.getId() + "_" + cleanStepId, accessConfiguration);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DELTA_DB_STEP_BUILDER, end - start);
        return virtualDB;
    }

    @Deprecated
    public IDatabase extractDatabaseWithViews(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        logger.warn("Building entire database for step. Slow call..");
        //Create param-view
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        for (String tableName : originalDB.getTableNames()) {
            attributeMap.put(tableName, buildAttributeRefs(originalDB.getTable(tableName)));
        }
        StringBuilder script = new StringBuilder();
        Map<String, String> tableViews = extractDatabase("$1", "", deltaDB, originalDB, attributeMap, false, false);
        for (String tableName : tableViews.keySet()) {
            String viewScript = tableViews.get(tableName);
            String viewFunctionScript = generateFunctionForView(((DBMSDB) originalDB).getAccessConfiguration().getSchemaName(), ((DBMSDB) deltaDB).getAccessConfiguration().getSchemaName(), tableName, viewScript);
            script.append(viewFunctionScript).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("View paramized script:\n" + script);
        QueryManager.executeScript(script.toString(), ((DBMSDB) originalDB).getAccessConfiguration(), true, true, true);
        AccessConfiguration accessConfiguration = ((DBMSDB) deltaDB).getAccessConfiguration();
        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "('" + stepId + "')", accessConfiguration);
        return virtualDB;
    }

    private String generateFunctionForView(String originalSchemaName, String deltaDBSchemaName, String tableName, String viewScript) {
        StringBuilder functionScript = new StringBuilder();
        functionScript.append("CREATE OR REPLACE FUNCTION ").append(deltaDBSchemaName).append(".").append(tableName).append("(step varchar)").append("\n");
        functionScript.append(LunaticConstants.INDENT).append("returns setof ").append(originalSchemaName).append(".").append(tableName).append(" as $$").append("\n");
        functionScript.append(viewScript).append(";").append("\n");
        functionScript.append("$$ language sql immutable;").append("\n");
        return functionScript.toString();
    }

    private Map<String, String> extractDatabase(String stepId, String dependencyId, IDatabase deltaDB, IDatabase originalDB, Map<String, List<AttributeRef>> tablesAndAttributesToExtract, boolean materialize, boolean distinct) {
        Set<String> tableNames = tablesAndAttributesToExtract.keySet();
        Map<String, String> tableViews = new HashMap<String, String>();
        for (String tableName : tableNames) {
            ITable table = originalDB.getTable(tableName);
            List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>(tablesAndAttributesToExtract.get(tableName));
            List<AttributeRef> nonAffectedAttributes = new ArrayList<AttributeRef>();
            List<AttributeRef> deltaTableAttributes = new ArrayList<AttributeRef>();
            IAlgebraOperator initialTable = generateInitialTable(tableName, affectedAttributes, nonAffectedAttributes, deltaDB, deltaTableAttributes, stepId, materialize);
            AttributeRef oidAttributeRef = findOidAttribute(initialTable, deltaDB);
            IAlgebraOperator algebraRoot;
            if (affectedAttributes.isEmpty()) {
                if (distinct) {
                    removeOIDAttribute(deltaTableAttributes);
                }
                IAlgebraOperator projection = new Project(deltaTableAttributes, cleanNames(deltaTableAttributes), true);
                projection.addChild(initialTable);
                algebraRoot = projection;
            } else {
                algebraRoot = buildAlgebraTreeForTable(table, affectedAttributes, nonAffectedAttributes, stepId, oidAttributeRef, initialTable, deltaTableAttributes, materialize, distinct);
            }
            if (distinct) {
                algebraRoot = createDistinctTable(algebraRoot);
            }
            IAlgebraOperator resultOperator;
            if (materialize) {
                String cleanStepId = stepId.replaceAll("\\.", "_");
                cleanStepId = cleanStepId.replaceAll("\"", "");
                if (useHash) {
                    cleanStepId = getHash(cleanStepId);
                }
                String materializedTableName = tableName + "_" + dependencyId + "_" + cleanStepId;
                CreateTable createTable = new CreateTable(materializedTableName, materializedTableName, distinct);
                createTable.addChild(algebraRoot);
                resultOperator = createTable;
            } else {
                resultOperator = algebraRoot;
            }
            if (logger.isDebugEnabled()) logger.debug("Algebra for extract database: \n" + resultOperator);
            String query = sqlGenerator.treeToSQL(resultOperator, null, deltaDB, "");
            if (logger.isDebugEnabled()) logger.debug("Script for extract database: \n" + query);
            tableViews.put(tableName, query);
        }
        return tableViews;
    }

    private IAlgebraOperator createDistinctTable(IAlgebraOperator algebraRoot) {
        Distinct distinct = new Distinct();
        distinct.addChild(algebraRoot);
        return distinct;
    }

    private AttributeRef findOidAttribute(IAlgebraOperator initialTable, IDatabase deltaDB) {
        for (AttributeRef attribute : initialTable.getAttributes(null, deltaDB)) {
            if (attribute.getName().equals(LunaticConstants.TID)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Unable to find oid attribute in " + initialTable);
    }

    private IAlgebraOperator generateInitialTable(String tableName, List<AttributeRef> affectedAttributes, List<AttributeRef> nonAffectedAttributes, IDatabase deltaDB, List<AttributeRef> deltaTableAttributes, String stepId, boolean materialize) {
        for (Iterator<AttributeRef> it = affectedAttributes.iterator(); it.hasNext();) {
            AttributeRef attributeRef = it.next();
            if (isNotAffected(attributeRef, deltaDB)) {
                it.remove();
                nonAffectedAttributes.add(attributeRef);
            }
        }
        IAlgebraOperator initialTable;
        if (!nonAffectedAttributes.isEmpty()) {
            String tableNameForNonAffected = tableName + LunaticConstants.NA_TABLE_SUFFIX;
            Scan scan = new Scan(new TableAlias(tableNameForNonAffected));
            initialTable = scan;
            deltaTableAttributes.add(new AttributeRef(tableNameForNonAffected, LunaticConstants.TID));
            for (AttributeRef attributeRef : nonAffectedAttributes) {
                deltaTableAttributes.add(new AttributeRef(tableNameForNonAffected, attributeRef.getName()));
            }
        } else {
            AttributeRef firstAttributeRef = affectedAttributes.remove(0);
            nonAffectedAttributes.add(firstAttributeRef);
            deltaTableAttributes.add(new AttributeRef(ChaseUtility.getDeltaRelationName(tableName, firstAttributeRef.getName()), LunaticConstants.TID));
            deltaTableAttributes.add(new AttributeRef(new TableAlias(ChaseUtility.getDeltaRelationName(tableName, firstAttributeRef.getName()), "0"), firstAttributeRef.getName()));
            initialTable = buildTreeForAttribute(firstAttributeRef, stepId, materialize);
        }
        return initialTable;
    }

    private IAlgebraOperator buildTreeForAttribute(AttributeRef attribute, String stepId, boolean materialize) {
        // select * from R_A where step
        TableAlias table = new TableAlias(ChaseUtility.getDeltaRelationName(attribute.getTableName(), attribute.getName()));
        Scan tableScan = new Scan(table);
        Expression stepExpression = new Expression("startswith(" + stepId + ", " + LunaticConstants.STEP + ")");
        stepExpression.changeVariableDescription(LunaticConstants.STEP, new AttributeRef(table, LunaticConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef oid = new AttributeRef(table, LunaticConstants.TID);
        AttributeRef step = new AttributeRef(table, LunaticConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on oid, step
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, step}));
        AttributeRef oidInAlias = new AttributeRef(alias, LunaticConstants.TID);
        AttributeRef stepInAlias = new AttributeRef(alias, LunaticConstants.STEP);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oidInAlias, stepInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        // select oid, A from (join)
        AttributeRef attributeInAlias = new AttributeRef(alias, attribute.getName());
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, attributeInAlias}));
        Project project = new Project(projectionAttributes);
        project.addChild(join);
        String cleanStepId = stepId.replaceAll("\\.", "_");
        cleanStepId = cleanStepId.replaceAll("\"", "");
        if (useHash) {
            cleanStepId = getHash(cleanStepId);
        }
        IAlgebraOperator resultOperator;
        if (materialize) {
            String tableName = "tmp_" + DBMSUtility.attributeRefToAliasSQL(attribute) + "_" + cleanStepId;
            String tableAlias = DBMSUtility.attributeRefToAliasSQL(attribute);
            CreateTable createTable = new CreateTable(tableName, tableAlias, false);
            createTable.addChild(project);
            resultOperator = createTable;
        } else {
            resultOperator = project;
        }
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for attribute: " + attribute + "\n" + resultOperator);
        return resultOperator;
    }

    private IAlgebraOperator buildAlgebraTreeForTable(ITable table, List<AttributeRef> affectedAttributes, List<AttributeRef> nonAffectedAttributes, String stepId, AttributeRef oidAttributeRef, IAlgebraOperator deltaForFirstAttribute, List<AttributeRef> deltaTableAttributes, boolean materialize, boolean distinct) {
        IAlgebraOperator leftChild = deltaForFirstAttribute;
        for (AttributeRef attributeRef : affectedAttributes) {
            deltaTableAttributes.add(new AttributeRef(new TableAlias(ChaseUtility.getDeltaRelationName(table.getName(), attributeRef.getName()), "0"), attributeRef.getName()));
            IAlgebraOperator deltaForAttribute = buildTreeForAttribute(attributeRef, stepId, materialize);
            AttributeRef oid = new AttributeRef(ChaseUtility.getDeltaRelationName(table.getName(), attributeRef.getName()), LunaticConstants.TID);
            Join join = new Join(oidAttributeRef, oid);
            join.addChild(leftChild);
            join.addChild(deltaForAttribute);
            leftChild = join;
        }
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>();
        projectionAttributes.add(new AttributeRef(table.getName(), LunaticConstants.OID));
        projectionAttributes.addAll(nonAffectedAttributes);
        projectionAttributes.addAll(affectedAttributes);
        sortAttributes(deltaTableAttributes, table.getAttributes());
        sortAttributes(projectionAttributes, table.getAttributes());
        if (distinct) {
            removeOIDAttribute(deltaTableAttributes);
            removeOIDAttribute(projectionAttributes);
        }
        Project project = new Project(deltaTableAttributes, projectionAttributes, true);
        project.addChild(leftChild);
        RestoreOIDs restore = new RestoreOIDs(new AttributeRef(table.getName(), LunaticConstants.OID));
        restore.addChild(project);
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for table: " + table.getName() + "\n" + restore);
        return restore;
    }

    private List<AttributeRef> buildAttributeRefs(ITable table) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Attribute attribute : getAttributes(table)) {
            result.add(new AttributeRef(table.getName(), attribute.getName()));
        }
        return result;
    }

    private List<AttributeRef> cleanNames(List<AttributeRef> nonAffectedAttributes) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : nonAffectedAttributes) {
            String tableName = attributeRef.getTableName();
            tableName = tableName.replaceAll(LunaticConstants.NA_TABLE_SUFFIX, "");
            String attributeName = attributeRef.getName();
            if (attributeName.equals(LunaticConstants.TID)) {
                attributeName = LunaticConstants.OID;
            }
            result.add(new AttributeRef(tableName, attributeName));
        }
        return result;
    }

    private boolean isNotAffected(AttributeRef attributeRef, IDatabase deltaDB) {
        List<String> tableNames = deltaDB.getTableNames();
        String deltaRelation = ChaseUtility.getDeltaRelationName(attributeRef.getTableName(), attributeRef.getName());
        for (String tableName : tableNames) {
            if (tableName.equalsIgnoreCase(deltaRelation)) {
                return false;
            }
        }
        return true;
    }

    private List<Attribute> getAttributes(ITable table) {
        List<Attribute> result = new ArrayList<Attribute>();
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(LunaticConstants.OID)) {
                continue;
            }
            result.add(attribute);
        }
        return result;
    }

    private String getHash(String cleanStepId) {
        return Math.abs(cleanStepId.hashCode()) + "";
    }

    private void sortAttributes(List<AttributeRef> unsortedAttributes, List<Attribute> attributes) {
        if (unsortedAttributes.isEmpty()) {
            return;
        }
        List<AttributeRef> sortedAttributes = new ArrayList<AttributeRef>();
        sortedAttributes.add(unsortedAttributes.remove(0));
        for (Attribute attributeToSearch : attributes) {
            for (AttributeRef attributeRef : unsortedAttributes) {
                if (attributeRef.getName().equalsIgnoreCase(attributeToSearch.getName())) {
                    sortedAttributes.add(attributeRef);
                }
            }
        }
        unsortedAttributes.clear();
        unsortedAttributes.addAll(sortedAttributes);
    }

    private void removeOIDAttribute(List<AttributeRef> attributes) {
        for (Iterator<AttributeRef> it = attributes.iterator(); it.hasNext();) {
            AttributeRef attributeRef = it.next();
            if (attributeRef.getName().equals(LunaticConstants.OID)
                    || attributeRef.getName().equals(LunaticConstants.TID)) {
                it.remove();
            }
        }
    }

}
