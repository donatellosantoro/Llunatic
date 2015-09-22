package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import speedy.model.database.AttributeRef;
import speedy.model.database.CellRef;
import speedy.model.database.IValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.TupleOID;

public class FindOriginalValuesForCellGroupCells {

    private static Logger logger = LoggerFactory.getLogger(FindOriginalValuesForCellGroupCells.class);

    public CellGroup findOriginalValues(List<CellGroup> cellGroupsToMerge, IValue value) {
        Map<CellRef, List<CellGroupCell>> occurrenceMap = new HashMap<CellRef, List<CellGroupCell>>();
        Map<CellRef, List<CellGroupCell>> justificationMap = new HashMap<CellRef, List<CellGroupCell>>();
        Map<AttributeRef, Map<CellRef, List<CellGroupCell>>> additionalMap = new HashMap<AttributeRef, Map<CellRef, List<CellGroupCell>>>();
        Set<CellGroupCell> userCells = new HashSet<CellGroupCell>();
        CellGroupCell invalidCell = null;
        for (CellGroup cellGroup : cellGroupsToMerge) {
            addCellGroupCells(occurrenceMap, cellGroup.getOccurrences());
            addCellGroupCells(justificationMap, cellGroup.getJustifications());
            userCells.addAll(cellGroup.getUserCells());
            if (cellGroup.hasInvalidCell() && invalidCell == null) {
                invalidCell = cellGroup.getInvalidCell();
            }
            for (AttributeRef additionalAttribute : cellGroup.getAdditionalCells().keySet()) {
                Map<CellRef, List<CellGroupCell>> additionalAttributeMap = getOrCreateAdditionalMap(additionalAttribute, additionalMap);
                addCellGroupCells(additionalAttributeMap, cellGroup.getAdditionalCells().get(additionalAttribute));
            }
        }
        Set<CellGroupCell> occurrences = mergeCellGroupCells(occurrenceMap);
        Set<CellGroupCell> justifications = mergeCellGroupCells(justificationMap);
        Map<AttributeRef, Set<CellGroupCell>> additionalCells = new HashMap<AttributeRef, Set<CellGroupCell>>();
        CellGroup result = new CellGroup(value, true);
        result.setOccurrences(occurrences);
        result.setJustifications(justifications);
        result.setUserCells(userCells);
        result.setInvalidCell(invalidCell);
        for (AttributeRef additionalAttribute : additionalMap.keySet()) {
            additionalCells.put(additionalAttribute, mergeCellGroupCells(additionalMap.get(additionalAttribute)));
        }
        result.setAdditionalCells(additionalCells);
        return result;
    }

    private void addCellGroupCells(Map<CellRef, List<CellGroupCell>> result, Set<CellGroupCell> cellGroupCellsToAdd) {
        for (CellGroupCell cellGroupCellToAdd : cellGroupCellsToAdd) {
            CellRef cellRef = new CellRef(cellGroupCellToAdd);
            List<CellGroupCell> cellGroupCellsFor = getCellGroupCellsForCellRef(cellRef, result);
            cellGroupCellsFor.add(cellGroupCellToAdd);
        }
    }

    private List<CellGroupCell> getCellGroupCellsForCellRef(CellRef cellRef, Map<CellRef, List<CellGroupCell>> map) {
        List<CellGroupCell> result = map.get(cellRef);
        if (result == null) {
            result = new ArrayList<CellGroupCell>();
            map.put(cellRef, result);
        }
        return result;
    }

    private Set<CellGroupCell> mergeCellGroupCells(Map<CellRef, List<CellGroupCell>> cellsMap) {
        Set<CellGroupCell> result = new HashSet<CellGroupCell>();
        for (CellRef cellRef : cellsMap.keySet()) {
            CellGroupCell generalizedCell = generalizeCellsWithEqualCellRef(cellsMap.get(cellRef));
            result.add(generalizedCell);
        }
        return result;
    }

    private CellGroupCell generalizeCellsWithEqualCellRef(List<CellGroupCell> cells) {
        CellGroupCell firstCell = cells.get(0);
        TupleOID tupleOid = firstCell.getTupleOID();
        AttributeRef attributeRef = firstCell.getAttributeRef();
        IValue value = firstCell.getValue();
        String type = firstCell.getType();
        IValue originalValue = firstCell.getOriginalValue();
        IValue lastSavedCellGroupId = firstCell.getLastSavedCellGroupId();
        for (int i = 1; i < cells.size(); i++) {
            CellGroupCell cell = cells.get(i);
            if (cell.getOriginalValue() != null) {
                originalValue = cell.getOriginalValue();
            }
            if (cell.getLastSavedCellGroupId() != null) {
                lastSavedCellGroupId = cell.getLastSavedCellGroupId();
            }
        }
        if (originalValue == null) {
            originalValue = value;
        }
        CellGroupCell result = new CellGroupCell(tupleOid, attributeRef, value, originalValue, type, null); //whether to save or not is decided later on
        result.setLastSavedCellGroupId(lastSavedCellGroupId);
        return result;
    }

    private Map<CellRef, List<CellGroupCell>> getOrCreateAdditionalMap(AttributeRef additionalAttribute, Map<AttributeRef, Map<CellRef, List<CellGroupCell>>> additionalMap) {
        Map<CellRef, List<CellGroupCell>> result = additionalMap.get(additionalAttribute);
        if (result == null) {
            result = new HashMap<CellRef, List<CellGroupCell>>();
            additionalMap.put(additionalAttribute, result);
        }
        return result;
    }
}
