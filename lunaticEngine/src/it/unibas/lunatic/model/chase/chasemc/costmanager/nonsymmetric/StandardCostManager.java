package it.unibas.lunatic.model.chase.chasemc.costmanager.nonsymmetric;

import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSatisfactionAfterUpgradesEGD;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.spicy.utility.GenericPowersetGenerator;
import speedy.utility.combinatorics.GenericListGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.Cell;
import speedy.utility.comparator.StringComparator;

public class StandardCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardCostManager.class);
    private CheckSatisfactionAfterUpgradesEGD satisfactionChecker = new CheckSatisfactionAfterUpgradesEGD();

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId, IOccurrenceHandler occurrenceHandler) {
        EquivalenceClassForEGD equivalenceClass = (EquivalenceClassForEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName() + " in step " + stepId);
        if (logger.isDebugEnabled()) logger.debug("######## Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("######## Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<CellGroup> conclusionCellGroups = equivalenceClass.getAllConclusionCellGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(conclusionCellGroups)) {
            if (logger.isDebugEnabled()) logger.debug("Dependency " + equivalenceClass.getEGD() + " is satisfied after upgrades in step " + stepId);
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        List<ViolationContext> allContexts = equivalenceClass.getViolationContexts();
        Repair forwardRepair = CostManagerUtility.generateForwardRepair(allContexts, scenario);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot, equivalenceClass.getEGD(), scenario.getCostManagerConfiguration())) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass, scenario, chaseTreeRoot.getDeltaDB(), stepId);
            for (Repair repair : backwardRepairs) {
//                if (result.contains(repair)) {
////                    if (logger.isDebugEnabled()) logger.debug("Result already contains repair " + repair + "\nResult: " + result);
//                    throw new IllegalArgumentException("Result already contains repair " + repair + "\nResult: " + result);
//                }
                LunaticUtility.addIfNotContained(result, repair);
            }
        }
        return result;
    }

    private boolean canDoBackward(DeltaChaseStep chaseTreeRoot, Dependency egd, CostManagerConfiguration costManagerConfiguration) {
        if (costManagerConfiguration.isDoBackwardOnDependency(egd)) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
            if (CostManagerUtility.isTreeSizeBelowThreshold(chaseBranching, potentialSolutions, costManagerConfiguration)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<Repair> generateBackwardRepairs(EquivalenceClassForEGD equivalenceClass, Scenario scenario, IDatabase deltaDB, String stepId) {
        List<ViolationContext> violationContextsForBackward = extractViolationContextWithWitnessCells(equivalenceClass);
        if (violationContextsForBackward.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (violationContextsForBackward.size() > 10) {
            throw new ChaseException("Equivalence class is too big (" + violationContextsForBackward.size() + "). It is not possible to chase this scenario with standard cost manager: " + equivalenceClass);
        }
        List<Repair> result = new ArrayList<Repair>();
        Set<String> repairFingerprints = new HashSet<String>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for equivalence class :" + equivalenceClass);
        GenericPowersetGenerator<Integer> powersetGenerator = new GenericPowersetGenerator<Integer>();
//        List<List<Integer>> powerset = powersetGenerator.generatePowerSet(CostManagerUtility.createIndexes(equivalenceClass.getSize()));
        List<List<Integer>> powerset = powersetGenerator.generatePowerSet(CostManagerUtility.createIndexes(violationContextsForBackward.size()));
        for (List<Integer> subsetIndex : powerset) {
            if (subsetIndex.isEmpty()) {
                continue;
            }
            Collections.reverse(subsetIndex);
            List<ViolationContext> backwardContexts = extractBackwardContexts(subsetIndex, violationContextsForBackward);
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset indexes: " + subsetIndex);
            List<List<CellGroup>> backwardCellGroups = extractBackwardCellGroups(backwardContexts);
            if (backwardCellGroups == null) {
                //Backward is not allowed on some of the contexts
                if (logger.isDebugEnabled()) logger.debug("Cannot do backward on all contexts. Discarding");
                continue;
            }
            GenericListGenerator<CellGroup> listGenerator = new GenericListGenerator<CellGroup>();
            List<List<CellGroup>> backwardCombinations = listGenerator.generateListsOfElements(backwardCellGroups);
            for (List<CellGroup> backwardCombination : backwardCombinations) {
                if (logger.isDebugEnabled()) logger.debug("Generating repairs for backward combination " + backwardCombination);
                List<ViolationContext> forwardContext = extractForwardContext(backwardContexts, equivalenceClass);
                if (!checkConsistencyOfBackwardChanges(backwardCombination, backwardContexts, forwardContext, equivalenceClass)) {
                    if (logger.isTraceEnabled()) logger.debug("Cell group combination for backward is not consistent. Discarding " + backwardCombination);
                    continue;
                }
                if (!checkConsistencyOfForwardChanges(forwardContext, backwardContexts, equivalenceClass)) {
                    if (logger.isDebugEnabled()) logger.debug("Inconsistent subset. Discarding " + LunaticUtility.printViolationContextIDs(backwardContexts));
                    continue;
                }
                String repairFingerprint = generateRepairFingerprint(backwardCombination);
                if (repairFingerprints.contains(repairFingerprint)) {
                    if (logger.isDebugEnabled()) logger.debug("Duplicate repair fingerprint. Discarding " + repairFingerprint);
                    continue;
                }
                repairFingerprints.add(repairFingerprint);
                Repair repair = CostManagerUtility.generateRepairWithBackwards(forwardContext, backwardCombination, backwardContexts, scenario);
                if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward contexts: " + forwardContext + "\nBackward groups: " + backwardCombination + "\n" + repair);
                result.add(repair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + result);
        return result;
    }

    private List<ViolationContext> extractViolationContextWithWitnessCells(EquivalenceClassForEGD equivalenceClass) {
        List<ViolationContext> result = new ArrayList<ViolationContext>();
        for (ViolationContext violationContext : equivalenceClass.getViolationContexts()) {
            if (violationContext.hasWitnessCells()) {
                result.add(violationContext);
            }
        }
        return result;
    }

    private List<ViolationContext> extractBackwardContexts(List<Integer> subsetIndex, List<ViolationContext> violationContextsForBackward) {
        List<ViolationContext> result = new ArrayList<ViolationContext>();
        for (Integer index : subsetIndex) {
            result.add(violationContextsForBackward.get(index));
        }
        return result;
    }

    private List<ViolationContext> extractForwardContext(List<ViolationContext> backwardContexts, EquivalenceClassForEGD equivalenceClass) {
        List<ViolationContext> result = new ArrayList<ViolationContext>(equivalenceClass.getViolationContexts());
        result.removeAll(backwardContexts);
        return result;
    }

    private String generateRepairFingerprint(List<CellGroup> backwardCombination) {
        Set<CellGroup> cellGroupSet = new HashSet<CellGroup>(backwardCombination);
        List<CellGroup> sortedCombination = new ArrayList<CellGroup>(cellGroupSet);
        Collections.sort(sortedCombination, new StringComparator());
        return sortedCombination.toString();
    }

    private boolean checkConsistencyOfForwardChanges(List<ViolationContext> forwardContexts, List<ViolationContext> backwardContexts, EquivalenceClassForEGD equivalenceClass) {
        //After a context has been forward chased, the conflict of those values has been removed.
        for (ViolationContext forwardContext : forwardContexts) {
            List<ViolationContext> contextsWithSameConclusionGroups = findContextsWithSameConclusionGroups(forwardContext, forwardContexts);
            if (!forwardContexts.containsAll(contextsWithSameConclusionGroups)) {
                return false;
            }
        }
        //Check that after enriching cell groups, forward changes do not interfear with backward changes
        Set<Cell> forwardCells = extractForwardCells(forwardContexts);
        for (ViolationContext backwardContext : backwardContexts) {
            Set<Cell> forwardCellsForBackwardContext = extractForwardCells(Arrays.asList(new ViolationContext[]{backwardContext}));
            if (forwardCells.containsAll(forwardCellsForBackwardContext)) {
                return false;
            }
        }
        return true;
    }

    private Set<Cell> extractForwardCells(List<ViolationContext> forwardContexts) {
        Set<Cell> result = new HashSet<Cell>();
        List<CellGroup> forwardCellGroups = CostManagerUtility.extractConclusionCellGroupsFromContexts(forwardContexts);
        for (CellGroup forwardCellGroup : forwardCellGroups) {
            result.addAll(forwardCellGroup.getOccurrences());
        }
        return result;
    }

    private List<ViolationContext> findContextsWithSameConclusionGroups(ViolationContext forwardContext, List<ViolationContext> forwardContexts) {
        List<ViolationContext> result = new ArrayList<ViolationContext>();
        for (ViolationContext context : forwardContexts) {
            if (context.equals(forwardContext)) {
                continue;
            }
            if (hasEqualsConclusionGroups(forwardContext, context)) {
                result.add(context);
            }
        }
        return result;
    }

    private boolean hasEqualsConclusionGroups(ViolationContext forwardContext, ViolationContext context) {
        return forwardContext.getAllConclusionGroups().equals(context.getAllConclusionGroups());
    }

    private boolean checkConsistencyOfBackwardChanges(List<CellGroup> backwardCombination, List<ViolationContext> backwardContexts, List<ViolationContext> forwardContext, EquivalenceClassForEGD equivalenceClass) {
        for (CellGroup backwardCellGroup : backwardCombination) {
            Set<ViolationContext> contextsForCellGroup = getContextsForCellGroup(backwardCellGroup, equivalenceClass, backwardCombination, backwardContexts);
            for (ViolationContext violationContext : contextsForCellGroup) {
                //If this context is backward, it must be fixed by changing the same cell group
                if (backwardContexts.contains(violationContext)) {
                    CellGroup changedCellGroup = findCellGroupToChangeForContext(violationContext, backwardCombination, backwardContexts);
                    if (!changedCellGroup.equals(backwardCellGroup)) {
                        return false;
                    }
                }
                //If this context is forward, then it is already fixed
                if (forwardContext.contains(violationContext)) {
                    if (logger.isDebugEnabled()) logger.debug("A Backward repair on CellGroup " + backwardCellGroup + " will fix forward context " + violationContext.toShortString());
                    forwardContext.remove(violationContext);
                }
            }
        }
        return true;
    }

    private Set<ViolationContext> getContextsForCellGroup(CellGroup backwardCellGroup, EquivalenceClassForEGD equivalenceClass, List<CellGroup> backwardCombination, List<ViolationContext> backwardContexts) {
        Set<ViolationContext> result = new HashSet<ViolationContext>();
        for (Cell backwardCell : backwardCellGroup.getOccurrences()) {
            List<ViolationContext> contextsForCell = equivalenceClass.getViolationContextsForCell(backwardCell);
            result.addAll(contextsForCell);
        }
        return result;
    }

    private CellGroup findCellGroupToChangeForContext(ViolationContext cellContext, List<CellGroup> backwardCombination, List<ViolationContext> backwardContexts) {
        return backwardCombination.get(backwardContexts.indexOf(cellContext));
    }

    private List<List<CellGroup>> extractBackwardCellGroups(List<ViolationContext> backwardContexts) {
        List<List<CellGroup>> result = new ArrayList<List<CellGroup>>();
        for (ViolationContext backwardContext : backwardContexts) {
            List<CellGroup> cellGroupsForContext = new ArrayList<CellGroup>();
            for (CellGroup witnessCellGroup : backwardContext.getAllWitnessCellGroups()) {
                if (!CostManagerUtility.canDoBackwardOnGroup(witnessCellGroup, backwardContext)) {
                    if (logger.isDebugEnabled()) logger.debug("Cannot do backward on cell group " + witnessCellGroup + " with context " + backwardContext);
                    continue;
                }
                cellGroupsForContext.add(witnessCellGroup);
            }
            if (cellGroupsForContext.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Cannot do backward on contexts " + backwardContexts);
                return null;
            }
            result.add(cellGroupsForContext);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Standard";
    }

}
