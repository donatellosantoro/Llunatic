package it.unibas.lunatic.model.algebra.operators.mainmemory;

import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryTable;
import it.unibas.lunatic.model.database.mainmemory.datasource.DataSource;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertTuple implements IInsertTuple {

    private static Logger logger = LoggerFactory.getLogger(InsertTuple.class);

    @Override
    public void execute(ITable table, Tuple tuple, IDatabase source, IDatabase target) {
        if (logger.isDebugEnabled()) logger.debug("----Executing insert into table " + table.getName() + " tuple: " + tuple);
        DataSource dataSource = ((MainMemoryTable) table).getDataSource();
        String tupleLabel = dataSource.getSchema().getChild(0).getLabel();
        INode tupleNode = LunaticUtility.createNode("TupleNode", tupleLabel, tuple.getOid());
        dataSource.getInstances().get(0).addChild(tupleNode);
        for (Cell cell : tuple.getCells()) {
            INode attributeNode = LunaticUtility.createNode("AttributeNode", cell.getAttribute(), IntegerOIDGenerator.getNextOID());
            tupleNode.addChild(attributeNode);
            String leafLabel = dataSource.getSchema().getChild(0).getChild(cell.getAttribute()).getChild(0).getLabel();
            INode leafNode = LunaticUtility.createNode("LeafNode", leafLabel, cell.getValue());
            attributeNode.addChild(leafNode);
        }
    }
}
