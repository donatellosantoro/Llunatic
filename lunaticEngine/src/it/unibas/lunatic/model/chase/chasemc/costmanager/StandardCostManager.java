package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassCells;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericListGenerator;
import it.unibas.lunatic.utility.combinatorial.GenericPowersetGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.StringComparator;
import speedy.model.database.Cell;
import speedy.model.database.LLUNValue;

public class StandardCostManager extends AbstractCostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardCostManager.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        EquivalenceClassForEGD equivalenceClass = (EquivalenceClassForEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<CellGroup> conclusionCellGroups = equivalenceClass.getAllConclusionCellGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && satisfactionChecker.isSatisfiedAfterUpgrades(conclusionCellGroups)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        List<ViolationContext> allContexts = equivalenceClass.getViolationContexts();
        Repair forwardRepair = generateStandardForwardRepair(allContexts, scenario);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot)) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass, scenario, chaseTreeRoot.getDeltaDB(), stepId);
            for (Repair repair : backwardRepairs) {
                if (result.contains(repair)) {
//                    if (logger.isDebugEnabled()) logger.debug("Result already contains repair " + repair + "\nResult: " + result);
                    throw new IllegalArgumentException("Result already contains repair " + repair + "\nResult: " + result);
                }
                LunaticUtility.addIfNotContained(result, repair);
            }
        }
        return result;
    }

    private Repair generateStandardForwardRepair(List<ViolationContext> forwardContexts, Scenario scenario) {
        Repair repair = new Repair();
        List<CellGroup> forwardCellGroups = extractConclusionCellGroupsFromContexts(forwardContexts);
        Set<Cell> contextCells = extractContextCellsFromContexts(forwardContexts);
        CellGroup lub = getLUB(forwardCellGroups, scenario);
        ChangeDescription forwardChanges = new ChangeDescription(lub, LunaticConstants.CHASE_FORWARD, contextCells);
        if (logger.isDebugEnabled()) logger.debug("Forward changes: " + forwardChanges);
        repair.addViolationContext(forwardChanges);
        return repair;
    }

    private List<CellGroup> extractConclusionCellGroupsFromContexts(List<ViolationContext> contexts) {
        Set<CellGroup> result = new HashSet<CellGroup>();
        for (ViolationContext context : contexts) {
            result.addAll(context.getAllConclusionGroups());
        }
        return new ArrayList<CellGroup>(result);
    }

    private Set<Cell> extractContextCellsFromContexts(List<ViolationContext> contexts) {
        Set<Cell> result = new HashSet<Cell>();
        for (ViolationContext context : contexts) {
            Set<Cell> cellsForContext = extractAllCellsFromContext(context);
            result.addAll(cellsForContext);
        }
        return result;
    }

    private Set<Cell> extractAllCellsFromContext(ViolationContext context) {
        Set<Cell> result = new HashSet<Cell>();
        for (CellGroup conclusionGroup : context.getAllConclusionGroups()) {
            result.addAll(conclusionGroup.getOccurrences());
        }
        for (CellGroup witnessCellGroup : context.getAllWitnessCellGroups()) {
            result.addAll(witnessCellGroup.getOccurrences());
        }
        return result;
    }

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

    private List<Repair> generateBackwardRepairs(EquivalenceClassForEGD equivalenceClass, Scenario scenario, IDatabase deltaDB, String stepId) {
        if (equivalenceClass.getSize() > 10) {
            throw new ChaseException("Equivalence class is too big. It is not possible to chase this scenario with standard cost manager: " + equivalenceClass);
        }
        List<Repair> result = new ArrayList<Repair>();
        Set<String> repairFingerprints = new HashSet<String>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for equivalence class :" + equivalenceClass);
        GenericPowersetGenerator<Integer> powersetGenerator = new GenericPowersetGenerator<Integer>();
        List<List<Integer>> powerset = powersetGenerator.generatePowerSet(createIndexes(equivalenceClass.getSize()));
        for (List<Integer> subsetIndex : powerset) {
            if (subsetIndex.isEmpty()) {
                continue;
            }
            Collections.reverse(subsetIndex);
            List<ViolationContext> backwardContexts = extractBackwardContexts(subsetIndex, equivalenceClass);
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
//                if (!canDoBackwardOnAllGroups(backwardCombination, backwardContexts)) {
//                    if (logger.isTraceEnabled()) logger.debug("Cannot do backward for this combination. Discarding " + backwardCombination);
//                    continue;
//                }
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
                Repair repair = generateRepairWithBackwards(forwardContext, backwardCombination, backwardContexts, equivalenceClass, scenario);
                if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward contexts: " + forwardContext + "\nBackward groups: " + backwardCombination + "\n" + repair);
                result.add(repair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + result);
        return result;
    }

    private List<ViolationContext> extractBackwardContexts(List<Integer> subsetIndex, EquivalenceClassForEGD equivalenceClass) {
        List<ViolationContext> result = new ArrayList<ViolationContext>();
        for (Integer index : subsetIndex) {
            result.add(equivalenceClass.getViolationContexts().get(index));
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
        Collections.sort(sortedCombination, new StringComparator<CellGroup>());
        return sortedCombination.toString();
    }

//    private boolean canDoBackwardOnAllGroups(List<CellGroup> backwardCombination, List<ViolationContext> backwardContexts) {
//        for (int i = 0; i < backwardContexts.size(); i++) {
//            ViolationContext violationContext = backwardContexts.get(i);
//            CellGroup backwardCellGroup = backwardCombination.get(i);
//            if (!canDoBackwardOnGroup(backwardCellGroup, violationContext)) {
//                return false;
//            }
//        }
//        return true;
//    }
    private boolean canDoBackwardOnGroup(CellGroup cellGroupToChange, ViolationContext backwardContext) {
        if (!backwardIsAllowed(cellGroupToChange)) {
            if (logger.isDebugEnabled()) logger.debug("Backward is not allowed on group " + cellGroupToChange);
            return false;
        }
//        return true;
//        //Checking that the backward change actually disrupts a join. (Was suspicious in previous versions)
        VariableEquivalenceClass variableEQC = findVariableEquivalenceClassForCellGroup(cellGroupToChange, backwardContext);
        Set<CellGroup> cellGroups = backwardContext.getWitnessCellsForVariable(variableEQC);
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.equals(cellGroupToChange)) {
                continue;
            }
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Backward change doesn't disrupt a join " + cellGroupToChange);
        return false;
    }

    private VariableEquivalenceClass findVariableEquivalenceClassForCellGroup(CellGroup witnessCellGroup, ViolationContext backwardContext) {
        for (VariableEquivalenceClass witnessVariable : backwardContext.getWitnessVariables()) {
            Set<CellGroup> cellGroups = backwardContext.getWitnessCellsForVariable(witnessVariable);
            if (cellGroups.contains(witnessCellGroup)) {
                return witnessVariable;
            }
        }
        throw new IllegalArgumentException("Unable to find variable equivalence class for cell group " + witnessCellGroup + "\n\t in context " + backwardContext);
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
        List<CellGroup> forwardCellGroups = extractConclusionCellGroupsFromContexts(forwardContexts);
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
                if (!canDoBackwardOnGroup(witnessCellGroup, backwardContext)) {
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

    protected boolean allGroupsCanBeBackwardChasedForAttribute(List<EGDEquivalenceClassCells> subset, List<BackwardAttribute> backwardAttributes) {
        for (int i = 0; i < subset.size(); i++) {
            EGDEquivalenceClassCells tupleGroup = subset.get(i);
            if (tupleGroup.getWitnessCells().get(backwardAttributes.get(i)) == null) {
                return false;
            }
        }
        return true;
    }

    private Repair generateRepairWithBackwards(List<ViolationContext> forwardContexts, List<CellGroup> backwardCombination, List<ViolationContext> backwardContexts, EquivalenceClassForEGD equivalenceClass, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Generating repair with forward contexts: " + LunaticUtility.printViolationContextIDs(forwardContexts)
                    + "\n\tbackward contexts: " + LunaticUtility.printViolationContextIDs(backwardContexts)
                    + "\n\tbackward combination: " + backwardCombination);
        Repair repair;
        if (!forwardContexts.isEmpty()) {
            repair = generateStandardForwardRepair(forwardContexts, scenario);
        } else {
            repair = new Repair();
        }
        for (CellGroup backwardGroup : backwardCombination) {
            if (hasBeenChanged(backwardGroup, repair)) {
                continue;
            }
            CellGroup backwardCellGroup = backwardGroup.clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            Set<Cell> contextCells = extractContextCellsFromContexts(backwardContexts);
            ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, contextCells);
            repair.addViolationContext(backwardChangesForGroup);
        }
        return repair;
    }

    private boolean hasBeenChanged(CellGroup backwardGroup, Repair repair) {
        for (ChangeDescription changeDescription : repair.getChangeDescriptions()) {
            if (changeDescription.getCellGroup().getOccurrences().containsAll(backwardGroup.getOccurrences())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Standard";
    }

}
