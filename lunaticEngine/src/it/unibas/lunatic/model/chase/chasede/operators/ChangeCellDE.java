package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import java.util.Set;
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

public class ChangeCellDE {

    private static Logger logger = LoggerFactory.getLogger(ChangeCellDE.class);
    private static long version = 1;

    private IInsertTuple insertOperator;
    private IBatchInsert batchInsertOperator;
    private OccurrenceHandlerDE occurrenceHandler;

    public ChangeCellDE(IInsertTuple insertOperator, IBatchInsert batchInsertOperator, OccurrenceHandlerDE occurrenceHandler) {
        this.insertOperator = insertOperator;
        this.batchInsertOperator = batchInsertOperator;
        this.occurrenceHandler = occurrenceHandler;
    }

    //NOTICE: Call flush after inserts
    public void changeCells(CellGroup cellGroup, IDatabase deltaDB, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Saving cell group " + cellGroup.toLongString());
        long cellGroupVersion = version++;
        occurrenceHandler.saveNewCellGroup(cellGroup, deltaDB, cellGroupVersion, scenario);
        IValue newValue = cellGroup.getValue();
        IValue groupID = cellGroup.getId();
        Set<CellGroupCell> cellsToChange = cellGroup.getOccurrences();
        if (logger.isDebugEnabled()) logger.debug("Changing cells " + cellsToChange + " with " + newValue);
        for (CellGroupCell cell : cellsToChange) {
            if (cell.isToSave() != null && !cell.isToSave()) {
                if (logger.isTraceEnabled()) logger.debug("Cell " + cell + " is already saved. Skipping...");
                continue;
            }
            insertNewValue(cell, newValue, groupID, deltaDB, scenario.getConfiguration().isUseBatchInsert());
        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    public void flush(IDatabase database) {
        this.batchInsertOperator.flush(database);
    }

    private void insertNewValue(CellGroupCell cell, IValue newValue, IValue groupID, IDatabase deltaDB, boolean useBatchInsert) {
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
        Tuple tupleToInsert = ChaseUtility.buildTupleDE(tid, version++, newValue, groupID, tableName, attributeName);
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
