package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import speedy.SpeedyConstants;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.Project;
import speedy.model.algebra.ProjectionAttribute;
import speedy.model.algebra.Scan;
import speedy.model.algebra.aggregatefunctions.CountAggregateFunction;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ITable;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;

public class CheckRedundancy {

    private IRunQuery queryRunner;

    public void checkRedundancyInAuthoritativeTables(Scenario scenario) {
        initializeOperator(scenario);
        if (scenario.getAuthoritativeSources().isEmpty()) {
            return;
        }
        long start = new Date().getTime();
        for (String authoritativeSource : scenario.getAuthoritativeSources()) {
            checkRedundancyInTable(authoritativeSource, scenario);
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.CHECK_REDUNDANCY_TIME, end - start);
    }

    public void checkDuplicateOIDs(Scenario scenario) {
        initializeOperator(scenario);
        long start = new Date().getTime();
        for (String sourceTable : scenario.getSource().getTableNames()) {
            checkDuplicateOIDsInTable(sourceTable, true, scenario);
        }
        for (String targetTable : scenario.getTarget().getTableNames()) {
            checkDuplicateOIDsInTable(targetTable, false, scenario);
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.CHECK_REDUNDANCY_TIME, end - start);
    }

    private void checkRedundancyInTable(String authoritativeTable, Scenario scenario) {
        ITable table = scenario.getSource().getTable(authoritativeTable);
        long totalSize = table.getSize();
        long distinctSize = countDistinct(table, scenario);
        if (totalSize != distinctSize) {
            throw new ChaseException("Authoritative table " + authoritativeTable + " contains duplicate tuples. Total size: " + totalSize + ". Distinct tuples: " + distinctSize);
        }
    }

    private long countDistinct(ITable table, Scenario scenario) {
        TableAlias tableAlias = new TableAlias(table.getName(), true);
        List<ProjectionAttribute> countProjection = new ArrayList<ProjectionAttribute>();
        AttributeRef countAttributeRef = new AttributeRef(tableAlias, SpeedyConstants.COUNT);
        countProjection.add(new ProjectionAttribute(new CountAggregateFunction(countAttributeRef)));
        Project projectCount = new Project(countProjection);
        List<ProjectionAttribute> attributesWithoutOID = new ArrayList<ProjectionAttribute>();
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(SpeedyConstants.OID)) {
                continue;
            }
            attributesWithoutOID.add(new ProjectionAttribute(new AttributeRef(tableAlias, attribute.getName())));
        }
        Project projectNoOID = new Project(attributesWithoutOID);
        projectNoOID.addChild(new Scan(tableAlias));
        Distinct distinct = new Distinct();
        distinct.addChild(projectNoOID);
        projectCount.addChild(distinct);
        ITupleIterator it = queryRunner.run(projectCount, scenario.getSource(), scenario.getTarget());
        Tuple countTuple = it.next();
        Cell countCell = countTuple.getCell(countAttributeRef);
        Long countValue = Long.parseLong(countCell.getValue().toString());
        it.close();
        return countValue;
    }

    private void checkDuplicateOIDsInTable(String tableName, boolean source, Scenario scenario) {
        if(scenario.isMainMemory()){
            return;
        }
        ITable table;
        if (source) {
            table = scenario.getSource().getTable(tableName);
        } else {
            table = scenario.getTarget().getTable(tableName);
        }
        long totalSize = table.getSize();
        long distinctOIDs = countDistinctOIDs(table, source, scenario);
        if (totalSize != distinctOIDs) {
            throw new ChaseException("Table " + tableName + " contains duplicate oids. Total size: " + totalSize + ". Distinct oids: " + distinctOIDs);
        }
    }

    private long countDistinctOIDs(ITable table, boolean source, Scenario scenario) {
        TableAlias tableAlias = new TableAlias(table.getName(), source);
        List<ProjectionAttribute> countProjection = new ArrayList<ProjectionAttribute>();
        AttributeRef countAttributeRef = new AttributeRef(tableAlias, SpeedyConstants.COUNT);
        countProjection.add(new ProjectionAttribute(new CountAggregateFunction(countAttributeRef)));
        Project projectCount = new Project(countProjection);
        List<ProjectionAttribute> oidAttributes = new ArrayList<ProjectionAttribute>();
        oidAttributes.add(new ProjectionAttribute(new AttributeRef(tableAlias, SpeedyConstants.OID)));
        Project projectOID = new Project(oidAttributes);
        projectOID.addChild(new Scan(tableAlias));
        Distinct distinct = new Distinct();
        distinct.addChild(projectOID);
        projectCount.addChild(distinct);
        ITupleIterator it = queryRunner.run(projectCount, scenario.getSource(), scenario.getTarget());
        Tuple countTuple = it.next();
        Cell countCell = countTuple.getCell(countAttributeRef);
        Long countValue = Long.parseLong(countCell.getValue().toString());
        it.close();
        return countValue;
    }

    private void initializeOperator(Scenario scenario) {
        this.queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
    }

}
