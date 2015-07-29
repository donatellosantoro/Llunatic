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
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
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
        List<TargetCellsToChangeForEGD> tupleGroupsWithSameConclusionValue = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroupsWithSameConclusionValue, scenario)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        // generate forward repair for all groups
        ViolationContext changesForForwardRepair = generateForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
        Repair forwardRepair = new Repair();
        forwardRepair.addChanges(changesForForwardRepair);
        if (logger.isDebugEnabled()) logger.debug("########Forward repair: " + forwardRepair);
        result.add(forwardRepair);
        if (isDoBackward()) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
            if (isTreeSizeBelowThreshold(chaseBranching, potentialSolutions)) {
                List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
                for (Repair repair : backwardRepairs) {
                    LunaticUtility.addIfNotContained(result, repair);
                }
                if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + backwardRepairs);
            }
        }
        return result;
    }

    protected List<Repair> generateBackwardRepairs(List<TargetCellsToChangeForEGD> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForEGD equivalenceClass) {
        if (tupleGroups.size() > 5) {
            throw new ChaseException("Tuple group of excessive size, it is not possible to chase this scenario: " + tupleGroups);
        }
        List<Repair> result = new ArrayList<Repair>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for groups:\n" + LunaticUtility.printCollection(tupleGroups));
        GenericPowersetGenerator<TargetCellsToChangeForEGD> powersetGenerator = new GenericPowersetGenerator<TargetCellsToChangeForEGD>();
        List<List<TargetCellsToChangeForEGD>> powerset = powersetGenerator.generatePowerSet(tupleGroups);
        for (List<TargetCellsToChangeForEGD> subset : powerset) {
            if (subset.isEmpty()) {
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset:\n" + LunaticUtility.printCollection(subset));
            for (BackwardAttribute backwardAttribute : equivalenceClass.getAttributesToChangeForBackwardChasing()) {
                if (!allGroupsCanBeBackwardChasedForAttribute(subset, backwardAttribute)) {
                    break;
                }
                List<TargetCellsToChangeForEGD> forwardGroups = new ArrayList<TargetCellsToChangeForEGD>(tupleGroups);
                List<TargetCellsToChangeForEGD> backwardGroups = new ArrayList<TargetCellsToChangeForEGD>();
                for (TargetCellsToChangeForEGD tupleGroup : subset) {
                    CellGroup cellGroup = tupleGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute);
                    if (backwardIsAllowed(cellGroup)) {
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
        return result;
    }

    protected boolean allGroupsCanBeBackwardChasedForAttribute(List<TargetCellsToChangeForEGD> subset, BackwardAttribute backwardAttribute) {
        for (TargetCellsToChangeForEGD tupleGroup : subset) {
            if (tupleGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute) == null) {
                return false;
            }
        }
        return true;
    }

    protected Repair generateRepairWithBackwards(EquivalenceClassForEGD equivalenceClass, List<TargetCellsToChangeForEGD> forwardTupleGroups, List<TargetCellsToChangeForEGD> backwardTupleGroups, BackwardAttribute backwardAttribute,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1) {
            ViolationContext forwardChanges = generateForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addChanges(forwardChanges);
        }
        for (TargetCellsToChangeForEGD backwardTupleGroup : backwardTupleGroups) {
            CellGroup backwardCellGroup = backwardTupleGroup.getCellGroupsForBackwardRepairs().get(backwardAttribute).clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            ViolationContext backwardChangesForGroup = new ViolationContext(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCellGroups(backwardTupleGroups));
            repair.addChanges(backwardChangesForGroup);
            if (scenario.getConfiguration().isRemoveSuspiciousSolutions() && isSuspicious(backwardCellGroup, backwardAttribute, equivalenceClass)) {
                backwardTupleGroup.setSuspicious(true);
                repair.setSuspicious(true);
            }
        }
        return repair;
    }

    @Override
    public String toString() {
        return "Standard";
    }
}
