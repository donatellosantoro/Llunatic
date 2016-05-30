package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.CellGroupStats;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupTableUtility;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IRunQuery;

public abstract class AbstractGreedyCacheManager implements ICacheManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractGreedyCacheManager.class);
    private static final String KEY_SEPARATOR = "$";

    private IRunQuery queryRunner;

    public AbstractGreedyCacheManager(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    protected abstract void loadCacheForStep(String stepId, IDatabase deltaDB, Scenario scenario);

    @Override
    @SuppressWarnings("unchecked")
    public void generateCellGroupStats(DeltaChaseStep step) {
//        if (step.getCellGroupStats() != null || LunaticConstants.CHASE_STEP_TGD.equals(step.getChaseMode())) {
        if (step.getCellGroupStats() != null) {
            return;
        }
        loadCacheForStep(step.getId(), step.getDeltaDB(), step.getScenario());
//        if (!step.getId().equals(currentCachedStepId)) {
//            throw new IllegalStateException("Unable to initialize cell group stats for chase step " + step.getId() + " with cache for step " + currentCachedStepId);
//        }
        CellGroupStats stats = new CellGroupStats();
        step.setCellGroupStats(stats);
        Set<String> cellGroupKeys = getKeySet();
        Collection<String> maximalCellGroupKeys = filterKeys(cellGroupKeys);
        stats.totalCellGroups = maximalCellGroupKeys.size();
        stats.minOccurrences = -1;
        stats.maxOccurrences = 0;
        stats.minJustifications = -1;
        stats.maxJustifications = 0;
        for (String cellGroupKey : maximalCellGroupKeys) {
            CellGroup cellGroup = getCellGroup(cellGroupKey);
            if (cellGroup.getValue() instanceof LLUNValue) {
                stats.llunCellGroups++;
                stats.totalNumberOfLluns += cellGroup.getOccurrences().size();
            }
            if (cellGroup.getValue() instanceof NullValue) stats.nullCellGroups++;
            if (cellGroup.getValue() instanceof ConstantValue) stats.constantCellGroups++;
            stats.totalOccurrences += cellGroup.getOccurrences().size();
            stats.totalJustifications += cellGroup.getJustifications().size();
            stats.totalUserCells += cellGroup.getUserCells().size();
            stats.totalInvalidCells += (cellGroup.hasInvalidCell() ? 1 : 0);
            stats.changedCells += computeChangedCells(cellGroup);
            if (cellGroup.getOccurrences().size() > stats.maxOccurrences) stats.maxOccurrences = cellGroup.getOccurrences().size();
            if (stats.minOccurrences == -1 || cellGroup.getOccurrences().size() < stats.minOccurrences) stats.minOccurrences = cellGroup.getOccurrences().size();
            if (cellGroup.getJustifications().size() > stats.maxJustifications) stats.maxJustifications = cellGroup.getJustifications().size();
            if (stats.minJustifications == -1 || cellGroup.getJustifications().size() < stats.minJustifications) stats.minJustifications = cellGroup.getJustifications().size();
            int hash = cellGroupHashCode(cellGroup);
            stats.totalCellGroupHash += hash;
            stats.addCellGroupHash(cellGroup, hash);
        }
        if (stats.minOccurrences == -1) stats.minOccurrences = 0;
        if (stats.minJustifications == -1) stats.minJustifications = 0;
    }

    private Collection<String> filterKeys(Set<String> cellGroupKeys) {
        Map<CellRef, String> maximalKeyForCells = new HashMap<CellRef, String>();
        for (String cellGroupKey : cellGroupKeys) {
            CellGroup cellGroup = getCellGroup(cellGroupKey);
            for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
                CellRef cellRef = new CellRef(occurrence);
                String previousKey = maximalKeyForCells.get(cellRef);
                if (previousKey == null || wasGeneratedInThisStep(cellGroup)) {
                    maximalKeyForCells.put(cellRef, cellGroupKey);
                }
            }
        }
        return maximalKeyForCells.values();
    }

    private boolean wasGeneratedInThisStep(CellGroup cellGroup) {
        for (CellGroupCell cell : cellGroup.getAllCells()) {
            if (cell.isToSave()) {
                return true;
            }
        }
        return false;
    }

    private int cellGroupHashCode(CellGroup cellGroup) {
        String valueString = cellGroup.getValue().toString();
        if (cellGroup.getValue() instanceof LLUNValue) {
            valueString = "_Llun_";
        } else if (cellGroup.getValue() instanceof NullValue) {
            valueString = "_Null_";
        }
        List<CellRef> occurrenceList = new ArrayList<CellRef>(ChaseUtility.createCellRefsFromCells(cellGroup.getOccurrences()));
        Collections.sort(occurrenceList, new CellRefStringComparator());
        List<CellRef> justificationList = new ArrayList<CellRef>(ChaseUtility.createCellRefsFromCells(cellGroup.getJustifications()));
        Collections.sort(justificationList, new CellRefStringComparator());
        List<String> userValues = new ArrayList<String>();
        for (CellGroupCell userCell : cellGroup.getUserCells()) {
            userValues.add(userCell.getValue().toString());
        }
        Collections.sort(userValues);
        Cell invalidCell = cellGroup.getInvalidCell();
        int cellGroupHash = (valueString + buildHashForCellRefs(occurrenceList) + buildHashForCellRefs(justificationList) + buildHashForValues(userValues) + buildHashForInvalidCell(invalidCell)).hashCode();
        return cellGroupHash;
    }

    protected String buildKey(Object value, String stepId) {
        return stepId + KEY_SEPARATOR + value.toString();
    }

    private boolean isDebug(String stepId) {
        return false;
//        return stepId.equalsIgnoreCase("r.ep2_0_f#.ed4_0_f#.ed2_0_f#.md1_0_f#.ed3_0_f#");
    }

    public void loadCellGroups(String stepId, IDatabase deltaDB, Scenario scenario) {
        if (scenario.getConfiguration().isDeScenario()) {
            loadCellGroupsForDE(stepId, deltaDB, scenario);
        } else {
            loadCellGroupsForMC(stepId, deltaDB, scenario);
        }
    }

    private void loadCellGroupsForMC(String stepId, IDatabase deltaDB, Scenario scenario) {
//        if (isDebug(stepId)) logger.warn("Loading occurrences value for step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("Loading occurrences value for step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("DeltaDB " + deltaDB.printInstances());
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToExtractCellGroupCellsForStep(stepId);
        if (logger.isDebugEnabled()) logger.debug("QueryToExtractCellGroupIds:\n " + query);
//        if (isDebug(stepId)) logger.warn(new AlgebraTreeToSQL().treeToSQL(query, null, deltaDB, ""));
        List<CellGroupCell> temporaryCellBuffer = new ArrayList<CellGroupCell>();
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            CellGroupCell cellGroupCell = buildCellGroupCell(tuple, scenario);
            IValue cellGroupId = cellGroupCell.getLastSavedCellGroupId();
            CellGroup cellGroup = loadCellGroupFromId(cellGroupId, stepId, deltaDB, scenario);
            if (cellGroup == null && cellGroupCell.getType().equals(LunaticConstants.TYPE_OCCURRENCE)) { //No cached version
                cellGroup = new CellGroup(cellGroupId, false);
                putCellGroup(cellGroup, stepId, deltaDB, scenario);
            }
            if (cellGroup != null) {
                addCellGroupCellIntoCellGroup(cellGroupCell, cellGroup);
                if (logger.isDebugEnabled()) logger.debug("Added cellgroup cell " + cellGroupCell + " into cg:\n" + cellGroup.toLongString());
            } else {
                //We cannot initialize cellGroups based on Justifications, Invalids or User Cells because they do not need to belong to a single cell group
                temporaryCellBuffer.add(cellGroupCell);
            }
            if (cellGroupCell.getType().equals(LunaticConstants.TYPE_OCCURRENCE) && cellGroupId instanceof ConstantValue) {
                putClusterId(new CellRef(cellGroupCell), cellGroupId, stepId, deltaDB, scenario);
            }
        }
        for (CellGroupCell cellGroupCell : temporaryCellBuffer) {
            IValue cellGroupId = cellGroupCell.getLastSavedCellGroupId();
            CellGroup cellGroup = loadCellGroupFromId(cellGroupId, stepId, deltaDB, scenario);
            if (cellGroup == null) {
                continue;
            }
            addCellGroupCellIntoCellGroup(cellGroupCell, cellGroup);
        }
        it.close();
