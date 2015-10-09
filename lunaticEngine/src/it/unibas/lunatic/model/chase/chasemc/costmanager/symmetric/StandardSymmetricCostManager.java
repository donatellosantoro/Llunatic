package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.AbstractCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.nonsymmetric.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericMultiCombinationsGenerator;
import it.unibas.lunatic.utility.combinatorial.GenericPowersetGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;

public class StandardSymmetricCostManager extends AbstractCostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardSymmetricCostManager.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<EGDEquivalenceClassCells> tupleGroupsWithSameConclusionValue = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(tupleGroupsWithSameConclusionValue, scenario)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        Repair forwardRepair = generateSymmetricForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot)) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
            for (Repair repair : backwardRepairs) {
                //TODO++ check: backward repairs are generated twice
                if (result.contains(repair)) {
                    logger.info("Result already contains repair " + repair + "\nResult: " + result);
                }
                LunaticUtility.addIfNotContained(result, repair);
            }
        }
        return result;
    }

//    private Repair generateSymmetricForwardRepair(EquivalenceClassForSymmetricEGD equivalenceClass, Scenario scenario, DeltaChaseStep chaseTreeRoot, String stepId) {
//        // generate forward repair for all groups
//        ChangeDescription changesForForwardRepair = generateChangeDescriptionForForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
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

    protected List<Repair> generateBackwardRepairs(List<EGDEquivalenceClassCells> tupleGroups, Scenario scenario, IDatabase deltaDB, String stepId, EquivalenceClassForSymmetricEGD equivalenceClass) {
        if (tupleGroups.size() > 5) {
            throw new ChaseException("Tuple group of excessive size, it is not possible to chase this scenario: " + tupleGroups);
        }
        List<Repair> result = new ArrayList<Repair>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for groups:\n" + LunaticUtility.printCollection(tupleGroups));
        GenericPowersetGenerator<Integer> powersetGenerator = new GenericPowersetGenerator<Integer>();
        List<List<Integer>> powerset = powersetGenerator.generatePowerSet(createIndexes(tupleGroups.size()));
        for (List<Integer> subsetIndex : powerset) {
            if (subsetIndex.isEmpty() || subsetIndex.size() == tupleGroups.size()) {
                continue;
            }
            List<EGDEquivalenceClassCells> subset = extractSubset(subsetIndex, tupleGroups);
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset indexes: " + subsetIndex);
            if (logger.isDebugEnabled()) logger.debug("Attributes to change for backward chasing: " + equivalenceClass.getAttributesToChangeForBackwardChasing());
            List<BackwardAttribute> backwardAttributes = equivalenceClass.getAttributesToChangeForBackwardChasing();
            GenericMultiCombinationsGenerator<BackwardAttribute> combinationGenerator = new GenericMultiCombinationsGenerator<BackwardAttribute>();
            List<List<BackwardAttribute>> combinations = combinationGenerator.generate(backwardAttributes, subset.size());
            for (List<BackwardAttribute> backwardAttributeCombination : combinations) {
                if (logger.isDebugEnabled()) logger.debug("BackwardAttributeCombination: " + backwardAttributeCombination);
                if (!allGroupsCanBeBackwardChasedForAttribute(subset, backwardAttributeCombination)) { //TODO++ Remove for symmetric
                    if (logger.isDebugEnabled()) logger.debug("Backward attribute missing. Discarding backward attribute " + backwardAttributeCombination);
                    break;
                }
                List<EGDEquivalenceClassCells> forwardGroups = new ArrayList<EGDEquivalenceClassCells>(tupleGroups);
                List<EGDEquivalenceClassCells> backwardGroups = new ArrayList<EGDEquivalenceClassCells>();
                for (int i = 0; i < subset.size(); i++) {
                    BackwardAttribute backwardAttribute = backwardAttributeCombination.get(i);
                    EGDEquivalenceClassCells tupleGroup = subset.get(i);
                    Set<CellGroup> backwardCellGroups = tupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
                    if (!CostManagerUtility.backwardIsAllowed(backwardCellGroups)) {
                        if (logger.isDebugEnabled()) logger.debug("Backward is not allowed. Discarding cell group " + backwardCellGroups);
                        continue;
                    }
                    forwardGroups.remove(tupleGroup);
                    tupleGroup.addCellGroupsForBackwardRepair(backwardAttribute, backwardCellGroups);
                    backwardGroups.add(tupleGroup);
                }
                if (backwardGroups.isEmpty() || forwardGroups.isEmpty()) {
                    if (logger.isDebugEnabled()) logger.debug("Forward or Backward is empty. Discarding cell group");
                    continue;
                }
                Repair repair = generateRepairWithBackwards(equivalenceClass, forwardGroups, backwardGroups, backwardAttributeCombination, scenario, deltaDB, stepId);
                if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward groups: " + forwardGroups + "\nBackward groups: " + backwardGroups + "\n" + repair);
                result.add(repair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + result);
        return result;
    }

    protected boolean allGroupsCanBeBackwardChasedForAttribute(List<EGDEquivalenceClassCells> subset, List<BackwardAttribute> backwardAttributes) {
        for (int i = 0; i < subset.size(); i++) {
            EGDEquivalenceClassCells tupleGroup = subset.get(i);
            if (tupleGroup.getWitnessCells().get(backwardAttributes.get(i)) == null) {
                return false;
            }
        }
        return true;
    }

    protected Repair generateRepairWithBackwards(EquivalenceClassForSymmetricEGD equivalenceClass, List<EGDEquivalenceClassCells> forwardTupleGroups, List<EGDEquivalenceClassCells> backwardTupleGroups, List<BackwardAttribute> backwardAttributes,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1) {
            ChangeDescription forwardChanges = generateChangeDescriptionForForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (int i = 0; i < backwardTupleGroups.size(); i++) {
            EGDEquivalenceClassCells backwardTupleGroup = backwardTupleGroups.get(i);
            BackwardAttribute backwardAttribute = backwardAttributes.get(i);
            Set<CellGroup> backwardCellGroups = backwardTupleGroup.getCellGroupsForBackwardRepair().get(backwardAttribute);
            for (CellGroup originalBackwardCellGroup : backwardCellGroups) {
                CellGroup backwardCellGroup = originalBackwardCellGroup.clone();
                LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
                backwardCellGroup.setValue(llunValue);
                backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
                ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, buildWitnessCells(backwardTupleGroups));
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
