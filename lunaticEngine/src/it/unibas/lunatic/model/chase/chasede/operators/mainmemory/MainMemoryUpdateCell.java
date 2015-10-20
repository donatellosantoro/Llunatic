package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.INode;

public class MainMemoryUpdateCell implements IUpdateCell {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryUpdateCell.class);

    public void execute(CellRef cellRef, IValue value, IDatabase database) {
        if (logger.isDebugEnabled()) logger.debug("Changing cell " + cellRef + " with new value " + value + " in database " + database);
        INode instanceRoot = ((MainMemoryDB)database).getDataSource().getInstances().get(0);
        for (INode set : instanceRoot.getChildren()) {
            if (!set.getLabel().equals(cellRef.getAttributeRef().getTableName())) {
                continue;
            }
            for (INode tuple : set.getChildren()) {
                if (!tuple.getValue().toString().equals(cellRef.getTupleOID().getValue().toString())) {
                    continue;
                }
                for (INode attribute : tuple.getChildren()) {
                    if (!attribute.getLabel().equals(cellRef.getAttributeRef().getName())) {
                        continue;
                    }
                    attribute.getChild(0).setValue(value);
                }                
            }
        }

    }
}
