package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DBMSException;
import speedy.model.algebra.operators.IBatchInsert;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public class ChangeCell implements IChangeCell {

    private static Logger logger = LoggerFactory.getLogger(ChangeCell.class);

    protected IInsertTuple insertOperator;
    protected IBatchInsert batchInsertOperator;
    protected IOccurrenceHandler occurrenceHandler;

    public ChangeCell(IInsertTuple insertOperator, IBatchInsert batchInsertOperator, IOccurrenceHandler occurrenceHandler) {
        this.batchInsertOperator = batchInsertOperator;
        this.insertOperator = insertOperator;
        this.occurrenceHandler = occurrenceHandler;
    }

    //NOTICE: Call flush after inserts
    @Override
    public void changeCells(CellGroup cellGroup, IDatabase deltaDB, String stepId, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Saving cell group " + cellGroup.toLongString());
        occurrenceHandler.saveNewCellGroup(cellGroup, deltaDB, stepId, scenario);
        IValue newValue = cellGroup.getValue();
        IValue groupID = cellGroup.getId();
        Set<CellGroupCell> cellsToChange = cellGroup.getOccurrences();
        if (logger.isDebugEnabled()) logger.debug("Changing cells " + cellsToChange + " with " + newValue);
        for (CellGroupCell cell : cellsToChange) {
            if (cell.isToSave() != null && !cell.isToSave()) {
                if (logger.isTraceEnabled()) logger.debug("Cell " + cell + " is already saved. Skipping...");
                continue;
            }
            insertNewValue(cell, stepId, newValue, groupID, deltaDB, scenario.getConfiguration().isUseBatchInsert());
        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    @Override
    public void flush(IDatabase database) {
        this.batchInsertOperator.flush(database);
    }

    protected void insertNewValue(CellGroupCell cell, String stepId, IValue newValue, IValue groupID, IDatabase deltaDB, boolean useBatchInsert) {
        String tableName = cell.getAttributeRef().getTableName();
        String attributeName = cell.getAttributeRef().getName();
        TupleOID tid = cell.getTupleOID();
        IValue originalValue = cell.getOriginalValue();
        if (logger.isDebugEnabled()) logger.debug("Inserting new value in TableName: " + tableName + " AttributeName: " + attributeName);
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Tuple tupleToInsert = ChaseUtility.buildTuple(tid, stepId, newValue, originalValue, groupID, tableName, attributeName);
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
