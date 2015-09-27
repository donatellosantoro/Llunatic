package it.unibas.lunatic.core;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.SpeedyConstants;
import speedy.model.algebra.Difference;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.MaxAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import speedy.model.expressions.Expression;
import speedy.utility.SpeedyUtility;

public class GenerateModifiedCells {

    private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(GenerateModifiedCells.class.getName());
    private IRunQuery queryRunner;
    private int counter;
    private boolean useCellGroup;

    public GenerateModifiedCells(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    public List<String> generate(DeltaChaseStep result) throws IOException {
        counter = 0;
        useCellGroup = false;
        List<String> results = new ArrayList<String>();
        visitForSolutions(result, results);
        return results;
    }

    public void generate(DeltaChaseStep result, String fileName) throws IOException {
        List<String> results = generate(result);
        saveResults(results, fileName);
    }

//    public void generateCellGroup(DeltaChaseStep result) {
//        counter = 0;
//        useCellGroup = true;
//        visitForSolutions(result);
//    }
    private void visitForSolutions(DeltaChaseStep step, List<String> results) {
        if (step.isLeaf() && !step.isDuplicate() && !step.isInvalid()) {
            results.add(generateModifiedCellsForStep(step));
        } else {
            for (DeltaChaseStep child : step.getChildren()) {
                visitForSolutions(child, results);
            }
        }
    }

    private String generateModifiedCellsForStep(DeltaChaseStep step) {
        StringBuilder result = new StringBuilder();
        result.append("+++++++++++++++  Solution ").append(++counter).append(" +++++++++++++++\n");
        IDatabase originalDB = step.getOriginalDB();
        IDatabase deltaDB = step.getDeltaDB();
        List<String> deltaTableNames = deltaDB.getTableNames();
        for (String tableName : originalDB.getTableNames()) {
            ITable table = originalDB.getTable(tableName);
            for (Attribute attribute : table.getAttributes()) {
                String deltaRelationName = ChaseUtility.getDeltaRelationName(tableName, attribute.getName());
                if (!deltaTableNames.contains(deltaRelationName)) {
                    continue; //Non affected
                }
                ITable deltaTable = deltaDB.getTable(deltaRelationName);
                result.append(generateModifiedCellsForTable(deltaTable, attribute, step.getDeltaDB(), step.getId()));
            }
        }
        return result.toString();
    }

    private String generateModifiedCellsForTable(ITable table, Attribute attribute, IDatabase deltaDB, String stepId) {
        StringBuilder result = new StringBuilder();
        IAlgebraOperator operator = buildOperatorForModifiedCells(table, attribute, stepId);
        ITupleIterator iterator = queryRunner.run(operator, null, deltaDB);
        AttributeRef oidAttribute = new AttributeRef(new TableAlias(table.getName()), SpeedyConstants.TID);
        AttributeRef valueAttribute = new AttributeRef(new TableAlias(table.getName(), "0"), attribute.getName());
        Map<IValue, List<String>> cellGroups = new HashMap<IValue, List<String>>();
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            IValue tupleOID = tuple.getCell(oidAttribute).getValue();
            IValue value = tuple.getCell(valueAttribute).getValue();
            if (useCellGroup && value instanceof LLUNValue) {
                addCellGroup(cellGroups, value, tupleOID.toString() + "." + attribute.getName());
            } else {
                result.append(tupleOID.toString()).append(".").append(attribute.getName()).append(",,").append(value).append("\n");
            }
        }
        for (IValue value : cellGroups.keySet()) {
            result.append(value).append(",");
            for (String string : cellGroups.get(value)) {
                result.append(string).append(",");
            }
            LunaticUtility.removeChars(",".length(), result);
            result.append("\n");
        }
        iterator.close();
        return result.toString();
    }

    private void addCellGroup(Map<IValue, List<String>> cellGroups, IValue value, String string) {
        List<String> cellStrings = cellGroups.get(value);
        if (cellStrings == null) {
            cellStrings = new ArrayList<String>();
            cellGroups.put(value, cellStrings);
        }
        cellStrings.add(string);
    }

    private IAlgebraOperator buildOperatorForModifiedCells(ITable table, Attribute attribute, String stepId) {
        stepId = "\"" + stepId + "\"";
        // select * from R_A where step
        TableAlias tableAlias = new TableAlias(table.getName());
        Scan tableScan = new Scan(tableAlias);
        Expression stepExpression = new Expression("startswith(" + stepId + ", " + SpeedyConstants.STEP + ")");
        stepExpression.setVariableDescription(SpeedyConstants.STEP, new AttributeRef(tableAlias, SpeedyConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef oid = new AttributeRef(tableAlias, SpeedyConstants.TID);
        AttributeRef step = new AttributeRef(tableAlias, SpeedyConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(tableAlias.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on oid, step
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, step}));
        AttributeRef oidInAlias = new AttributeRef(alias, SpeedyConstants.TID);
        AttributeRef stepInAlias = new AttributeRef(alias, SpeedyConstants.STEP);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oidInAlias, stepInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        // select oid, A from (join)
        AttributeRef attributeInAlias = new AttributeRef(alias, attribute.getName());
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, attributeInAlias}));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(projectionAttributes));
        project.addChild(join);
        // except oid, A from ('r') 
        Scan rootScan = new Scan(tableAlias);
        Expression rootExpression = new Expression("\"r\" == " + SpeedyConstants.STEP);
        rootExpression.setVariableDescription(SpeedyConstants.STEP, new AttributeRef(tableAlias, SpeedyConstants.STEP));
        Select rootSelect = new Select(rootExpression);
        rootSelect.addChild(rootScan);
        AttributeRef rootAttributeInAlias = new AttributeRef(tableAlias, attribute.getName());
        List<AttributeRef> rootProjectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, rootAttributeInAlias}));
        Project rootProject = new Project(SpeedyUtility.createProjectionAttributes(rootProjectionAttributes));
        rootProject.addChild(rootSelect);
        Difference difference = new Difference();
        difference.addChild(project);
        difference.addChild(rootProject);
        return difference;
    }

    private void saveResults(List<String> results, String fileName) throws IOException {
        File outputFile = new File(fileName);
        outputFile.getParentFile().mkdirs();
        FileWriter printer = new FileWriter(outputFile);
        for (String result : results) {
            printer.write(result);
            printer.write("\n");
        }
        printer.close();
    }
}
