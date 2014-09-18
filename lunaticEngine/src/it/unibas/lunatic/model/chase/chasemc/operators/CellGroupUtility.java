package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellGroupUtility {
    
    private static Logger logger = LoggerFactory.getLogger(CellGroupUtility.class);
    
    public static void addCellGroupsForTGDVariableOccurrences(Tuple tuple, FormulaVariable formulaVariable, Map<FormulaVariable, List<CellGroup>> cellGroupsForVariable, IDatabase deltaDB, String stepId, IValueOccurrenceHandlerMC occurrenceHandler) {
//        if (!valueForVariableIsConstant(tuple, formulaVariable)) {
//            return;
//        }
        List<CellGroup> cellGroups = cellGroupsForVariable.get(formulaVariable);
        if (cellGroups == null) {
            cellGroups = new ArrayList<CellGroup>();
        }
        loadOrCreateCellGroupForTGDVariable(extractAttributeRefs(formulaVariable.getPremiseRelationalOccurrences()), tuple, cellGroups, deltaDB, stepId, occurrenceHandler);
        loadOrCreateCellGroupForTGDVariable(extractAttributeRefs(formulaVariable.getConclusionRelationalOccurrences()), tuple, cellGroups, deltaDB, stepId, occurrenceHandler);
        cellGroupsForVariable.put(formulaVariable, cellGroups);
    }

//    private static boolean valueForVariableIsConstant(Tuple tuple, FormulaVariable formulaVariable) {
//        IValue value = getFirstOccurrenceValue(tuple, formulaVariable);
//        return (value instanceof ConstantValue);
//    }
//    private static IValue getFirstOccurrenceValue(Tuple tuple, FormulaVariable formulaVariable) {
//        AttributeRef firstOccurrence = formulaVariable.getPremiseOccurrences().get(0).getAttributeRef();
//        return tuple.getCell(firstOccurrence).getValue();
//    }
    public static List<AttributeRef> extractAttributeRefs(List<FormulaVariableOccurrence> occurrences) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : occurrences) {
            result.add(formulaVariableOccurrence.getAttributeRef());
        }
        return result;
    }
    
    private static void loadOrCreateCellGroupForTGDVariable(List<AttributeRef> occurrenceAttributes, Tuple tuple, List<CellGroup> cellGroups, IDatabase deltaDB, String stepId, IValueOccurrenceHandlerMC occurrenceHandler) {
//        for (FormulaVariableOccurrence formulaVariableOccurrence : occurrences) {
        for (AttributeRef attributeRef : occurrenceAttributes) {
            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeRef));
            CellRef cellRef = new CellRef(originalOid, attributeRef);
            IValue cellGroupValue = tuple.getCell(attributeRef).getValue();
            CellGroup cellGroup = occurrenceHandler.loadCellGroupFromValue(cellGroupValue, cellRef, deltaDB, stepId);
            if (cellGroup == null) {
                cellGroup = createNewCellGroupFromCell(cellGroupValue, cellRef);
//                occurrenceHandler.saveNewCellGroup(cellGroup, deltaDB, stepId, true);
            }
            cellGroups.add(cellGroup);
        }
    }
    
    public static CellGroup createNewCellGroupFromCell(IValue value, CellRef cellRef) {
        CellGroup cellGroup = new CellGroup(value, true);
        if (cellRef.getAttributeRef().isAuthoritative()) {
            cellGroup.addProvenanceCell(new Cell(cellRef, value));
        } else if (cellRef.getAttributeRef().isTarget()) {
            cellGroup.addOccurrenceCell(cellRef);
        }
        return cellGroup;
    }
    
    public static CellGroup mergeCellGroupsForTGDs(List<CellGroup> cellGroups) {
        IValue cellGroupValue = cellGroups.get(0).getValue();
//        IValue originalCellGroupValue = new ConstantValue(LunaticUtility.extractValueFromLabel(cellGroupValue.toString()));
//        CellGroup mergedCellGroup = new CellGroup(originalCellGroupValue, true);
        CellGroup mergedCellGroup = new CellGroup(cellGroupValue, true);
        for (CellGroup cellGroup : cellGroups) {
            mergedCellGroup.getOccurrences().addAll(cellGroup.getOccurrences());
            mergedCellGroup.getProvenances().addAll(cellGroup.getProvenances());
        }
//        mergedCellGroup.setValue(LunaticUtility.generateClusterId(mergedCellGroup));
        return mergedCellGroup;
    }
}
