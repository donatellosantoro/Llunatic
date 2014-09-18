package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.EquivalenceClassUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeSet;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClass;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChange;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (scenario.getEGDs().isEmpty()) {
            return false;
        }
        for (Dependency dependency : scenario.getEGDs()) {
            if (dependency.getConclusion().getAtoms().size() > 1) {
                throw new IllegalArgumentException("The chase algorithm requires normalized depdendencies: " + dependency);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Chasing egds on scenario: " + scenario);
        Map<Dependency, IAlgebraOperator> premiseTreeMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getEGDs(), scenario);
        boolean modifiedCells = false;
        while (true) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            boolean modifiedCellsInLastStep = false;
            for (Dependency egd : scenario.getEGDs()) {
                if (logger.isDebugEnabled()) logger.debug("----Chasing egd: " + egd);
                modifiedCellsInLastStep = chaseDependency(egd, scenario, chaseState, premiseTreeMap.get(egd)) || modifiedCellsInLastStep;
            }
            if (!modifiedCellsInLastStep) {
                break;
            } else {
                modifiedCells = true;
            }
        }
        if (modifiedCells) {
            duplicateRemover.removeDuplicatesModuloOID(scenario.getTarget());
        }
        return modifiedCells;
    }

    public boolean chaseDependency(Dependency egd, Scenario scenario, IChaseState chaseState, IAlgebraOperator premiseQuery) {
        if (logger.isDebugEnabled()) logger.debug("***** Chasing dependency: " + egd);
        this.lastTuple = null;
        this.lastTupleHandled = false;
        if (logger.isDebugEnabled()) logger.debug("Executing premise query: " + premiseQuery);
        ITupleIterator it = queryRunner.run(premiseQuery, scenario.getSource(), scenario.getTarget());
//        List<Repair> repairsForDependency = new ArrayList<Repair>();
        Repair repairForDependency = new Repair();
        while (true) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            EquivalenceClass equivalenceClass = readNextEquivalenceClass(it, egd, scenario, chaseState);
            if (equivalenceClass == null) {
                break;
            }
            if (logger.isDebugEnabled()) logger.debug("Equivalence class: " + equivalenceClass);
            Repair repairForEquivalenceClass = generateRepair(equivalenceClass);
            if (repairForEquivalenceClass != null) {
//                repairsForDependency = accumulateRepairs(repairsForDependency, repairForEquivalenceClass, equivalenceClass);
                accumulateRepairs(repairForDependency, repairForEquivalenceClass);
                if (logger.isDebugEnabled()) logger.debug("########### Accumulate repairs " + repairForDependency);
            }
            if (noMoreTuples(it)) {
                break;
            }
        }
        it.close();
        if (logger.isDebugEnabled()) logger.debug("Repair for dependency: " + repairForDependency);
        if (repairForDependency.getChanges().isEmpty()) {
            return false;
        }
        applyRepairs(repairForDependency, scenario);
        return true;
    }

    private boolean noMoreTuples(ITupleIterator it) {
        return (!it.hasNext() && lastTupleHandled);
    }

    private void accumulateRepairs(Repair repairForDependency, Repair repairForEquivalenceClass) {
        repairForDependency.getChanges().addAll(repairForEquivalenceClass.getChanges());
//        if (repairsForDependency.isEmpty()) {
//            repairsForDependency.add(repairForEquivalenceClass);
//            return repairsForDependency;
//        }
//        List<Repair> result = new ArrayList<Repair>();
//        for (Repair repairForDependency : repairsForDependency) {
//            Repair newRepair = new Repair();
//            newRepair.getChanges().addAll(repairForDependency.getChanges());
//            newRepair.getChanges().addAll(repairForEquivalenceClass.getChanges());
//            result.add(newRepair);
//        }
//        return result;
    }

    @SuppressWarnings("unchecked")
    private Repair generateRepair(EquivalenceClass equivalenceClass) {
        Repair repair = new Repair();
        List<CellGroup> cellGroups = extractCellGroups(equivalenceClass.getTupleGroups());
        CellGroup cellGroup = findChanges(cellGroups);
        ChangeSet changesForRepair = new ChangeSet(cellGroup, LunaticConstants.CHASE_FORWARD, Collections.EMPTY_LIST);
        repair.addChanges(changesForRepair);
        return repair;
    }

    private CellGroup findChanges(List<CellGroup> cellGroups) {
        IValue newValue = findNewValue(cellGroups);
        CellGroup changes = new CellGroup(newValue, true);
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.getValue() == newValue) {
                continue;
            }
            changes = mergeGroups(changes, cellGroup, newValue);
        }
        return changes;
    }

    private IValue findNewValue(List<CellGroup> cellGroups) {
        IValue newValue = cellGroups.get(0).getValue();
        for (int i = 1; i < cellGroups.size(); i++) {
            CellGroup group = cellGroups.get(i);
            IValue val = group.getValue();
            if (newValue instanceof ConstantValue && val instanceof ConstantValue) {
                throw new ChaseException("EGD chase failed: Unable to equate " + newValue + " and " + val + " in cell groups: " + cellGroups);
            }
            if (val instanceof ConstantValue) {
                newValue = val;
            }
        }
        return newValue;
    }

    private CellGroup mergeGroups(CellGroup group1, CellGroup group2, IValue newValue) {
        CellGroup newGroup = new CellGroup(newValue, true);
        newGroup.getOccurrences().addAll(group1.getOccurrences());
        newGroup.getOccurrences().addAll(group2.getOccurrences());
        return newGroup;
    }

    private List<CellGroup> extractCellGroups(List<TargetCellsToChange> tupleGroups) {
        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
        for (TargetCellsToChange tupleGroup : tupleGroups) {
            cellGroups.add(tupleGroup.getCellGroupForForwardRepair());
        }
        return cellGroups;
    }

    private void applyRepairs(Repair repair, Scenario scenario) {
        for (ChangeSet changeSet : repair.getChanges()) {
            IValue newValue = changeSet.getNewValue();
            Set<CellRef> cellsToChange = changeSet.getCellGroup().getOccurrences();
            for (CellRef cell : cellsToChange) {
                if (newValue instanceof NullValue) {
                    valueOccurrenceHandler.addOccurrenceForNull(scenario.getTarget(), (NullValue) newValue, cell);
                }
                cellUpdater.execute(cell, newValue, scenario.getTarget());
            }
        }
    }
