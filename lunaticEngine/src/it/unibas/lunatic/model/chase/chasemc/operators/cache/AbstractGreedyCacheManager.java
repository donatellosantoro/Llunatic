package it.unibas.lunatic.model.chase.chasemc.operators.cache;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupStats;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class AbstractGreedyCacheManager extends AbstractCacheManager {

    public AbstractGreedyCacheManager(IRunQuery queryRunner) {
        super(queryRunner);
    }

    protected abstract void loadCacheForStep(String stepId, IDatabase deltaDB);

    protected abstract Set<String> getKeySet();

    protected abstract CellGroup getCellGroup(String key);

    @Override
    @SuppressWarnings("unchecked")
    public void generateCellGroupStats(DeltaChaseStep step) {
        if (step.getCellGroupStats() != null || LunaticConstants.CHASE_STEP_TGD.equals(step.getChaseMode())) {
            return;
        }
//        if (currentCachedStepId == null) {
        loadCacheForStep(step.getId(), step.getDeltaDB());
//        }
//        if (!step.getId().equals(currentCachedStepId)) {
//            throw new IllegalStateException("Unable to initialize cell group stats for chase step " + step.getId() + " with cache for step " + currentCachedStepId);
//        }
        CellGroupStats stats = new CellGroupStats();
        step.setCellGroupStats(stats);
        Set<String> keys = getKeySet();
        stats.totalCellGroups = keys.size();
        stats.minOccurrences = -1;
        stats.maxOccurrences = 0;
        stats.minProvenances = -1;
        stats.maxProvenances = 0;
        for (String key : keys) {
            CellGroup cellGroup = getCellGroup(key);
            if (cellGroup.getValue() instanceof LLUNValue) stats.llunCellGroups++;
            if (cellGroup.getValue() instanceof NullValue) stats.nullCellGroups++;
            if (cellGroup.getValue() instanceof ConstantValue) stats.constantCellGroups++;
            stats.totalOccurrences += cellGroup.getOccurrences().size();
            stats.totalProvenances += cellGroup.getProvenances().size();
            if (cellGroup.getOccurrences().size() > stats.maxOccurrences) stats.maxOccurrences = cellGroup.getOccurrences().size();
            if (stats.minOccurrences == -1 || cellGroup.getOccurrences().size() < stats.minOccurrences) stats.minOccurrences = cellGroup.getOccurrences().size();
            if (cellGroup.getProvenances().size() > stats.maxProvenances) stats.maxProvenances = cellGroup.getProvenances().size();
            if (stats.minProvenances == -1 || cellGroup.getProvenances().size() < stats.minProvenances) stats.minProvenances = cellGroup.getProvenances().size();
            stats.totalCellGroupHash += cellGroupHashCode(cellGroup);
        }
        if (stats.minOccurrences == -1) stats.minOccurrences = 0;
        if (stats.minProvenances == -1) stats.minProvenances = 0;
    }

    private int cellGroupHashCode(CellGroup cellGroup) {
        String valueString = cellGroup.getValue().toString();
        if (cellGroup.getValue() instanceof LLUNValue) {
            valueString = "_Llun_";
        } else if (cellGroup.getValue() instanceof NullValue) {
            valueString = "_Null_";
        }
        List<CellRef> occurrenceList = new ArrayList<CellRef>(cellGroup.getOccurrences());
        Collections.sort(occurrenceList, new CellRefStringComparator());
        List<CellRef> provenanceList = generateCellRefs(cellGroup.getProvenances());
        Collections.sort(provenanceList, new CellRefStringComparator());
//        int cellGroupHash = (valueString + occurrenceList.hashCode() + provenanceList.hashCode()).hashCode();
//        int cellGroupHash = (valueString + occurrenceList.toString().hashCode() + provenanceList.toString().hashCode()).hashCode();
        int cellGroupHash = (valueString + buildHash(occurrenceList) + buildHash(provenanceList)).hashCode();
        return cellGroupHash;
    }

    private List<CellRef> generateCellRefs(Set<Cell> provenances) {
        List<CellRef> result = new ArrayList<CellRef>();
        for (Cell cell : provenances) {
            result.add(new CellRef(cell));
        }
        return result;
    }

    private int buildHash(List<CellRef> cellRefs) {
        int hash = 7;
        for (CellRef cellRef : cellRefs) {
            hash += cellRef.toString().hashCode();
        }
        return hash;
    }
}
