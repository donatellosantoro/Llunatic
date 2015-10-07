package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public class EquivalenceClassUtility {

    private static Logger logger = LoggerFactory.getLogger(EquivalenceClassUtility.class);

    public static boolean sameEquivalenceClass(Tuple tuple, Tuple lastTuple, Dependency egd) {
        List<AttributeRef> joinAttributes = getTargetJoinAttributes(egd);
        joinAttributes = ChaseUtility.filterConclusionOccurrences(joinAttributes, egd);
        if (logger.isDebugEnabled()) logger.debug("Target join attributes: " + joinAttributes);
        for (AttributeRef attribute : joinAttributes) {
            IValue tupleValue = tuple.getCell(attribute).getValue();
            IValue lastTupleValue = lastTuple.getCell(attribute).getValue();
            if (!tupleValue.equals(lastTupleValue)) {
                return false;
            }
        }
        return true;
    }

    //TODO++ refactor: this method is used twice, once for chasemc, once for chasede; consider separating those
    //       for chase de no need to consider backard attributes
    public static void addTuple(Tuple tuple, EquivalenceClassForSymmetricEGD equivalenceClass) {
        if (logger.isDebugEnabled()) logger.trace("Adding tuple " + tuple + " to equivalence class: " + equivalenceClass);
        if (logger.isDebugEnabled()) logger.debug("OccurrenceAttributesForConclusionVariable: " + equivalenceClass.getOccurrenceAttributesForConclusionVariable());
        for (AttributeRef occurrenceAttributesForConclusionVariable : equivalenceClass.getOccurrenceAttributesForConclusionVariable()) {
            Cell cellToChangeForForwardChasing = tuple.getCell(occurrenceAttributesForConclusionVariable);
            if (logger.isDebugEnabled()) logger.trace("Attribute: " + occurrenceAttributesForConclusionVariable + " - Cell: " + cellToChangeForForwardChasing);
            IValue conclusionValue = cellToChangeForForwardChasing.getValue();
            EGDEquivalenceClassCells equivalenceClassCells = getOrCreateEquivanceClassCells(equivalenceClass, conclusionValue);
            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, occurrenceAttributesForConclusionVariable));
            CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(occurrenceAttributesForConclusionVariable));
            if (occurrenceAttributesForConclusionVariable.isSource()) {
                CellGroupCell sourceCell = new CellGroupCell(cellRef, conclusionValue, conclusionValue, LunaticConstants.TYPE_JUSTIFICATION, null);
                equivalenceClassCells.getCellGroupForForwardRepair().addJustificationCell(sourceCell);
                continue;
            }
            CellGroupCell targetCell = new CellGroupCell(cellRef, conclusionValue, null, LunaticConstants.TYPE_OCCURRENCE, null);
            equivalenceClassCells.getCellGroupForForwardRepair().addOccurrenceCell(targetCell);
            //TODO++ refactor: avoid nesting by extracting conclusion values
            for (BackwardAttribute backwardAttribute : equivalenceClass.getAttributesToChangeForBackwardChasing()) {
                AttributeRef attributeForBackwardChasing = backwardAttribute.getAttributeRef();
                Cell cellForBackward = tuple.getCell(attributeForBackwardChasing);
//                CellGroup cellGroupForBackward = targetCellsToChange.getOrCreateCellsForBackwardRepair(backwardAttribute, cellForBackward.getValue());
                Set<Cell> cellsForBackward = equivalenceClassCells.getOrCreateCellsForBackwardRepair(backwardAttribute);
                TupleOID tupleOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeForBackwardChasing));
                Cell backwardCell = new Cell(tupleOid, ChaseUtility.unAlias(attributeForBackwardChasing), cellForBackward.getValue());
                cellsForBackward.add(backwardCell);
            }
            addAdditionalAttributes(equivalenceClassCells, originalOid, tuple, equivalenceClass);
        }
        if (logger.isDebugEnabled()) logger.trace("Equivalence class: " + equivalenceClass);
    }

    private static EGDEquivalenceClassCells getOrCreateEquivanceClassCells(EquivalenceClassForSymmetricEGD equivalenceClass, IValue conclusionValue) {
        EGDEquivalenceClassCells targetCellsToChange = equivalenceClass.getTupleGroupsWithSameConclusionValue().get(conclusionValue);
        if (logger.isDebugEnabled()) logger.trace("Target cells to change: " + targetCellsToChange);
        if (targetCellsToChange == null) {
            targetCellsToChange = new EGDEquivalenceClassCells(conclusionValue);
            equivalenceClass.getTupleGroupsWithSameConclusionValue().put(conclusionValue, targetCellsToChange);
        }
        return targetCellsToChange;
    }

    private static void addAdditionalAttributes(EGDEquivalenceClassCells targetCellsToChange, TupleOID originalOIDForConclusionValue, Tuple tuple, EquivalenceClassForSymmetricEGD equivalenceClass) {
        for (AttributeRef additionalAttribute : equivalenceClass.getEGD().getAdditionalAttributes()) {
            for (Cell additionalCell : tuple.getCells()) {
                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(additionalCell.getAttributeRef());
                if (!unaliasedAttribute.equals(additionalAttribute)) {
                    continue;
                }
                TupleOID originalOIDForCell = new TupleOID(ChaseUtility.getOriginalOid(tuple, additionalCell.getAttributeRef()));
                if (!originalOIDForCell.equals(originalOIDForConclusionValue)) {
                    continue;
                }
                CellGroupCell additionalCellGroupCell = new CellGroupCell(originalOIDForCell, unaliasedAttribute, additionalCell.getValue(), null, LunaticConstants.TYPE_ADDITIONAL, null);
                targetCellsToChange.getCellGroupForForwardRepair().addAdditionalCell(additionalAttribute, additionalCellGroupCell);
            }
        }
    }

    public static AttributeRef correctAttributeForSymmetricEGDs(AttributeRef attributeRef, Dependency egd) {
        if (!egd.hasSymmetricChase()) {
            return attributeRef;
        }
        for (TableAlias tableAlias : egd.getSymmetricAtoms().getSymmetricAliases()) {
            if (attributeRef.getTableName().equals(tableAlias.getTableName())) {
                return ChaseUtility.unAlias(attributeRef);
            }
        }
        return attributeRef;
    }

    private static List<AttributeRef> getTargetJoinAttributes(Dependency egd) {
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributesInPositiveFormula(egd);
        if (!egd.hasSymmetricChase()) {
            return targetJoinAttributes;
        }
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : targetJoinAttributes) {
            result.add(correctAttributeForSymmetricEGDs(attributeRef, egd));
        }
        return result;
    }
}
