package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;

public class ChaseEGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseEGDs.class);

    private BuildAlgebraTreeForEGD treeBuilderForEGD = new BuildAlgebraTreeForEGD();
    private IValueOccurrenceHandlerDE valueOccurrenceHandler;
    private IRemoveDuplicates duplicateRemover;
    private IRunQuery queryRunner;
    private IUpdateCell cellUpdater;
    private Tuple lastTuple;
    private boolean lastTupleHandled;

    public ChaseEGDs(IValueOccurrenceHandlerDE valueOccurrenceHandler, IRemoveDuplicates duplicateRemover, IRunQuery queryRunner, IUpdateCell cellUpdater) {
        this.valueOccurrenceHandler = valueOccurrenceHandler;
        this.duplicateRemover = duplicateRemover;
        this.queryRunner = queryRunner;
        this.cellUpdater = cellUpdater;
    }

    public boolean doChase(Scenario scenario, IChaseState chaseState) {
        throw new UnsupportedOperationException();
//        if (scenario.getEGDs().isEmpty()) {
//            return false;
//        }
//        for (Dependency dependency : scenario.getEGDs()) {
//            if (dependency.getConclusion().getAtoms().size() > 1) {
//                throw new IllegalArgumentException("The chase algorithm requires normalized depdendencies: " + dependency);
//            }
//        }
//        if (logger.isDebugEnabled()) logger.debug("Chasing egds on scenario: " + scenario);
//        long start = new Date().getTime();
//        Map<Dependency, IAlgebraOperator> premiseTreeMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getEGDs(), scenario);
//        boolean modifiedCells = false;
//        while (true) {
//            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
//            boolean modifiedCellsInLastStep = false;
//            for (Dependency egd : scenario.getEGDs()) {
//                if (logger.isDebugEnabled()) logger.debug("----Chasing egd: " + egd);
//                long startEgd = new Date().getTime();
//                modifiedCellsInLastStep = chaseDependency(egd, scenario, chaseState, premiseTreeMap.get(egd)) || modifiedCellsInLastStep;
//                long endEgd = new Date().getTime();
//                ChaseStats.getInstance().addDepenendecyStat(egd, endEgd - startEgd);
//            }
//            if (!modifiedCellsInLastStep) {
//                break;
//            } else {
//                modifiedCells = true;
//            }
//        }
//        long end = new Date().getTime();
//        ChaseStats.getInstance().addStat(ChaseStats.EGD_TIME, end - start);
//        if (modifiedCells) {
//            long startDuplicate = new Date().getTime();
//            duplicateRemover.removeDuplicatesModuloOID(scenario.getTarget());
//            long endDuplicate = new Date().getTime();
//            ChaseStats.getInstance().addStat(ChaseStats.REMOVE_DUPLICATE_TIME, endDuplicate - startDuplicate);
//        }
//        return modifiedCells;
    }

    public boolean chaseDependency(Dependency egd, Scenario scenario, IChaseState chaseState, IAlgebraOperator premiseQuery) {
        throw new UnsupportedOperationException();
//        if (logger.isDebugEnabled()) logger.debug("***** Chasing dependency: " + egd);
//        this.lastTuple = null;
//        this.lastTupleHandled = false;
//        if (logger.isDebugEnabled()) logger.debug("Executing premise query: " + premiseQuery);
//        long violationQueryStart = new Date().getTime();
//        ITupleIterator it = queryRunner.run(premiseQuery, scenario.getSource(), scenario.getTarget());
//        long violationQueryEnd = new Date().getTime();
//        ChaseStats.getInstance().addStat(ChaseStats.EGD_VIOLATION_QUERY_TIME, violationQueryEnd - violationQueryStart);
////        List<Repair> repairsForDependency = new ArrayList<Repair>();
//        Repair repairForDependency = new Repair();
//        try {
//            while (true) {
//                if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
//                EquivalenceClassForSymmetricEGD equivalenceClass = readNextEquivalenceClass(it, egd, scenario, chaseState);
//                if (equivalenceClass == null) {
//                    break;
//                }
//                if (logger.isDebugEnabled()) logger.debug("Equivalence class: " + equivalenceClass);
//                Repair repairForEquivalenceClass = generateRepair(equivalenceClass);
//                if (repairForEquivalenceClass != null) {
////                repairsForDependency = accumulateRepairs(repairsForDependency, repairForEquivalenceClass, equivalenceClass);
//                    accumulateRepairs(repairForDependency, repairForEquivalenceClass);
//                    if (logger.isDebugEnabled()) logger.debug("########### Accumulate repairs " + repairForDependency);
//                }
//                if (noMoreTuples(it)) {
//                    break;
//                }
//            }
//        } catch (ChaseFailedException e) {
//            throw e;
//        } finally {
//            it.close();
//        }
//        if (logger.isDebugEnabled()) logger.debug("Repair for dependency: " + repairForDependency);
//        if (repairForDependency.getChangeDescriptions().isEmpty()) {
//            return false;
//        }
//        long repairStart = new Date().getTime();
//        applyRepairs(repairForDependency, scenario);
//        long repairEnd = new Date().getTime();
//        ChaseStats.getInstance().addStat(ChaseStats.EGD_REPAIR_TIME, repairEnd - repairStart);
//        return true;
    }

