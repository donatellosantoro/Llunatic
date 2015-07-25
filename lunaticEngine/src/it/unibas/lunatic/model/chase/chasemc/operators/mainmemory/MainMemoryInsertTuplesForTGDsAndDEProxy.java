package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.IInsertTuplesForTGDsAndDEProxy;
import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMemoryInsertTuplesForTGDsAndDEProxy implements IInsertTuplesForTGDsAndDEProxy {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryInsertTuplesForTGDsAndDEProxy.class);

    private IInsertTuple insertOperator;
    private IRunQuery queryRunner;
    private OccurrenceHandlerMC occurrenceHandler;
    private IOIDGenerator oidGenerator;

    public MainMemoryInsertTuplesForTGDsAndDEProxy(IInsertTuple insertOperator, IRunQuery queryRunner, OccurrenceHandlerMC occurrenceHandler, IOIDGenerator oidGenerator) {
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
                    if (scenario.isDBMS() && attribute.getName().equals(LunaticConstants.OID)) {
                        continue;
                    }
                    String deltaTableName = ChaseUtility.getDeltaRelationName(table.getName(), attribute.getName());
                    if (logger.isDebugEnabled()) logger.debug("----Inserting into delta table: " + deltaTableName);
                    IValue attributeValue = computeValue(tableAlias, attribute, targetGenerators, premiseTuple);
                    Tuple targetTuple = buildTargetTupleForDeltaDB(deltaTableName, newTupleOID, attribute.getName(), attributeValue, currentNode.getId());
                    if (logger.isDebugEnabled()) logger.debug("----Target tuple: " + targetTuple);
                    ITable deltaTable = currentNode.getDeltaDB().getTable(deltaTableName);
                    insertOperator.execute(deltaTable, targetTuple, scenario.getSource(), scenario.getTarget());
                    //TODO DE Check
                    updateOccurrencesForNewTuple(targetTuple, attributeValue, databaseForStep, deltaTableName, deltaTableName, scenario);
                }
            }
        }
        it.close();
        if (logger.isDebugEnabled()) logger.debug("TGD repair terminated");
        return insertedTuple;
    }

    public void updateOccurrencesForNewTuple(Tuple tuple, IValue cellValue, IDatabase deltaDB, String tableName, String attributeName, Scenario scenario) {
        IValue groupId = CellGroupIDGenerator.generateNewId(cellValue);
        IValue tid = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.TID);
        String stepId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.STEP).toString();
        CellRef cellRef = new CellRef(new TupleOID(tid), new AttributeRef(tableName, attributeName));
        CellGroupCell cellGroupCell = new CellGroupCell(cellRef, tid, cellValue, LunaticConstants.TYPE_OCCURRENCE, true);
        occurrenceHandler.saveCellGroupCell(deltaDB, groupId, cellGroupCell, stepId, scenario);
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

    private Tuple buildTargetTupleForDeltaDB(String deltaTableName, TupleOID newTupleOID, String attributeName, IValue attributeValue, String id) {
        TupleOID tupleOID = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(tupleOID);
        Cell tidCell = new Cell(tupleOID, new AttributeRef(deltaTableName, LunaticConstants.TID), new ConstantValue(newTupleOID));
        tuple.addCell(tidCell);
        Cell stepCell = new Cell(tupleOID, new AttributeRef(deltaTableName, LunaticConstants.STEP), new ConstantValue(id));
        tuple.addCell(stepCell);
        Cell value = new Cell(tupleOID, new AttributeRef(deltaTableName, attributeName), attributeValue);
        tuple.addCell(value);
        return tuple;
    }

    @Override
    public void initializeOIDs(IDatabase database) {
        oidGenerator.initializeOIDs(database);
    }
}