//        if (isDebug(stepId)) throw new IllegalArgumentException();
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
        IValue cellGroupId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
        IValue originalValue = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ORIGINAL_VALUE);
        String type = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TYPE) + "";
        IValue currentValue = CellGroupIDGenerator.getCellGroupValueFromGroupID(cellGroupId);
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
        cellGroupCell.setLastSavedCellGroupId(cellGroupId);
        return cellGroupCell;
    }

    private void loadCellGroupsForDE(String stepId, IDatabase deltaDB, Scenario scenario) {
//        if (isDebug(stepId)) logger.warn("Loading occurrences value for step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("Loading occurrences value for step " + stepId);
        if (logger.isTraceEnabled()) logger.trace("DeltaDB " + deltaDB.printInstances());
        IAlgebraOperator query = CellGroupTableUtility.buildQueryToExtractCellGroupCellsForStepDE(stepId);
        if (logger.isDebugEnabled()) logger.debug("QueryToExtractCellGroupIds:\n " + query);
//        if (isDebug(stepId)) logger.warn(new AlgebraTreeToSQL().treeToSQL(query, null, deltaDB, ""));
        ITupleIterator it = queryRunner.run(query, null, deltaDB);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            CellGroupCell cellGroupCell = buildCellGroupCellDE(tuple, scenario);
            IValue cellGroupId = cellGroupCell.getLastSavedCellGroupId();
            CellGroup cellGroup = loadCellGroupFromId(cellGroupId, stepId, deltaDB, scenario);
            if (cellGroup == null) { //No cached version
                cellGroup = new CellGroup(cellGroupId, false);
                putCellGroup(cellGroup, stepId, deltaDB, scenario);
            }
            addCellGroupCellIntoCellGroup(cellGroupCell, cellGroup);
            if (logger.isDebugEnabled()) logger.debug("Added cellgroup cell " + cellGroupCell + " into cg:\n" + cellGroup.toLongString());
        }
        it.close();
    }

    private CellGroupCell buildCellGroupCellDE(Tuple tuple, Scenario scenario) {
        TupleOID tid = new TupleOID(LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_OID));
        String table = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_TABLE) + "";
        String attribute = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.CELL_ATTRIBUTE) + "";
        IValue cellGroupId = LunaticUtility.getAttributevalueInTuple(tuple, LunaticConstants.GROUP_ID);
        IValue originalValue = cellGroupId;
        String type = LunaticConstants.TYPE_OCCURRENCE;
        IValue currentValue = CellGroupIDGenerator.getCellGroupValueFromGroupID(cellGroupId);
        CellRef cellRef = new CellRef(tid, new AttributeRef(table, attribute));
        TableAlias tableAlias = cellRef.getAttributeRef().getTableAlias();
        tableAlias.setSource(true);
        tableAlias.setAuthoritative(LunaticUtility.isAuthoritative(tableAlias.getTableName(), scenario));
        CellGroupCell cellGroupCell = new CellGroupCell(cellRef, currentValue, originalValue, type, false);
        cellGroupCell.setLastSavedCellGroupId(cellGroupId);
        return cellGroupCell;
    }

    private int buildHashForCellRefs(List<CellRef> cellRefs) {
        StringBuilder sb = new StringBuilder();
        for (CellRef cellRef : cellRefs) {
            sb.append(cellRef.toString());
        }
        return sb.toString().hashCode();
    }

    private int buildHashForValues(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value);
        }
        return sb.toString().hashCode();
    }

    private int buildHashForInvalidCell(Cell cell) {
        if (cell == null) {
            return 0;
        }
//        return cell.hashCode();
        return LunaticConstants.TYPE_INVALID.hashCode();
    }

    private int computeChangedCells(CellGroup cellGroup) {
        int total = 0;
        for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
            if (occurrence.getOriginalValue().equals(cellGroup.getValue())) {
                continue;
            }
            total++;
        }
        return total;
    }

}