//    private boolean noMoreTuples(ITupleIterator it) {
//        return (!it.hasNext() && lastTupleHandled);
//    }
//
//    private void accumulateRepairs(Repair repairForDependency, Repair repairForEquivalenceClass) {
//        repairForDependency.getChangeDescriptions().addAll(repairForEquivalenceClass.getChangeDescriptions());
//    }
//
//    @SuppressWarnings("unchecked")
//    private Repair generateRepair(EquivalenceClassForSymmetricEGD equivalenceClass) {
//        Repair repair = new Repair();
//        List<CellGroup> cellGroups = extractCellGroups(equivalenceClass.getTupleGroups());
//        CellGroup cellGroup = findChanges(cellGroups);
//        ChangeDescription changesForRepair = new ChangeDescription(cellGroup, LunaticConstants.CHASE_FORWARD);
//        repair.addViolationContext(changesForRepair);
//        return repair;
//    }
//
//    private CellGroup findChanges(List<CellGroup> cellGroups) {
//        IValue newValue = findNewValue(cellGroups);
//        CellGroup changes = new CellGroup(newValue, true);
//        for (CellGroup cellGroup : cellGroups) {
//            if (cellGroup.getValue() == newValue) {
//                continue;
//            }
//            changes = mergeGroups(changes, cellGroup, newValue);
//        }
//        return changes;
//    }
//
//    private IValue findNewValue(List<CellGroup> cellGroups) {
//        IValue newValue = cellGroups.get(0).getValue();
//        for (int i = 1; i < cellGroups.size(); i++) {
//            CellGroup group = cellGroups.get(i);
//            IValue val = group.getValue();
//            if (newValue instanceof ConstantValue && val instanceof ConstantValue) {
////                throw new ChaseException("EGD chase failed: Unable to equate " + newValue + " and " + val + " in cell groups: " + cellGroups);
//                throw new ChaseFailedException("EGD chase failed: Unable to equate " + newValue + " and " + val + " in cell groups: " + cellGroups);
//            }
//            if (val instanceof ConstantValue) {
//                newValue = val;
//            }
//        }
//        return newValue;
//    }
//
//    private CellGroup mergeGroups(CellGroup group1, CellGroup group2, IValue newValue) {
//        CellGroup newGroup = new CellGroup(newValue, true);
//        newGroup.getOccurrences().addAll(group1.getOccurrences());
//        newGroup.getOccurrences().addAll(group2.getOccurrences());
//        return newGroup;
//    }
//
//    private List<CellGroup> extractCellGroups(List<EGDEquivalenceClassTupleCellsOLD> tupleGroups) {
//        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
//        for (EGDEquivalenceClassTupleCellsOLD tupleGroup : tupleGroups) {
//            cellGroups.add(tupleGroup.getCellGroupForForwardRepair());
//        }
//        return cellGroups;
//    }
//
//    private void applyRepairs(Repair repair, Scenario scenario) {
//        for (ChangeDescription changeSet : repair.getChangeDescriptions()) {
//            IValue newValue = changeSet.getCellGroup().getValue();
//            Set<CellRef> cellsToChange = ChaseUtility.createCellRefsFromCells(changeSet.getCellGroup().getOccurrences());
//            for (CellRef cellRef : cellsToChange) {
//                if (newValue instanceof NullValue) {
//                    NullValue newNullValue = (NullValue) newValue;
//                    Cell nullCell = new Cell(cellRef, newNullValue);
//                    valueOccurrenceHandler.addOccurrenceForNull(scenario.getTarget(), newNullValue, nullCell);
//                }
//                cellUpdater.execute(cellRef, newValue, scenario.getTarget());
//            }
//        }
//    }
//
//    private EquivalenceClassForSymmetricEGD readNextEquivalenceClass(ITupleIterator it, Dependency egd, Scenario scenario, IChaseState chaseState) {
//        if (!it.hasNext() && (this.lastTupleHandled || this.lastTuple == null)) {
//            return null;
//        }
//        EquivalenceClassForSymmetricEGD equivalenceClass = buildEquivalenceClass(egd);
//        if (lastTuple != null && !this.lastTupleHandled) {
//            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + this.lastTuple.toStringWithOIDAndAlias());
//            addTuple(this.lastTuple, equivalenceClass);
//            this.lastTupleHandled = true;
//        }
//        if (logger.isDebugEnabled()) logger.debug("Reading next equivalence class...");
//        while (it.hasNext()) {
//            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
//            Tuple tuple = it.next();
//            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + tuple.toStringWithOIDAndAlias());
//            if (lastTuple == null || equivalenceClass.getTupleGroups().isEmpty() || EquivalenceClassUtility.sameEquivalenceClass(tuple, this.lastTuple, egd)) {
//                addTuple(tuple, equivalenceClass);
//                this.lastTuple = tuple;
//                this.lastTupleHandled = true;
//            } else {
//                if (logger.isDebugEnabled()) logger.debug("Equivalence class is finished...");
//                if (equivalenceClass.getTupleGroups().isEmpty()) {
//                    throw new IllegalArgumentException("Unable to create equivalence class for egd:\n" + egd + "\nLast tuple: \n" + lastTuple + "\nCurrent tuple: \n" + tuple);
//                }
//                this.lastTuple = tuple;
//                this.lastTupleHandled = false;
//                break;
//            }
//        }
//        if (logger.isDebugEnabled()) logger.debug("Equivalence class loaded");
//        addOccurrencesForEquivalenceClass(equivalenceClass, scenario.getTarget());
//        if (logger.isDebugEnabled()) logger.trace("--Equivalence class: " + equivalenceClass);
//        return equivalenceClass;
//    }
//
//    public void addTuple(Tuple tuple, EquivalenceClassForSymmetricEGD equivalenceClass) {
//        if (logger.isDebugEnabled()) logger.trace("Adding tuple " + tuple + " to equivalence class: " + equivalenceClass);
//        if (logger.isDebugEnabled()) logger.debug("OccurrenceAttributesForConclusionVariable: " + equivalenceClass.getOccurrenceAttributesForConclusionVariable());
//        for (AttributeRef occurrenceAttributesForConclusionVariable : equivalenceClass.getOccurrenceAttributesForConclusionVariable()) {
//            Cell cellToChangeForForwardChasing = tuple.getCell(occurrenceAttributesForConclusionVariable);
//            if (logger.isDebugEnabled()) logger.trace("Attribute: " + occurrenceAttributesForConclusionVariable + " - Cell: " + cellToChangeForForwardChasing);
//            IValue conclusionValue = cellToChangeForForwardChasing.getValue();
//            EGDEquivalenceClassTupleCellsOLD equivalenceClassCells = getOrCreateEquivanceClassCells(equivalenceClass, conclusionValue);
//            TupleOID originalOid = new TupleOID(ChaseUtility.getOriginalOid(tuple, occurrenceAttributesForConclusionVariable));
//            CellRef cellRef = new CellRef(originalOid, ChaseUtility.unAlias(occurrenceAttributesForConclusionVariable));
//            if (occurrenceAttributesForConclusionVariable.isSource()) {
//                CellGroupCell sourceCell = new CellGroupCell(cellRef, conclusionValue, conclusionValue, LunaticConstants.TYPE_JUSTIFICATION, null);
//                equivalenceClassCells.getCellGroupForForwardRepair().addJustificationCell(sourceCell);
//                continue;
//            }
//            CellGroupCell targetCell = new CellGroupCell(cellRef, conclusionValue, null, LunaticConstants.TYPE_OCCURRENCE, null);
//            equivalenceClassCells.getCellGroupForForwardRepair().addOccurrenceCell(targetCell);
//            addAdditionalAttributes(equivalenceClassCells, originalOid, tuple, equivalenceClass);
//        }
//        if (logger.isDebugEnabled()) logger.trace("Equivalence class: " + equivalenceClass);
//    }
//
//    private EGDEquivalenceClassTupleCellsOLD getOrCreateEquivanceClassCells(EquivalenceClassForSymmetricEGD equivalenceClass, IValue conclusionValue) {
//        EGDEquivalenceClassTupleCellsOLD targetCellsToChange = equivalenceClass.getTupleGroupsWithSameConclusionValue().get(conclusionValue);
//        if (logger.isDebugEnabled()) logger.trace("Target cells to change: " + targetCellsToChange);
//        if (targetCellsToChange == null) {
//            targetCellsToChange = new EGDEquivalenceClassTupleCellsOLD(conclusionValue);
//            equivalenceClass.getTupleGroupsWithSameConclusionValue().put(conclusionValue, targetCellsToChange);
//        }
//        return targetCellsToChange;
//    }
//
//    private void addAdditionalAttributes(EGDEquivalenceClassTupleCellsOLD targetCellsToChange, TupleOID originalOIDForConclusionValue, Tuple tuple, EquivalenceClassForSymmetricEGD equivalenceClass) {
//        for (AttributeRef additionalAttribute : equivalenceClass.getEGD().getAdditionalAttributes()) {
//            for (Cell additionalCell : tuple.getCells()) {
//                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(additionalCell.getAttributeRef());
//                if (!unaliasedAttribute.equals(additionalAttribute)) {
//                    continue;
//                }
//                TupleOID originalOIDForCell = new TupleOID(ChaseUtility.getOriginalOid(tuple, additionalCell.getAttributeRef()));
//                if (!originalOIDForCell.equals(originalOIDForConclusionValue)) {
//                    continue;
//                }
//                CellGroupCell additionalCellGroupCell = new CellGroupCell(originalOIDForCell, unaliasedAttribute, additionalCell.getValue(), null, LunaticConstants.TYPE_ADDITIONAL, null);
//                targetCellsToChange.getCellGroupForForwardRepair().addAdditionalCell(additionalAttribute, additionalCellGroupCell);
//            }
//        }
//    }
//
//    private void addOccurrencesForEquivalenceClass(EquivalenceClassForSymmetricEGD equivalenceClass, IDatabase target) {
//        for (EGDEquivalenceClassTupleCellsOLD tupleGroup : equivalenceClass.getTupleGroupsWithSameConclusionValue().values()) {
//            addOccurrencesForCellGroup(tupleGroup.getCellGroupForForwardRepair(), target);
////            for (CellGroup premiseGroup : tupleGroup.getCellGroupsForBackwardAttributes().values()) {
////                addOccurrencesForCellGroup(premiseGroup, target);
////            }
//        }
//    }
//
//    private void addOccurrencesForCellGroup(CellGroup cellGroup, IDatabase target) {
//        if (!(cellGroup.getValue() instanceof NullValue)) {
//            return;
//        }
//        List<Cell> cells = valueOccurrenceHandler.getOccurrencesForNull(target, (NullValue) cellGroup.getValue());
//        if (cells != null) {
//            for (Cell cell : cells) {
//                IValue value = cell.getValue();
//                CellGroupCell cellGroupCell = new CellGroupCell(cell.getTupleOID(), cell.getAttributeRef(), value, null, LunaticConstants.TYPE_OCCURRENCE, false);
//                cellGroup.addOccurrenceCell(cellGroupCell);
//            }
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private EquivalenceClassForSymmetricEGD buildEquivalenceClass(Dependency egd) {
//        List<AttributeRef> attributesForForwardChasing = new ArrayList<AttributeRef>();
//        FormulaVariable v1 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(0);
//        FormulaVariable v2 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(1);
//        for (FormulaVariableOccurrence occurrence : v1.getPremiseRelationalOccurrences()) {
//            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
//            if (attributesForForwardChasing.contains(occurrenceAttribute)) {
//                continue;
//            }
//            attributesForForwardChasing.add(occurrenceAttribute);
//        }
//        for (FormulaVariableOccurrence occurrence : v2.getPremiseRelationalOccurrences()) {
//            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
//            if (attributesForForwardChasing.contains(occurrenceAttribute)) {
//                continue;
//            }
//            attributesForForwardChasing.add(occurrenceAttribute);
//        }
//        return new EquivalenceClassForSymmetricEGD(egd, attributesForForwardChasing, Collections.EMPTY_LIST);
//    }
}
