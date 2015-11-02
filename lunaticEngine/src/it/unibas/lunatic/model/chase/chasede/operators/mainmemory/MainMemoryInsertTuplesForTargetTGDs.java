package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertTuplesForTargetTGDs;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.operators.IRunQuery;

public class MainMemoryInsertTuplesForTargetTGDs implements IInsertTuplesForTargetTGDs {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryInsertTuplesForTargetTGDs.class);

    private IInsertTuple insertOperator;
    private IRunQuery queryRunner;
    private IOccurrenceHandler occurrenceHandler;
    private IOIDGenerator oidGenerator;

    public MainMemoryInsertTuplesForTargetTGDs(IInsertTuple insertOperator, IRunQuery queryRunner, IOccurrenceHandler occurrenceHandler, IOIDGenerator oidGenerator) {
        this.insertOperator = insertOperator;
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.oidGenerator = oidGenerator;
    }

    @Override
    public boolean execute(IAlgebraOperator violationQuery, DeltaChaseStep currentNode, Dependency tgd, Scenario scenario, IDatabase databaseForStep) {
        Map<AttributeRef, IValueGenerator> targetGenerators = tgd.getTargetGenerators();
        if (logger.isDebugEnabled()) logger.debug("----Executing insert. Tgd generator map: " + LunaticUtility.printMap(targetGenerators));
        if (logger.isDebugEnabled()) logger.debug("----Violation query: " + violationQuery);
        List<TableAlias> targetTablesToInsert = findTableAliases(targetGenerators);
        if (logger.isDebugEnabled()) logger.debug("----Tables to insert: " + targetTablesToInsert);
        if (logger.isDebugEnabled()) logger.debug("Executing violation query...");
        ITupleIterator it = queryRunner.run(violationQuery, scenario.getSource(), databaseForStep);
        boolean insertedTuple = it.hasNext();
        while (it.hasNext()) {
            Tuple premiseTuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("----Premise tuple: " + premiseTuple);
            for (TableAlias tableAlias : targetTablesToInsert) {
                if (logger.isDebugEnabled()) logger.debug("----Inserting into table: " + tableAlias);
                ITable table = currentNode.getOriginalDB().getTable(tableAlias.getTableName());
                TupleOID newTupleOID = new TupleOID(oidGenerator.getNextOID(table.getName()));
                if (logger.isDebugEnabled()) logger.debug("New tuple oid: " + newTupleOID);
                for (Attribute attribute : table.getAttributes()) {
                    if (scenario.isDBMS() && attribute.getName().equals(SpeedyConstants.OID)) {
                        continue;
                    }
                    String deltaTableName = ChaseUtility.getDeltaRelationName(table.getName(), attribute.getName());
                    if (logger.isDebugEnabled()) logger.debug("----Inserting into delta table: " + deltaTableName);
                    IValue attributeValue = computeValue(tableAlias, attribute, targetGenerators, premiseTuple);
                    Tuple targetTuple = buildTargetTuple(deltaTableName, newTupleOID, attribute.getName(), attributeValue, currentNode.getId());
                    if (logger.isDebugEnabled()) logger.debug("----Target tuple: " + targetTuple);
                    ITable deltaTable = currentNode.getDeltaDB().getTable(deltaTableName);
                    insertOperator.execute(deltaTable, targetTuple, scenario.getSource(), scenario.getTarget());
                    if (attributeValue instanceof NullValue) {
                        CellRef cellRef = new CellRef(newTupleOID, new AttributeRef(attribute.getTableName(), attribute.getName()));
                        CellGroupCell cellGroupCell = new CellGroupCell(cellRef, attributeValue, null, LunaticConstants.TYPE_OCCURRENCE, true);
                        occurrenceHandler.saveCellGroupCell(currentNode.getDeltaDB(), attributeValue, cellGroupCell, currentNode.getId(), scenario);
                    }
                }
            }
        }
        it.close();
        if (logger.isDebugEnabled()) logger.debug("TGD repair terminated");
        return insertedTuple;
    }

    private List<TableAlias> findTableAliases(Map<AttributeRef, IValueGenerator> targetGenerators) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (AttributeRef attributeRef : targetGenerators.keySet()) {
            TableAlias tableAlias = attributeRef.getTableAlias();
            LunaticUtility.addIfNotContained(result, tableAlias);
        }
        return result;
    }

    private IValue computeValue(TableAlias tableAlias, Attribute attribute, Map<AttributeRef, IValueGenerator> targetGenerators, Tuple sourceTuple) {
        AttributeRef attributeRef = new AttributeRef(tableAlias, attribute.getName());
        IValueGenerator generator = targetGenerators.get(attributeRef);
        return generator.generateValue(sourceTuple);
    }

    private Tuple buildTargetTuple(String deltaTableName, TupleOID newTupleOID, String attributeName, IValue attributeValue, String id) {
        TupleOID tupleOID = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(tupleOID);
        Cell tidCell = new Cell(tupleOID, new AttributeRef(deltaTableName, SpeedyConstants.TID), new ConstantValue(newTupleOID));
        tuple.addCell(tidCell);
        Cell stepCell = new Cell(tupleOID, new AttributeRef(deltaTableName, SpeedyConstants.STEP), new ConstantValue(id));
        tuple.addCell(stepCell);
        Cell value = new Cell(tupleOID, new AttributeRef(deltaTableName, attributeName), attributeValue);
        tuple.addCell(value);
        return tuple;
    }

    @Override
    public void initializeOIDs(IDatabase database, Scenario scenario) {
        oidGenerator.initializeOIDs(database, scenario);
    }
}
