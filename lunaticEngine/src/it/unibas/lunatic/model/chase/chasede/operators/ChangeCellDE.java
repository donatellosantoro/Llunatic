package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.ChangeCell;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DBMSException;
import speedy.model.algebra.operators.IBatchInsert;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public class ChangeCellDE extends ChangeCell {

    private static Logger logger = LoggerFactory.getLogger(ChangeCellDE.class);

    public ChangeCellDE(IInsertTuple insertOperator, IBatchInsert batchInsertOperator, IOccurrenceHandler occurrenceHandler) {
        super(insertOperator, batchInsertOperator, occurrenceHandler);
    }

    @Override
    protected void insertNewValue(CellGroupCell cell, String stepId, IValue newValue, IValue groupID, IDatabase deltaDB, boolean useBatchInsert) {
        if (logger.isDebugEnabled()) logger.debug("Inserting new value using ChangeCellDEProxy. CellGroupCell: " + cell);
        if (cell.getOriginalValue() instanceof ConstantValue) {
            //Constant cells in DE may be singleton cellgroups
            return;
        }
        if (newValue instanceof ConstantValue) {
            //Writing a constant into a null, no need to change the cellgroup table using the trigger
            groupID = null;
        }
        String tableName = cell.getAttributeRef().getTableName();
        String attributeName = cell.getAttributeRef().getName();
        TupleOID tid = cell.getTupleOID();
        if (logger.isDebugEnabled()) logger.debug("Inserting new value in TableName: " + tableName + " AttributeName: " + attributeName);
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Tuple tupleToInsert = ChaseUtility.buildTupleDE(tid, stepId, newValue, groupID, tableName, attributeName);
        try {
            if (useBatchInsert) {
                batchInsertOperator.insert(deltaDB.getTable(deltaTableName), tupleToInsert, deltaDB);
            } else {
                insertOperator.execute(deltaDB.getTable(deltaTableName), tupleToInsert, null, deltaDB);
            }
        } catch (DBMSException ex) {
            logger.error("Change of cell " + cell + " to value " + newValue + " failed. Please check the data type of your database attribute, it might be incompatible with the new value...");
            throw ex;
        }
    }

}
