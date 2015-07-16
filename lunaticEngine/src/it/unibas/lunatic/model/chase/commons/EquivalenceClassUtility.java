package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClass;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChange;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void addTuple(Tuple tuple, EquivalenceClass equivalenceClass) {
        if (logger.isDebugEnabled()) logger.trace("Adding tuple " + tuple + " to equivalence class: " + equivalenceClass);
        if (logger.isDebugEnabled()) logger.debug("OccurrenceAttributesForConclusionVariable: " + equivalenceClass.getOccurrenceAttributesForConclusionVariable());
        for (AttributeRef occurrenceAttributesForConclusionVariable : equivalenceClass.getOccurrenceAttributesForConclusionVariable()) {
            Cell cellToChangeForForwardChasing = tuple.getCell(occurrenceAttributesForConclusionVariable);
            if (logger.isDebugEnabled()) logger.trace("Attribute: " + occurrenceAttributesForConclusionVariable + " - Cell: " + cellToChangeForForwardChasing);
            IValue conclusionValue = cellToChangeForForwardChasing.getValue();
            TargetCellsToChange targetCellsToChange = getOrCreateTargetCellsToChange(equivalenceClass, conclusionValue);
            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, occurrenceAttributesForConclusionVariable));
            CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(occurrenceAttributesForConclusionVariable));
            if (occurrenceAttributesForConclusionVariable.isSource()) {
                CellGroupCell sourceCell = new CellGroupCell(cellRef, conclusionValue, conclusionValue, LunaticConstants.TYPE_JUSTIFICATION, null);
                targetCellsToChange.getCellGroupForForwardRepair().addJustificationCell(sourceCell);
                continue;
            }
            CellGroupCell targetCell = new CellGroupCell(cellRef, conclusionValue, null, LunaticConstants.TYPE_OCCURRENCE, null);
            targetCellsToChange.getCellGroupForForwardRepair().addOccurrenceCell(targetCell);
            for (BackwardAttribute backwardAttribute : equivalenceClass.getAttributesToChangeForBackwardChasing()) {
                AttributeRef attributeForBackwardChasing = backwardAttribute.getAttributeRef();
                Cell cellForBackward = tuple.getCell(attributeForBackwardChasing);
                CellGroup cellGroupForBackward = targetCellsToChange.getOrCreateCellGroupForBackwardRepair(backwardAttribute, cellForBackward.getValue());
                TupleOID tupleOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, attributeForBackwardChasing));
                CellGroupCell backwardCell = new CellGroupCell(tupleOid, ChaseUtility.unAlias(attributeForBackwardChasing), cellForBackward.getValue(), null, LunaticConstants.TYPE_OCCURRENCE, null);
                cellGroupForBackward.addOccurrenceCell(backwardCell);
            }
            addAdditionalAttributes(targetCellsToChange, originalOid, tuple, equivalenceClass);
        }
        if (logger.isDebugEnabled()) logger.trace("Equivalence class: " + equivalenceClass);
    }

    private static TargetCellsToChange getOrCreateTargetCellsToChange(EquivalenceClass equivalenceClass, IValue conclusionValue) {
        TargetCellsToChange targetCellsToChange = equivalenceClass.getTupleGroupsWithSameConclusionValue().get(conclusionValue);
        if (logger.isDebugEnabled()) logger.trace("Target cells to change: " + targetCellsToChange);
        if (targetCellsToChange == null) {
            targetCellsToChange = new TargetCellsToChange(conclusionValue);
            equivalenceClass.getTupleGroupsWithSameConclusionValue().put(conclusionValue, targetCellsToChange);
        }
        return targetCellsToChange;
    }

    private static void addAdditionalAttributes(TargetCellsToChange targetCellsToChange, TupleOID originalOIDForConclusionValue, Tuple tuple, EquivalenceClass equivalenceClass) {
        for (AttributeRef additionalAttribute : equivalenceClass.getDependency().getAdditionalAttributes()) {
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
        if (!egd.hasSymmetricAtoms()) {
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
        List<AttributeRef> targetJoinAttributes = DependencyUtility.findTargetJoinAttributes(egd);
        if (!egd.hasSymmetricAtoms()) {
            return targetJoinAttributes;
        }
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : targetJoinAttributes) {
            result.add(correctAttributeForSymmetricEGDs(attributeRef, egd));
        }
        return result;
    }
}
