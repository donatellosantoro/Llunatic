package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import it.unibas.lunatic.model.dependency.AllConstantsInFormula;
import it.unibas.lunatic.model.dependency.ConstantInFormula;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.EmptyDB;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.nodes.AttributeNode;
import speedy.model.database.mainmemory.datasource.nodes.LeafNode;
import speedy.model.database.mainmemory.datasource.nodes.SetNode;
import speedy.model.database.mainmemory.datasource.nodes.TupleNode;
import speedy.persistence.PersistenceConstants;
import speedy.persistence.Types;

public class MainMemoryCreateTableForConstants implements ICreateTablesForConstants {

    private static final Logger logger = LoggerFactory.getLogger(MainMemoryCreateTableForConstants.class.getName());

    public void createTable(AllConstantsInFormula constantsInFormula, Scenario scenario, boolean autoritative) {
        if (scenario.getSource() instanceof EmptyDB) {
            MainMemoryDB newSource = createEmptySourceDatabase();
            scenario.setSource(newSource);
        }
        MainMemoryDB mainMemorySource = (MainMemoryDB) scenario.getSource();
        String tableNameForPremise = constantsInFormula.getTableNameForPremiseConstants();
        if (mainMemorySource.getTableNames().contains(tableNameForPremise)) {
            return;
        }
        createSchema(tableNameForPremise, mainMemorySource, constantsInFormula, true);
        createInstance(tableNameForPremise, mainMemorySource, constantsInFormula, true);
        if (autoritative) scenario.getAuthoritativeSources().add(tableNameForPremise);
        String tableNameForConclusion = constantsInFormula.getTableNameForConclusionConstants();
        if (mainMemorySource.getTableNames().contains(tableNameForConclusion)) {
            return;
        }
        createSchema(tableNameForConclusion, mainMemorySource, constantsInFormula, false);
        createInstance(tableNameForConclusion, mainMemorySource, constantsInFormula, false);
        if (autoritative) scenario.getAuthoritativeSources().add(tableNameForConclusion);
    }

    private void createSchema(String tableName, MainMemoryDB mainMemorySource, AllConstantsInFormula constantsInFormula, boolean premise) {
        INode setNodeSchema = new SetNode(tableName);
        mainMemorySource.getDataSource().getSchema().addChild(setNodeSchema);
        TupleNode tupleNodeSchema = new TupleNode(tableName + "Tuple");
        setNodeSchema.addChild(tupleNodeSchema);
        for (ConstantInFormula constant : constantsInFormula.getConstants(premise)) {
            String attributeName = DependencyUtility.buildAttributeNameForConstant(constant.getConstantValue()) ;  
            tupleNodeSchema.addChild(createAttributeSchema(attributeName, constant.getConstantValue(), constant.getType()));
        }
    }

    private AttributeNode createAttributeSchema(String attributeName, Object value, String type) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName);
        LeafNode leafNodeInstance = new LeafNode(type);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private void createInstance(String tableName, MainMemoryDB mainMemorySource, AllConstantsInFormula constantsInFormula, boolean premise) {
        INode setNodeInstance = new SetNode(tableName, IntegerOIDGenerator.getNextOID());
        mainMemorySource.getDataSource().getInstances().get(0).addChild(setNodeInstance);
        TupleNode tupleNodeInstance = new TupleNode(tableName + "Tuple", IntegerOIDGenerator.getNextOID());
        setNodeInstance.addChild(tupleNodeInstance);
        for (ConstantInFormula constant : constantsInFormula.getConstants(premise)) {
            String attributeName = DependencyUtility.buildAttributeNameForConstant(constant.getConstantValue()) ;  
            tupleNodeInstance.addChild(createAttributeInstance(attributeName, constant.getConstantValue()));
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
//        dataSource.addInstanceWithCheck(instanceNode);
        dataSource.addInstance(instanceNode);
        return new MainMemoryDB(dataSource);
    }
}
