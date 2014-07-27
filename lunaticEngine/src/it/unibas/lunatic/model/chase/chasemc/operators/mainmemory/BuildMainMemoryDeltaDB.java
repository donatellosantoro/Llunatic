package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.persistence.PersistenceConstants;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.AbstractBuildDeltaDB;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.mainmemory.datasource.DataSource;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.AttributeNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.LeafNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.SetNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.TupleNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.database.mainmemory.datasource.OID;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.persistence.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildMainMemoryDeltaDB extends AbstractBuildDeltaDB {

    private static Logger logger = LoggerFactory.getLogger(BuildMainMemoryDeltaDB.class);

    @Override
    public MainMemoryDB generate(IDatabase database, Scenario scenario, String rootName) {
        long start = new Date().getTime();
        List<AttributeRef> affectedAttributes = findAllAffectedAttributes(scenario);
//        List<AttributeRef> nonAffectedAttributes = findNonAffectedAttributes(scenario, affectedAttributes);
        INode schemaNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        schemaNode.setRoot(true);
        generateSchema(schemaNode, (MainMemoryDB) database, affectedAttributes);
        DataSource deltaDataSource = new DataSource(PersistenceConstants.TYPE_META_INSTANCE, schemaNode);
        MainMemoryDB deltaDB = new MainMemoryDB(deltaDataSource);
        generateInstance(deltaDB, (MainMemoryDB) database, rootName, affectedAttributes);
        if (logger.isDebugEnabled()) logger.debug("Delta DB:\n" + deltaDB.toString());
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DELTA_DB_BUILDER, end - start);
        return deltaDB;
    }

    private void generateSchema(INode schemaNode, MainMemoryDB database, List<AttributeRef> affectedAttributes) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            List<Attribute> tableNonAffectedAttributes = new ArrayList<Attribute>();
            for (Attribute attribute : table.getAttributes()) {
                if (isAffected(new AttributeRef(table.getName(), attribute.getName()), affectedAttributes)) {
                    String deltaRelationName = ChaseUtility.getDeltaRelationName(table.getName(), attribute.getName());
                    INode setNodeSchema = new SetNode(deltaRelationName);
                    schemaNode.addChild(setNodeSchema);
                    TupleNode tupleNodeSchema = new TupleNode(deltaRelationName + "Tuple");
                    setNodeSchema.addChild(tupleNodeSchema);
                    tupleNodeSchema.addChild(createAttributeSchema(LunaticConstants.STEP));
                    tupleNodeSchema.addChild(createAttributeSchema(LunaticConstants.TID));
                    tupleNodeSchema.addChild(createAttributeSchema(attribute.getName()));
                    tupleNodeSchema.addChild(createAttributeSchema(LunaticConstants.GROUP_ID));
                } else {
                    tableNonAffectedAttributes.add(attribute);
                }
            }
            if (!tableNonAffectedAttributes.isEmpty()) {
                createTableForNonAffected(schemaNode, table.getName(), tableNonAffectedAttributes);
            }
        }
        createOccurrenceTables(schemaNode);
    }

    private void createTableForNonAffected(INode schemaNode, String tableName, List<Attribute> tableNonAffectedAttributes) {
        String deltaRelationName = tableName + LunaticConstants.NA_TABLE_SUFFIX;
        INode setNodeSchema = new SetNode(deltaRelationName);
        schemaNode.addChild(setNodeSchema);
        TupleNode tupleNodeSchema = new TupleNode(deltaRelationName + "Tuple");
        setNodeSchema.addChild(tupleNodeSchema);
        tupleNodeSchema.addChild(createAttributeSchema(LunaticConstants.TID));
        for (Attribute attribute : tableNonAffectedAttributes) {
            tupleNodeSchema.addChild(createAttributeSchema(attribute.getName()));
        }
    }

    private void createOccurrenceTables(INode schemaNode) {
        INode occurrenceSet = new SetNode(LunaticConstants.OCCURRENCE_TABLE);
        TupleNode occurrenceTuple = new TupleNode(LunaticConstants.OCCURRENCE_TABLE + "Tuple");
        occurrenceSet.addChild(occurrenceTuple);
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.STEP));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.GROUP_ID));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_OID));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_TABLE));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_ATTRIBUTE));
        schemaNode.addChild(occurrenceSet);
        INode provenanceSet = new SetNode(LunaticConstants.PROVENANCE_TABLE);
        TupleNode provenanceTuple = new TupleNode(LunaticConstants.PROVENANCE_TABLE + "Tuple");
        provenanceSet.addChild(provenanceTuple);
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.STEP));
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.GROUP_ID));
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_OID));
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_TABLE));
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_ATTRIBUTE));
        provenanceTuple.addChild(createAttributeSchema(LunaticConstants.PROVENANCE_CELL_VALUE));
        schemaNode.addChild(provenanceSet);
    }

    private AttributeNode createAttributeSchema(String attributeName) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName);
        LeafNode leafNodeInstance = new LeafNode(Types.STRING);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private void generateInstance(MainMemoryDB deltaDB, MainMemoryDB database, String rootName, List<AttributeRef> affectedAttributes) {
        DataSource dataSource = deltaDB.getDataSource();
        INode instanceNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        instanceNode.setRoot(true);
        initOccurrenceTables(instanceNode);
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            initInstanceNode(table, instanceNode, affectedAttributes);
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                TupleOID tupleOID = tuple.getOid();
                List<Cell> nonAffectedCells = new ArrayList<Cell>();
                for (Cell cell : tuple.getCells()) {
                    if (cell.getAttribute().equals(LunaticConstants.OID)) {
                        continue;
                    }
                    if (isAffected(cell.getAttributeRef(), affectedAttributes)) {
                        String deltaRelationName = ChaseUtility.getDeltaRelationName(table.getName(), cell.getAttribute());
                        INode setNodeInstance = getSetNodeInstance(deltaRelationName, instanceNode);
//                        if (setNodeInstance == null) {
//                            setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
//                            instanceNode.addChild(setNodeInstance);
//                        }
                        OID oid = IntegerOIDGenerator.getNextOID();
                        TupleNode tupleNodeInstance = new TupleNode(deltaRelationName + "Tuple", oid);
                        tupleNodeInstance.addChild(createAttributeInstance(LunaticConstants.TID, tupleOID));
                        tupleNodeInstance.addChild(createAttributeInstance(LunaticConstants.STEP, rootName));
                        IValue value = cell.getValue();
                        tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), value));
                        if (value instanceof NullValue && ((NullValue) value).isLabeledNull()) {
                            CellRef cellRef = new CellRef(new TupleOID(oid), new AttributeRef(table.getName(), cell.getAttribute()));
                            addTupleForNullOccurrence(value, cellRef, instanceNode);
                        }
                        setNodeInstance.addChild(tupleNodeInstance);
                    } else {
                        nonAffectedCells.add(cell);
                    }
                }
                if (!nonAffectedCells.isEmpty()) {
                    createTupleForNonAffectedCells(instanceNode, table.getName(), tupleOID, nonAffectedCells);
                }
            }
            it.close();
        }
        dataSource.addInstanceWithCheck(instanceNode);
    }

    private void initInstanceNode(ITable table, INode instanceNode, List<AttributeRef> affectedAttributes) {
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(LunaticConstants.OID)) {
                continue;
            }
            if (isAffected(new AttributeRef(attribute.getTableName(), attribute.getName()), affectedAttributes)) {
                String deltaRelationName = ChaseUtility.getDeltaRelationName(table.getName(), attribute.getName());
                INode setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
                instanceNode.addChild(setNodeInstance);
            }
        }
    }

    private void initOccurrenceTables(INode instanceNode) {
        instanceNode.addChild(new SetNode(LunaticConstants.OCCURRENCE_TABLE, IntegerOIDGenerator.getNextOID()));
        instanceNode.addChild(new SetNode(LunaticConstants.PROVENANCE_TABLE, IntegerOIDGenerator.getNextOID()));
    }

    private AttributeNode createAttributeInstance(String attributeName, Object value) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName, IntegerOIDGenerator.getNextOID());
        LeafNode leafNodeInstance = new LeafNode(Types.STRING, value);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private INode getSetNodeInstance(String deltaRelationName, INode instanceNode) {
        for (INode node : instanceNode.getChildren()) {
            if (node.getLabel().equals(deltaRelationName)) {
                return node;
            }
        }
        return null;
    }

    private void createTupleForNonAffectedCells(INode instanceNode, String tableName, TupleOID tupleOID, List<Cell> nonAffectedCells) {
        String deltaRelationName = tableName + LunaticConstants.NA_TABLE_SUFFIX;
        INode setNodeInstance = getSetNodeInstance(deltaRelationName, instanceNode);
        if (setNodeInstance == null) {
            setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
            instanceNode.addChild(setNodeInstance);
        }
        OID oid = IntegerOIDGenerator.getNextOID();
        TupleNode tupleNodeInstance = new TupleNode(deltaRelationName + "Tuple", oid);
        tupleNodeInstance.addChild(createAttributeInstance(LunaticConstants.TID, tupleOID));
        for (Cell cell : nonAffectedCells) {
            tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), cell.getValue()));
        }
        setNodeInstance.addChild(tupleNodeInstance);
    }

    private void addTupleForNullOccurrence(IValue value, CellRef cellRef, INode instanceNode) {
        INode nullInsertSet = getSetNodeInstance(LunaticConstants.OCCURRENCE_TABLE, instanceNode);
        TupleNode nullInsertTuple = new TupleNode(LunaticConstants.OCCURRENCE_TABLE + "Tuple", IntegerOIDGenerator.getNextOID());
        nullInsertSet.addChild(nullInsertTuple);
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.GROUP_ID, value));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.STEP, LunaticConstants.CHASE_STEP_ROOT));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_OID, cellRef.getTupleOID()));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_TABLE, cellRef.getAttributeRef().getTableName()));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_ATTRIBUTE, cellRef.getAttributeRef().getName()));
    }
}
