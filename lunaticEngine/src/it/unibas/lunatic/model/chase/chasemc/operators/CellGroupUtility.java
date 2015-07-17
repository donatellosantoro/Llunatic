package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellGroupUtility {

    private static Logger logger = LoggerFactory.getLogger(CellGroupUtility.class);

    public static void addCellGroupsForTGDVariableOccurrences(Tuple tuple, FormulaVariable formulaVariable, Map<FormulaVariable, List<CellGroup>> cellGroupsForVariable, IDatabase deltaDB, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        List<CellGroup> cellGroups = cellGroupsForVariable.get(formulaVariable);
        if (cellGroups == null) {
            cellGroups = new ArrayList<CellGroup>();
        }
        loadOrCreateCellGroupForTGDVariable(extractAttributeRefs(formulaVariable.getPremiseRelationalOccurrences()), tuple, cellGroups, deltaDB, stepId, occurrenceHandler);
        loadOrCreateCellGroupForTGDVariable(extractAttributeRefs(formulaVariable.getConclusionRelationalOccurrences()), tuple, cellGroups, deltaDB, stepId, occurrenceHandler);
        cellGroupsForVariable.put(formulaVariable, cellGroups);
    }

    public static List<AttributeRef> extractAttributeRefs(List<FormulaVariableOccurrence> occurrences) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : occurrences) {
            result.add(formulaVariableOccurrence.getAttributeRef());
        }
        return result;
    }

    //TODO++ (TGD)
    private static void loadOrCreateCellGroupForTGDVariable(List<AttributeRef> occurrenceAttributes, Tuple tuple, List<CellGroup> cellGroups, IDatabase deltaDB, String stepId, OccurrenceHandlerMC occurrenceHandler) {
        throw new UnsupportedOperationException();
//        for (AttributeRef attributeRef : occurrenceAttributes) {
//            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeRef));
//            CellRef cellRef = new CellRef(originalOid, attributeRef);
//            IValue cellGroupValue = tuple.getCell(attributeRef).getValue();
//            //TODO++ (TGD) Check new cell
//            Cell cell = new Cell(cellRef, cellGroupValue);
//            CellGroup cellGroup = occurrenceHandler.loadCellGroupFromValue(cell, deltaDB, stepId);
//            if (cellGroup == null) {
//                cellGroup = createNewCellGroupFromCell(cell);
//            }
//            cellGroups.add(cellGroup);
//        }
    }

    public static CellGroup createNewCellGroupFromCell(Cell cell) {
        CellGroup cellGroup = new CellGroup(cell.getValue(), true);
        IValue value = cell.getValue();
        if (cell.getAttributeRef().isSource()) {
            CellGroupCell cellGroupCell = new CellGroupCell(cell.getTupleOID(), cell.getAttributeRef(), value, value, LunaticConstants.TYPE_JUSTIFICATION, true);
            cellGroup.addJustificationCell(cellGroupCell);
        } else if (cell.getAttributeRef().isTarget()) {
            CellGroupCell cellGroupCell = new CellGroupCell(cell.getTupleOID(), cell.getAttributeRef(), value, value, LunaticConstants.TYPE_OCCURRENCE, true);
            cellGroup.addOccurrenceCell(cellGroupCell);
        }
        return cellGroup;
    }

    public static CellGroup mergeCellGroupsForTGDs(List<CellGroup> cellGroups) {
        IValue cellGroupValue = cellGroups.get(0).getValue();
        CellGroup mergedCellGroup = new CellGroup(cellGroupValue, true);
        for (CellGroup cellGroup : cellGroups) {
            mergedCellGroup.getOccurrences().addAll(cellGroup.getOccurrences());
            mergedCellGroup.getJustifications().addAll(cellGroup.getJustifications());
        }
        return mergedCellGroup;
    }

    public static boolean haveAllEqualValues(Set<CellGroupCell> cells) {
        IValue firstValue = cells.iterator().next().getValue();
        for (CellGroupCell cell : cells) {
            if (cell.getValue().equals(firstValue)) {
                continue;
            }
            return false;
        }
        return true;
    }

}
