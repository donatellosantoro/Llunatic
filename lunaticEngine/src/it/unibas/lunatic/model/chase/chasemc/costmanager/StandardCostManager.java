package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericPowersetGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardCostManager extends AbstractCostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardCostManager.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<EGDEquivalenceClassCells> tupleGroupsWithSameConclusionValue = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroupsWithSameConclusionValue, scenario)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        Repair forwardRepair = generateForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot)) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
            for (Repair repair : backwardRepairs) {
                //TODO++ check: backward repairs are generated twice
                LunaticUtility.addIfNotContained(result, repair);
            }
        }
        return result;
    }

//    private Repair generateForwardRepair(EquivalenceClassForEGD equivalenceClass, Scenario scenario, DeltaChaseStep chaseTreeRoot, String stepId) {
//        // generate forward repair for all groups
//        ViolationContext changesForForwardRepair = generateViolationContextForForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
//        Repair forwardRepair = new Repair();
//        forwardRepair.addViolationContext(changesForForwardRepair);
//        if (logger.isDebugEnabled()) logger.debug("########Forward repair: " + forwardRepair);
//        return forwardRepair;
//    }

    private boolean canDoBackward(DeltaChaseStep chaseTreeRoot) {
        if (isDoBackward()) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
            if (isTreeSizeBelowThreshold(chaseBranching, potentialSolutions)) {
                return true;
            }
        }
        return false;
    }

    protected List<Repair> generateBackwardRepairs(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForEGD equivalenceClass) {
        if (tupleGroups.size() > 5) {
            throw new ChaseException("Tuple group of excessive size, it is not possible to chase this scenario: " + tupleGroups);
        }
        List<Repair> result = new ArrayList<Repair>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for groups:\n" + LunaticUtility.printCollection(tupleGroups));
        GenericPowersetGenerator<EGDEquivalenceClassCells> powersetGenerator = new GenericPowersetGenerator<EGDEquivalenceClassCells>();
        List<List<EGDEquivalenceClassCells>> powerset = powersetGenerator.generatePowerSet(tupleGroups);
        for (List<EGDEquivalenceClassCells> subset : powerset) {
            if (subset.isEmpty()) {
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset:\n" + LunaticUtility.printCollection(subset));
            for (BackwardAttribute backwardAttribute : equivalenceClass.getAttributesToChangeForBackwardChasing()) {
                if (!allGroupsCanBeBackwardChasedForAttribute(subset, backwardAttribute)) {
                    break;
                }
                List<EGDEquivalenceClassCells> forwardGroups = new ArrayList<EGDEquivalenceClassCells>(tupleGroups);
                List<EGDEquivalenceClassCells> backwardGroups = new ArrayList<EGDEquivalenceClassCells>();
                for (EGDEquivalenceClassCells tupleGroup : subset) {
                    Set<CellGroup> backwardCellGroups = tupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
                    if (backwardIsAllowed(backwardCellGroups)) {
                        tupleGroup.addCellGroupsForBackwardRepair(backwardAttribute, backwardCellGroups);
                        backwardGroups.add(tupleGroup);
                        forwardGroups.remove(tupleGroup);
                    }
                }
                if (backwardGroups.isEmpty() || forwardGroups.isEmpty()) {
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward groups: " + forwardGroups + "\nBackward groups: " + backwardGroups);
                Repair repair = generateRepairWithBackwards(equivalenceClass, forwardGroups, backwardGroups, backwardAttribute, scenario, deltaDB, stepId);
                result.add(repair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + result);
        return result;
    }

    protected boolean allGroupsCanBeBackwardChasedForAttribute(List<EGDEquivalenceClassCells> subset, BackwardAttribute backwardAttribute) {
        for (EGDEquivalenceClassCells tupleGroup : subset) {
            if (tupleGroup.getWitnessCells().get(backwardAttribute) == null) {
                return false;
            }
        }
        return true;
    }

    protected Repair generateRepairWithBackwards(EquivalenceClassForEGD equivalenceClass, List<EGDEquivalenceClassCells> forwardTupleGroups, List<EGDEquivalenceClassCells> backwardTupleGroups, BackwardAttribute backwardAttribute,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1) {
            ViolationContext forwardChanges = generateViolationContextForForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (EGDEquivalenceClassCells backwardTupleGroup : backwardTupleGroups) {
            Set<CellGroup> backwardCellGroups = backwardTupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
            for (CellGroup backwardCellGroup : backwardCellGroups) {
                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
                backwardCellGroup.setValue(llunValue);
                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
                ViolationContext backwardChangesForGroup = new ViolationContext(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCells(backwardTupleGroups));
                repair.addViolationContext(backwardChangesForGroup);
                if (scenario.getConfiguration().isRemoveSuspiciousSolutions() && isSuspicious(backwardCellGroup, backwardAttribute, equivalenceClass)) {
                    backwardTupleGroup.setSuspicious(true);
                    repair.setSuspicious(true);
                }
            }
        }
        return repair;
    }

    @Override
    public String toString() {
        return "Standard";
    }
}
