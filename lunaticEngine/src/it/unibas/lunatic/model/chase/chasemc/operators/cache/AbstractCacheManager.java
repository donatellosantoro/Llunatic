package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupTableUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.utility.LunaticUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCacheManager implements ICacheManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractCacheManager.class);

    private IRunQuery queryRunner;

    public AbstractCacheManager(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    protected String buildKey(Object value, String stepId) {
        return stepId + "#" + value.toString();
    }

    public void generateCellGroupStats(DeltaChaseStep step) {
    }

    protected void loadCellGroups(String stepId, IDatabase deltaDB, Scenario scenario) {
        if (logger.isTraceEnabled()) logger.trace("Loading occurrences value for step " + stepId);
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToExtractCellGroupIds(stepId);
        if (logger.isDebugEnabled()) logger.debug("QueryToExtractCellGroupIds:\n " + query);
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            CellGroupCell cellGroupCell = buildCellGroupCell(tuple, scenario);
            IValue cellGroupId = cellGroupCell.getOriginalCellGroupId();
            CellGroup cellGroup = loadCellGroupFromId(cellGroupId, stepId, deltaDB, scenario);
            if (cellGroup == null) { //No cached version
                cellGroup = new CellGroup(cellGroupId, false);
                putCellGroup(cellGroup, stepId, deltaDB, scenario);
            }
            addCellGroupCellIntoCellGroup(cellGroupCell, cellGroup);
            if (cellGroupCell.getType().equals(LunaticConstants.TYPE_OCCURRENCE) && cellGroupId instanceof ConstantValue) {
                putClusterId(new CellRef(cellGroupCell), cellGroupId, stepId, deltaDB, scenario);
            }
        }
        it.close();
    }

    private void addCellGroupCellIntoCellGroup(CellGroupCell cell, CellGroup cellGroup) {
        String type = cell.getType();
        if (LunaticConstants.TYPE_OCCURRENCE.equals(type)) {
            cellGroup.addOccurrenceCell(cell);
        } else if (LunaticConstants.TYPE_JUSTIFICATION.equals(type)) {
            cellGroup.addJustificationCell(cell);
        } else if (LunaticConstants.TYPE_USER.equals(type)) {
            cellGroup.addUserCell(cell);
        } else if (LunaticConstants.TYPE_INVALID.equals(type)) {
            cellGroup.setInvalidCell(cell);
        } else if (type.startsWith(LunaticConstants.TYPE_ADDITIONAL)) {
            AttributeRef attributeRef = ChaseUtility.extractAttributeRef(type);
            cellGroup.addAdditionalCell(attributeRef, cell);
        } else {
            throw new IllegalArgumentException("Unknown cell-group cell type " + type);
        }
    }

    private CellGroupCell buildCellGroupCell(Tuple tuple, Scenario scenario) {
        TupleOID tid = new TupleOID(LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_OID));
        String table = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TABLE) + "";
        String attribute = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ATTRIBUTE) + "";
        IValue originalCellGroupId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
        IValue originalValue = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ORIGINAL_VALUE);
        String type = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TYPE) + "";
        IValue currentValue = CellGroupIDGenerator.getCellGroupValueFromGroupID(originalCellGroupId);
        if (type.equals(LunaticConstants.TYPE_JUSTIFICATION)) {
            currentValue = originalValue;
        }
        if (type.equals(LunaticConstants.TYPE_USER)) {
            currentValue = originalValue;
        }
        CellRef cellRef = new CellRef(tid, new AttributeRef(table, attribute));
        if (type.equals(LunaticConstants.TYPE_JUSTIFICATION)) {
            TableAlias tableAlias = cellRef.getAttributeRef().getTableAlias();
            tableAlias.setSource(true);
            tableAlias.setAuthoritative(LunaticUtility.isAuthoritative(tableAlias.getTableName(), scenario));
        }
        CellGroupCell cellGroupCell = new CellGroupCell(cellRef, currentValue, originalValue, type, false);
        cellGroupCell.setOriginalCellGroupId(originalCellGroupId);
        return cellGroupCell;
    }
}
