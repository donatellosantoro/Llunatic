package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.EquivalenceClassUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.NewChaseSteps;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseEGDEquivalenceClass {

    private static Logger logger = LoggerFactory.getLogger(ChaseEGDEquivalenceClass.class);

    private IRunQuery queryRunner;
    private OccurrenceHandlerMC occurrenceHandler;
    private IBuildDatabaseForChaseStep databaseBuilder;
    private ChangeCell cellChanger;
    private Tuple lastTuple;
    private boolean lastTupleHandled;

    public ChaseEGDEquivalenceClass(IRunQuery queryRunner, OccurrenceHandlerMC occurrenceHandler, IBuildDatabaseForChaseStep databaseBuilder, ChangeCell cellChanger) {
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.databaseBuilder = databaseBuilder;
        this.cellChanger = cellChanger;
    }

    public NewChaseSteps chaseDependency(DeltaChaseStep currentNode, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario, IChaseState chaseState, IDatabase databaseForStep) {
        if (logger.isDebugEnabled()) logger.debug("***** Step: " + currentNode.getId() + " - Chasing dependency: " + egd);
        this.lastTuple = null;
        this.lastTupleHandled = false;
        if (logger.isDebugEnabled()) logger.debug("Executing premise query: " + premiseQuery);
        if (logger.isTraceEnabled()) logger.debug("Result:\n" + LunaticUtility.printIterator(queryRunner.run(premiseQuery, scenario.getSource(), databaseForStep)));
        long violationQueryStart = new Date().getTime();
        ITupleIterator it = queryRunner.run(premiseQuery, scenario.getSource(), databaseForStep);
        long violationQueryEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_VIOLATION_QUERY_TIME, violationQueryEnd - violationQueryStart);
        List<Repair> repairsForDependency = new ArrayList<Repair>();
        try {
            while (true) {
                long equivalenceClassStart = new Date().getTime();
                EquivalenceClassForEGD equivalenceClass = readNextEquivalenceClass(it, egd, currentNode.getDeltaDB(), currentNode.getId(), chaseState, scenario);
                long equivalenceClasEnd = new Date().getTime();
                ChaseStats.getInstance().addStat(ChaseStats.EGD_EQUIVALENCE_CLASS_TIME, equivalenceClasEnd - equivalenceClassStart);
                if (equivalenceClass == null) {
                    break;
                }
                if (logger.isDebugEnabled()) logger.debug("Equivalence class: " + equivalenceClass);
                List<Repair> repairsForEquivalenceClass = scenario.getCostManager().chooseRepairStrategy(equivalenceClass, currentNode.getRoot(), repairsForDependency, scenario, currentNode.getId(), occurrenceHandler);
                if (logger.isDebugEnabled()) logger.debug("Repairs for equivalence class: " + LunaticUtility.printCollection(repairsForEquivalenceClass));
                repairsForDependency = accumulateRepairs(repairsForDependency, repairsForEquivalenceClass, equivalenceClass);
                if (noMoreTuples(it)) {
                    break;
                }
                if (logger.isDebugEnabled()) logger.debug("Repairs for equivalence classes so far: " + repairsForDependency.size());
            }
        } catch (ChaseFailedException e) {
            throw e;
        } finally {
            it.close();
        }
        if (logger.isDebugEnabled()) logger.debug("Total repairs for dependency: " + LunaticUtility.printCollection(repairsForDependency));
        long repairStart = new Date().getTime();
        NewChaseSteps newSteps = applyRepairs(currentNode, repairsForDependency, egd, premiseQuery, scenario);
        long repairEnd = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_REPAIR_TIME, repairEnd - repairStart);
        return newSteps;
    }

    private boolean dependencyIsSatisfied(DeltaChaseStep currentNode, IAlgebraOperator queryOperator, Dependency dependency, Scenario scenario) {
        IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), dependency);
        if (logger.isDebugEnabled()) logger.debug("Checking dependency satisfaction for suspicious egd: " + dependency.getId() + "\nDatabase for step: " + databaseForStep);
        ITupleIterator it = queryRunner.run(queryOperator, scenario.getSource(), databaseForStep);
        boolean isEmpty = !it.hasNext();
        it.close();
        if (logger.isDebugEnabled()) logger.debug("Returning: " + isEmpty);
        return isEmpty;
    }

    private boolean noMoreTuples(ITupleIterator it) {
        return (!it.hasNext() && lastTupleHandled);
    }

    private EquivalenceClassForEGD readNextEquivalenceClass(ITupleIterator it, Dependency egd, IDatabase deltaDB, String stepId, IChaseState chaseState, Scenario scenario) {
        if (!it.hasNext() && (this.lastTupleHandled || this.lastTuple == null)) {
            return null;
        }
        EquivalenceClassForEGD equivalenceClass = createEquivalenceClass(egd);
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
        completeCellGroup(equivalenceClass, deltaDB, stepId, scenario);
        if (logger.isDebugEnabled()) logger.debug("-------- Equivalence class:\n" + equivalenceClass + "\n---------------");
        return equivalenceClass;
    }

    private EquivalenceClassForEGD createEquivalenceClass(Dependency egd) {
        List<AttributeRef> occurrenceAttributesForConclusionVariable = new ArrayList<AttributeRef>();
        FormulaVariable v1 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(0);
        FormulaVariable v2 = ((ComparisonAtom) egd.getConclusion().getAtoms().get(0)).getVariables().get(1);
        for (FormulaVariableOccurrence occurrence : v1.getPremiseRelationalOccurrences()) {
            if (!ChaseUtility.containsAlias(egd.getPremise().getPositiveFormula(), occurrence.getTableAlias())) {
                continue;
            }
            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
            if (occurrenceAttributesForConclusionVariable.contains(occurrenceAttribute)) {
                continue;
            }
            occurrenceAttributesForConclusionVariable.add(occurrenceAttribute);
        }
        for (FormulaVariableOccurrence occurrence : v2.getPremiseRelationalOccurrences()) {
            if (!ChaseUtility.containsAlias(egd.getPremise().getPositiveFormula(), occurrence.getTableAlias())) {
                continue;
            }
            AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(occurrence.getAttributeRef(), egd);
            if (occurrenceAttributesForConclusionVariable.contains(occurrenceAttribute)) {
                continue;
            }
            occurrenceAttributesForConclusionVariable.add(occurrenceAttribute);
        }
        return new EquivalenceClassForEGD(egd, occurrenceAttributesForConclusionVariable, egd.getAttributesForBackwardChasing());
    }

    private void completeCellGroup(EquivalenceClassForEGD equivalenceClass, IDatabase deltaDB, String stepId, Scenario scenario) {
        for (TargetCellsToChangeForEGD tupleGroup : equivalenceClass.getTupleGroupsWithSameConclusionValue().values()) {
            CellGroup forwardCellGroup = this.occurrenceHandler.enrichCellGroups(tupleGroup.getCellGroupForForwardRepair(), deltaDB, stepId, scenario);
            tupleGroup.setCellGroupForForwardRepair(forwardCellGroup);
            for (BackwardAttribute backwardAttribute : tupleGroup.getCellGroupsForBackwardRepairs().keySet()) {
                CellGroup oldBackwardCellGroup = tupleGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute);
                CellGroup newBackwardCellGroup = this.occurrenceHandler.enrichCellGroups(oldBackwardCellGroup, deltaDB, stepId, scenario);
                tupleGroup.setCellGroupForBackwardRepair(backwardAttribute, newBackwardCellGroup);
            }
        }
    }

    private List<Repair> accumulateRepairs(List<Repair> repairsForDependency, List<Repair> repairsForEquivalenceClass, EquivalenceClassForEGD equivalenceClass) {
        if (logger.isDebugEnabled()) logger.debug("Accumulating new repairs. Repairs for dependency so far:\n" + LunaticUtility.printCollection(repairsForDependency) + "\nRepairs for equivalence class:\n" + LunaticUtility.printCollection(repairsForEquivalenceClass));
        // needed to handle the various ways to repair each equivalence class as returned by the cost manager
        if (repairsForEquivalenceClass.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("No repairs to add...");
            return repairsForDependency;
        }
        if (repairsForDependency.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("These are the first repairs, returning repairs for equivalence class...");
            return new ArrayList<Repair>(repairsForEquivalenceClass);
        }
        List<Repair> result = new ArrayList<Repair>();
        for (Repair repairForDependency : repairsForDependency) {
            for (Repair repairForEquivalenceClass : repairsForEquivalenceClass) {
                Repair newRepair = new Repair();
                newRepair.getChanges().addAll(repairForDependency.getChanges());
                newRepair.getChanges().addAll(repairForEquivalenceClass.getChanges());
                newRepair.setSuspicious(newRepair.isSuspicious() || repairForDependency.isSuspicious() || repairForEquivalenceClass.isSuspicious());
                result.add(newRepair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + LunaticUtility.printCollection(result));
        return result;
    }

    private NewChaseSteps applyRepairs(DeltaChaseStep currentNode, List<Repair> repairs, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("---Applying repairs...");
        NewChaseSteps newChaseSteps = new NewChaseSteps(egd);
        for (int i = 0; i < repairs.size(); i++) {
            Repair repair = repairs.get(i);
            boolean consistentRepair = purgeOverlappingContexts(egd, repair, scenario);
            String egdId = egd.getId();
            String localId = ChaseUtility.generateChaseStepIdForEGDs(egdId, i, repair);
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, currentNode, localId, egd, repair, repair.getChaseModes());
            for (ViolationContext changeSet : repair.getChanges()) {
                this.cellChanger.changeCells(changeSet.getCellGroup(), newStep.getDeltaDB(), newStep.getId(), scenario);
            }
            if (repair.isSuspicious() && !dependencyIsSatisfied(newStep, premiseQuery, egd, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Generated step is not a solution \n" + newStep);
                for (ViolationContext changeSet : repair.getChanges()) {
                    this.cellChanger.deleteCells(changeSet, newStep.getDeltaDB(), newStep.getId());
                }
                continue;
            }
            if (isEGDSatisfied(egd, consistentRepair, scenario)) {
                if (logger.isDebugEnabled()) logger.debug("EGD " + egd.getId() + " is satisfied in this step...");
                newStep.addSatisfiedEGD(egd);
            }
            List<AttributeRef> affectedAttributes = extractAffectedAttributes(repair);
            newStep.setAffectedAttributes(affectedAttributes);
            if (logger.isDebugEnabled()) logger.debug("Generated step " + newStep.getId() + " for repair: " + repair);
            newChaseSteps.addChaseStep(newStep);
        }
        if (repairs.isEmpty()) {
            newChaseSteps.setNoRepairsNeeded(true);
        }
        return newChaseSteps;
    }

    private boolean purgeOverlappingContexts(Dependency egd, Repair repair, Scenario scenario) {
        if (egd.hasSymmetricAtoms() || scenario.getConfiguration().isUseLimit1ForEGDs()) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Checking independence of violation contexts for egd " + egd);
        boolean consistent = true;
        Set<Cell> cellsToChange = new HashSet<Cell>();
        for (Iterator<ViolationContext> it = repair.getChanges().iterator(); it.hasNext();) {
            ViolationContext changeSet = it.next();
            if (occurrencesOverlap(changeSet, cellsToChange) || witnessOverlaps(changeSet, cellsToChange)) {
                if (logger.isDebugEnabled()) logger.debug("Violation context has overlaps: " + changeSet);
                it.remove();
                consistent = false;
            } else {
                cellsToChange.addAll(changeSet.getCellGroup().getOccurrences());
            }
        }
        return consistent;
    }

    private boolean occurrencesOverlap(ViolationContext changeSet, Set<Cell> cellsToChange) {
        CellGroup cellGroup = changeSet.getCellGroup();
        boolean inconsistent = containsCells(cellGroup, cellsToChange);
        if (inconsistent && logger.isDebugEnabled()) logger.debug("Occurrences Overlap:\n" + changeSet);
//        if (inconsistent) logger.warn("Inconsistent changes:\n" + changeSet);
        return inconsistent;
    }

    private boolean witnessOverlaps(ViolationContext changeSet, Set<Cell> cellsToChange) {
        if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
            return false;
        }
        List<CellGroup> witnessGroups = changeSet.getWitnessCellGroups();
        for (CellGroup witnessGroup : witnessGroups) {
            if (containsCells(witnessGroup, cellsToChange)) {
                if (logger.isDebugEnabled()) logger.debug("Witness Overlaps:\n" + witnessGroup);
//                logger.warn("Inconsistent witness:\n" + witnessGroup);
                return true;
            }
        }
        return false;
    }

    private boolean containsCells(CellGroup cellGroup, Set<Cell> cellsToChange) {
        for (Cell cellRef : cellGroup.getOccurrences()) {
            if (cellsToChange.contains(cellRef)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEGDSatisfied(Dependency egd, boolean consistentRepair, Scenario scenario) {
//        return egd.hasSymmetricAtoms() && consistentRepair && !scenario.getConfiguration().isUseLimit1();
        return egd.hasSymmetricAtoms() && consistentRepair && !scenario.getConfiguration().isUseLimit1ForEGDs() && !egd.isOverlapBetweenAffectedAndQueried();
    }

    private List<AttributeRef> extractAffectedAttributes(Repair repair) {
        List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
        for (ViolationContext changeSet : repair.getChanges()) {
            CellGroup cellGroupToChange = changeSet.getCellGroup();
            for (Cell occurrenceCell : cellGroupToChange.getOccurrences()) {
                if (!affectedAttributes.contains(occurrenceCell.getAttributeRef())) {
                    affectedAttributes.add(occurrenceCell.getAttributeRef());
                }
            }
        }
        return affectedAttributes;
    }
}
