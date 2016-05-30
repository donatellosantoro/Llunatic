package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.AbstractBuildDeltaDB;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.OID;
import speedy.model.database.mainmemory.datasource.nodes.AttributeNode;
import speedy.model.database.mainmemory.datasource.nodes.LeafNode;
import speedy.model.database.mainmemory.datasource.nodes.SetNode;
import speedy.model.database.mainmemory.datasource.nodes.TupleNode;
import speedy.persistence.PersistenceConstants;
import speedy.persistence.Types;

public class BuildMainMemoryDeltaDBDE extends AbstractBuildDeltaDB {

    private static Logger logger = LoggerFactory.getLogger(BuildMainMemoryDeltaDBDE.class);

    @Override
    public MainMemoryDB generate(IDatabase database, Scenario scenario, String rootName) {
        long start = new Date().getTime();
        Set<AttributeRef> affectedAttributes = findAllAffectedAttributesForDEScenario(scenario);
        if (logger.isDebugEnabled()) logger.debug("Affected attributes " + affectedAttributes);
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

    private void generateSchema(INode schemaNode, MainMemoryDB database, Set<AttributeRef> affectedAttributes) {
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
                    tupleNodeSchema.addChild(createAttributeSchema(SpeedyConstants.STEP));
                    tupleNodeSchema.addChild(createAttributeSchema(SpeedyConstants.TID));
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
        createCellGroupTable(schemaNode);
    }

    private void createTableForNonAffected(INode schemaNode, String tableName, List<Attribute> tableNonAffectedAttributes) {
        String deltaRelationName = tableName + LunaticConstants.NA_TABLE_SUFFIX;
        INode setNodeSchema = new SetNode(deltaRelationName);
        schemaNode.addChild(setNodeSchema);
        TupleNode tupleNodeSchema = new TupleNode(deltaRelationName + "Tuple");
        setNodeSchema.addChild(tupleNodeSchema);
        tupleNodeSchema.addChild(createAttributeSchema(SpeedyConstants.TID));
        for (Attribute attribute : tableNonAffectedAttributes) {
            tupleNodeSchema.addChild(createAttributeSchema(attribute.getName()));
        }
    }

    private void createCellGroupTable(INode schemaNode) {
        INode cellGroupTableSet = new SetNode(LunaticConstants.CELLGROUP_TABLE);
        TupleNode occurrenceTuple = new TupleNode(LunaticConstants.CELLGROUP_TABLE + "Tuple");
        cellGroupTableSet.addChild(occurrenceTuple);
        occurrenceTuple.addChild(createAttributeSchema(SpeedyConstants.STEP));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.GROUP_ID));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_OID));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_TABLE));
        occurrenceTuple.addChild(createAttributeSchema(LunaticConstants.CELL_ATTRIBUTE));
        schemaNode.addChild(cellGroupTableSet);
    }

    private AttributeNode createAttributeSchema(String attributeName) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName);
        LeafNode leafNodeInstance = new LeafNode(Types.STRING);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private void generateInstance(MainMemoryDB deltaDB, MainMemoryDB database, String rootName, Set<AttributeRef> affectedAttributes) {
        DataSource dataSource = deltaDB.getDataSource();
        INode instanceNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        instanceNode.setRoot(true);
        instanceNode.addChild(new SetNode(LunaticConstants.CELLGROUP_TABLE, IntegerOIDGenerator.getNextOID()));
        insertTargetTablesIntoDeltaDB(database, instanceNode, affectedAttributes, rootName);
//        dataSource.addInstanceWithCheck(instanceNode);
        dataSource.addInstance(instanceNode);
    }

    private void insertTargetTablesIntoDeltaDB(MainMemoryDB database, INode instanceNode, Set<AttributeRef> affectedAttributes, String rootName) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            initInstanceNode(table, instanceNode, affectedAttributes);
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                TupleOID tupleOID = tuple.getOid();
                List<Cell> nonAffectedCells = new ArrayList<Cell>();
                for (Cell cell : tuple.getCells()) {
                    if (cell.getAttribute().equals(SpeedyConstants.OID)) {
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
                        tupleNodeInstance.addChild(createAttributeInstance(SpeedyConstants.TID, tupleOID));
                        tupleNodeInstance.addChild(createAttributeInstance(SpeedyConstants.STEP, rootName));
                        IValue value = cell.getValue();
                        tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), value));
                        if (value instanceof NullValue && ((NullValue) value).isLabeledNull()) {
                            CellRef cellRef = new CellRef(tupleOID, new AttributeRef(table.getName(), cell.getAttribute()));
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
    }

    private void initInstanceNode(ITable table, INode instanceNode, Set<AttributeRef> affectedAttributes) {
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(SpeedyConstants.OID)) {
                continue;
            }
            if (isAffected(new AttributeRef(attribute.getTableName(), attribute.getName()), affectedAttributes)) {
                String deltaRelationName = ChaseUtility.getDeltaRelationName(table.getName(), attribute.getName());
                INode setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
                instanceNode.addChild(setNodeInstance);
            }
        }
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
        tupleNodeInstance.addChild(createAttributeInstance(SpeedyConstants.TID, tupleOID));
        for (Cell cell : nonAffectedCells) {
            tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), cell.getValue()));
        }
        setNodeInstance.addChild(tupleNodeInstance);
    }

    private void addTupleForNullOccurrence(IValue value, CellRef cellRef, INode instanceNode) {
        INode nullInsertSet = getSetNodeInstance(LunaticConstants.CELLGROUP_TABLE, instanceNode);
        TupleNode nullInsertTuple = new TupleNode(LunaticConstants.CELLGROUP_TABLE + "Tuple", IntegerOIDGenerator.getNextOID());
        nullInsertSet.addChild(nullInsertTuple);
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.GROUP_ID, value));
        nullInsertTuple.addChild(createAttributeInstance(SpeedyConstants.STEP, LunaticConstants.CHASE_STEP_ROOT));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_OID, cellRef.getTupleOID()));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_TABLE, cellRef.getAttributeRef().getTableName()));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_ATTRIBUTE, cellRef.getAttributeRef().getName()));
        nullInsertTuple.addChild(createAttributeInstance(LunaticConstants.CELL_TYPE, LunaticConstants.TYPE_OCCURRENCE));
    }

}