//    private void applyRepairs(List<Repair> repairs, Scenario scenario) {
//        for (Repair repair : repairs) {
//            for (ChangeSet changeSet : repair.getChanges()) {
//                IValue newValue = changeSet.getNewValue();
//                Set<CellRef> cellsToChange = changeSet.getCellGroup().getOccurrences();
//                for (CellRef cell : cellsToChange) {
//                    if (newValue instanceof NullValue) {
//                        valueOccurrenceHandler.addOccurrenceForNull(scenario.getTarget(), (NullValue) newValue, cell);
//                    }
//                    cellUpdater.execute(cell, newValue, scenario.getTarget());
//                }
//            }
//        }
//    }

    private EquivalenceClass readNextEquivalenceClass(ITupleIterator it, Dependency egd, Scenario scenario, IChaseState chaseState) {
        if (!it.hasNext() && (this.lastTupleHandled || this.lastTuple == null)) {
            return null;
        }
        EquivalenceClass equivalenceClass = buildEquivalenceClass(egd);
        if (lastTuple != null && !this.lastTupleHandled) {
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + this.lastTuple.toStringWithOIDAndAlias());
            EquivalenceClassUtility.addTuple(this.lastTuple, equivalenceClass);
            this.lastTupleHandled = true;
        }
        if (logger.isDebugEnabled()) logger.debug("Reading next equivalence class...");
        while (it.hasNext()) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            Tuple tuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("Reading tuple : " + tuple.toStringWithOIDAndAlias());
            if (lastTuple == null || equivalenceClass.getTupleGroups().isEmpty() || EquivalenceClassUtility.sameEquivalenceClass(tuple, this.lastTuple, egd)) {
                EquivalenceClassUtility.addTuple(tuple, equivalenceClass);
                this.lastTuple = tuple;
                this.lastTupleHandled = true;
            } else {
                if (logger.isDebugEnabled()) logger.debug("Equivalence class is finished...");
                if (equivalenceClass.getTupleGroups().isEmpty()) {
                    throw new IllegalArgumentException("Unable to create equivalence class for egd:\n" + egd + "\nLast tuple: \n" + lastTuple + "\nCurrent tuple: \n" + tuple);
                }
                this.lastTuple = tuple;
                this.lastTupleHandled = false;
                break;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Equivalence class loaded");
        addOccurrencesForEquivalenceClass(equivalenceClass, scenario.getTarget());
        if (logger.isDebugEnabled()) logger.trace("--Equivalence class: " + equivalenceClass);
        return equivalenceClass;
    }

    private void addOccurrencesForEquivalenceClass(EquivalenceClass equivalenceClass, IDatabase target) {
        for (TargetCellsToChange tupleGroup : equivalenceClass.getTupleGroupsWithSameConclusionValue().values()) {
            addOccurrencesForCellGroup(tupleGroup.getCellGroupForForwardRepair(), target);
//            for (CellGroup premiseGroup : tupleGroup.getCellGroupsForBackwardAttributes().values()) {
//                addOccurrencesForCellGroup(premiseGroup, target);
//            }
        }
    }

    private void addOccurrencesForCellGroup(CellGroup cellGroup, IDatabase target) {
        if (!(cellGroup.getValue() instanceof NullValue)) {
            return;
        }
        List<CellRef> cellRefs = valueOccurrenceHandler.getOccurrencesForNull(target, (NullValue) cellGroup.getValue());
        if (cellRefs != null) {
            cellGroup.getOccurrences().addAll(cellRefs);
        }
    }

    @SuppressWarnings("unchecked")
    private EquivalenceClass buildEquivalenceClass(Dependency egd) {
        List<AttributeRef> attributesForForwardChasing = new ArrayList<AttributeRef>();
        FormulaVariable v1 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(0);
        FormulaVariable v2 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(1);
        for (FormulaVariableOccurrence occurrence : v1.getPremiseRelationalOccurrences()) {
            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
            if (attributesForForwardChasing.contains(occurrenceAttribute)) {
                continue;
            }
            attributesForForwardChasing.add(occurrenceAttribute);
        }
        for (FormulaVariableOccurrence occurrence : v2.getPremiseRelationalOccurrences()) {
            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
            if (attributesForForwardChasing.contains(occurrenceAttribute)) {
                continue;
            }
            attributesForForwardChasing.add(occurrenceAttribute);
        }
        return new EquivalenceClass(egd, attributesForForwardChasing, Collections.EMPTY_LIST);
    }
}
