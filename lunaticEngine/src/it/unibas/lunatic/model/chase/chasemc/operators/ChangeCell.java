package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.algebra.Scan;
import it.unibas.lunatic.model.algebra.Select;
import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeSet;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.expressions.Expression;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeCell {

    private static Logger logger = LoggerFactory.getLogger(ChangeCell.class);

    private IInsertTuple insertOperator;
    private IDelete deleteOperator;
    private IValueOccurrenceHandlerMC occurrenceHandler;

    public ChangeCell(IInsertTuple insertOperator, IDelete deleteOperator, IValueOccurrenceHandlerMC occurrenceHandler) {
        this.insertOperator = insertOperator;
        this.deleteOperator = deleteOperator;
        this.occurrenceHandler = occurrenceHandler;
    }

    public void changeCells(ChangeSet changeSet, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        CellGroup cellGroup = changeSet.getCellGroup();
        IValue newValue = changeSet.getNewValue();
        IValue groupID = cellGroup.getId();
//        if (newValue instanceof ConstantValue) {
//            IValue occurrenceValue = LunaticUtility.generateClusterId(changeSet.getCellGroup());
//            cellGroup.setValue(occurrenceValue);
//        }
        Set<CellRef> cellsToChange = cellGroup.getOccurrences();
        if (logger.isDebugEnabled()) logger.debug("Changing cells " + cellsToChange + " with " + newValue);
        occurrenceHandler.saveNewCellGroup(cellGroup, deltaDB, stepId, synchronizeCache);
        for (CellRef cell : cellsToChange) {
            String tableName = cell.getAttributeRef().getTableName();
            String attributeName = cell.getAttributeRef().getName();
            TupleOID tid = cell.getTupleOID();
            insertNewValue(tableName, attributeName, tid, stepId, newValue, groupID, deltaDB);
        }
//        Set<Cell> provenances = changeSet.getCellGroup().getProvenances();
//        for (Cell cell : provenances) {
//            occurrenceHandler.addProvenance(deltaDB, occurrenceValue, cell, stepId);
//        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    public void deleteCells(ChangeSet changeSet, IDatabase deltaDB, String stepId, boolean synchronizeCache) {
        CellGroup cellGroup = changeSet.getCellGroup();
        occurrenceHandler.deleteCellGroup(cellGroup, deltaDB, stepId, synchronizeCache);
        Set<CellRef> cellsToChange = cellGroup.getOccurrences();
        for (CellRef cell : cellsToChange) {
            String tableName = cell.getAttributeRef().getTableName();
            String attributeName = cell.getAttributeRef().getName();
            delete(tableName, attributeName, stepId, deltaDB);
        }
        if (logger.isDebugEnabled()) logger.debug("New target: " + deltaDB.printInstances());
    }

    private void insertNewValue(String tableName, String attributeName, TupleOID tid, String stepId, IValue newValue, IValue groupID, IDatabase deltaDB) {
        if (logger.isDebugEnabled()) logger.debug("Inserting new value in TableName: " + tableName + " AttributeName: " + attributeName);
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Tuple tupleToInsert = ChaseUtility.buildTuple(tid, stepId, newValue, groupID, tableName, attributeName);
        insertOperator.execute(deltaDB.getTable(deltaTableName), tupleToInsert);
//        occurrenceHandler.handleNewTuple(tupleToInsert, occurrenceValue, deltaDB, tableName, attributeName);
    }

    private void delete(String tableName, String attributeName, String stepId, IDatabase deltaDB) {
        String deltaTableName = ChaseUtility.getDeltaRelationName(tableName, attributeName);
        Scan scan = new Scan(new TableAlias(deltaTableName));
        Expression expression = new Expression(LunaticConstants.STEP + "== \"" + stepId + "\"");
        expression.setVariableDescription(LunaticConstants.STEP, new AttributeRef(deltaTableName, LunaticConstants.STEP));
        Select select = new Select(expression);
        select.addChild(scan);
        deleteOperator.execute(deltaTableName, select, null, deltaDB);
    }
}
