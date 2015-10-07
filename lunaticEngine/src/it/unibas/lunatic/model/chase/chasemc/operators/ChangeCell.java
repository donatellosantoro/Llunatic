package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.expressions.Expression;

public class ChangeCell {

    private static Logger logger = LoggerFactory.getLogger(ChangeCell.class);

    private IInsertTuple insertOperator;
    private IDelete deleteOperator;
    private OccurrenceHandlerMC occurrenceHandler;

    public ChangeCell(IInsertTuple insertOperator, IDelete deleteOperator, OccurrenceHandlerMC occurrenceHandler) {
        this.insertOperator = insertOperator;
        this.deleteOperator = deleteOperator;
        this.occurrenceHandler = occurrenceHandler;
    }

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
            insertNewValue(cell, stepId, newValue, groupID, deltaDB);
        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    public void deleteCells(ChangeDescription changeSet, IDatabase deltaDB, String stepId) {
        CellGroup cellGroup = changeSet.getCellGroup();
        occurrenceHandler.deleteCellGroup(cellGroup, deltaDB, stepId);
        Set<CellGroupCell> cellsToChange = cellGroup.getOccurrences();
        for (Cell cell : cellsToChange) {
            String tableName = cell.getAttributeRef().getTableName();
            String attributeName = cell.getAttributeRef().getName();
            delete(tableName, attributeName, stepId, deltaDB);
        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    private void insertNewValue(CellGroupCell cell, String stepId, IValue newValue, IValue groupID, IDatabase deltaDB) {
        String tableName = cell.getAttributeRef().getTableName();
        String attributeName = cell.getAttributeRef().getName();
        TupleOID tid = cell.getTupleOID();
        IValue originalValue = cell.getOriginalValue();
        if (logger.isDebugEnabled()) logger.debug("Inserting new value in TableName: " + tableName + " AttributeName: " + attributeName);
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Tuple tupleToInsert = ChaseUtility.buildTuple(tid, stepId, newValue, originalValue, groupID, tableName, attributeName);
        insertOperator.execute(deltaDB.getTable(deltaTableName), tupleToInsert, null, deltaDB);
//        occurrenceHandler.handleNewTuple(tupleToInsert, occurrenceValue, deltaDB, tableName, attributeName);
    }

    private void delete(String tableName, String attributeName, String stepId, IDatabase deltaDB) {
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Scan scan = new Scan(new TableAlias(deltaTableName));
        Expression expression = new Expression(SpeedyConstants.STEP + "== \"" + stepId + "\"");
        expression.changeVariableDescription(SpeedyConstants.STEP, new AttributeRef(deltaTableName, SpeedyConstants.STEP));
        Select select = new Select(expression);
        select.addChild(scan);
        deleteOperator.execute(deltaTableName, select, null, deltaDB);
    }
}
