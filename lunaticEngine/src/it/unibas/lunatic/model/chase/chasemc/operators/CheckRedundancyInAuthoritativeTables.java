package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import java.util.ArrayList;
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

public class CheckRedundancyInAuthoritativeTables {

    private IRunQuery queryRunner;

    public void check(Scenario scenario) {
        initializeOperator(scenario);
        if (scenario.getAuthoritativeSources().isEmpty()) {
            return;
        }
        for (String authoritativeSource : scenario.getAuthoritativeSources()) {
            checkRedundancyInTable(authoritativeSource, scenario);
        }
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

    private void initializeOperator(Scenario scenario) {
        this.queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
    }

}
