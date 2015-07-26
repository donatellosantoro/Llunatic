package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import it.unibas.lunatic.model.database.EmptyDB;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.mainmemory.datasource.DataSource;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.AttributeNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.LeafNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.SetNode;
import it.unibas.lunatic.model.database.mainmemory.datasource.nodes.TupleNode;
import it.unibas.lunatic.model.dependency.ConstantsInFormula;
import it.unibas.lunatic.persistence.PersistenceConstants;
import it.unibas.lunatic.persistence.Types;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMemoryCreateTableForConstants implements ICreateTablesForConstants {

    private static final Logger logger = LoggerFactory.getLogger(MainMemoryCreateTableForConstants.class.getName());
    
    public void createTable(ConstantsInFormula constantsInFormula, Scenario scenario) {
        if (scenario.getSource() instanceof EmptyDB) {
            MainMemoryDB newSource = createEmptySourceDatabase();
            scenario.setSource(newSource);
        }
        MainMemoryDB mainMemorySource = (MainMemoryDB) scenario.getSource();
        String tableName = constantsInFormula.getTableName();
        if (containsTable(mainMemorySource, tableName)) {
            return;
        }
        createSchema(tableName, mainMemorySource, constantsInFormula);
        createInstance(tableName, mainMemorySource, constantsInFormula);
    }

    private boolean containsTable(MainMemoryDB mainMemorySource, String tableName) {
        for (INode tableNode : mainMemorySource.getDataSource().getSchema().getChildren()) {
            if (tableNode.getLabel().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    private void createSchema(String tableName, MainMemoryDB mainMemorySource, ConstantsInFormula constantsInFormula) {
        INode setNodeSchema = new SetNode(tableName);
        mainMemorySource.getDataSource().getSchema().addChild(setNodeSchema);
        TupleNode tupleNodeSchema = new TupleNode(tableName + "Tuple");
        setNodeSchema.addChild(tupleNodeSchema);
        for (String attributeName : constantsInFormula.getAttributeNames()) {
            tupleNodeSchema.addChild(createAttributeSchema(attributeName));
        }
    }

    private AttributeNode createAttributeSchema(String attributeName) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName);
        LeafNode leafNodeInstance = new LeafNode(Types.STRING);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private void createInstance(String tableName, MainMemoryDB mainMemorySource, ConstantsInFormula constantsInFormula) {
        INode setNodeInstance = new SetNode(tableName, IntegerOIDGenerator.getNextOID());
        mainMemorySource.getDataSource().getInstances().get(0).addChild(setNodeInstance);
        TupleNode tupleNodeInstance = new TupleNode(tableName + "Tuple", IntegerOIDGenerator.getNextOID());
        setNodeInstance.addChild(tupleNodeInstance);
        List<String> attributeNames = constantsInFormula.getAttributeNames();
        List<Object> constantValues = constantsInFormula.getConstantValues();
        for (int i = 0; i < constantValues.size(); i++) {
            String attributeName = attributeNames.get(i);
            Object constantValue = constantValues.get(i);
            tupleNodeInstance.addChild(createAttributeInstance(attributeName, constantValue));
        }
    }

    private AttributeNode createAttributeInstance(String attributeName, Object value) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName, IntegerOIDGenerator.getNextOID());
        LeafNode leafNodeInstance = new LeafNode(Types.STRING, value);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private MainMemoryDB createEmptySourceDatabase() {
        INode schemaNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL);
        schemaNode.setRoot(true);
        DataSource dataSource = new DataSource(PersistenceConstants.TYPE_META_INSTANCE, schemaNode);
        INode instanceNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        instanceNode.setRoot(true);
        dataSource.addInstanceWithCheck(instanceNode);
        return new MainMemoryDB(dataSource);
    }
}
