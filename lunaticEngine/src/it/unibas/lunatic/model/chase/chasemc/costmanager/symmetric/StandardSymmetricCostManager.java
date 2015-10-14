package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTupleCells;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericMultiCombinationsGenerator;
import it.unibas.lunatic.utility.combinatorial.GenericPowersetGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;

public class StandardSymmetricCostManager implements ICostManager {

    private static Logger logger = LoggerFactory.getLogger(StandardSymmetricCostManager.class);

    @SuppressWarnings("unchecked")
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGDProxy equivalenceClassProxy, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass.toLongString());
        List<Repair> result = new ArrayList<Repair>();
        Repair forwardRepair = CostManagerUtility.generateSymmetricForwardRepair(equivalenceClass, scenario, chaseTreeRoot.getDeltaDB(), stepId);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot, scenario.getCostManagerConfiguration())) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass, scenario, chaseTreeRoot.getDeltaDB(), stepId);
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

    private boolean canDoBackward(DeltaChaseStep chaseTreeRoot, CostManagerConfiguration costManagerConfiguration) {
        if (costManagerConfiguration.isDoBackward()) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
            if (CostManagerUtility.isTreeSizeBelowThreshold(chaseBranching, potentialSolutions, costManagerConfiguration)) {
                return true;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Skipping backward. " + costManagerConfiguration.toString() + " Chase tree leaves: " + chaseTreeRoot.getNumberOfLeaves());
        return false;
    }

    private List<Repair> generateBackwardRepairs(EquivalenceClassForSymmetricEGD equivalenceClass, Scenario scenario, IDatabase deltaDB, String stepId) {
        List<EGDEquivalenceClassTupleCells> allTupleCells = equivalenceClass.getAllTupleCells();
        if (allTupleCells.size() > 5) {
            throw new ChaseException("Tuple cells of excessive size, it is not possible to chase this scenario: " + equivalenceClass);
        }
        List<Repair> result = new ArrayList<Repair>();
        if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for equivalence class:\n" + equivalenceClass);
        GenericPowersetGenerator<Integer> powersetGenerator = new GenericPowersetGenerator<Integer>();
        List<List<Integer>> powerset = powersetGenerator.generatePowerSet(CostManagerUtility.createIndexes(allTupleCells.size()));
        for (List<Integer> subsetIndex : powerset) {
            if (subsetIndex.isEmpty() || subsetIndex.size() == allTupleCells.size()) {
                continue;
            }
            Collections.reverse(subsetIndex);
            List<EGDEquivalenceClassTupleCells> subset = extractSubset(subsetIndex, allTupleCells);
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset indexes: " + subsetIndex);
            if (logger.isDebugEnabled()) logger.debug("Attributes to change for backward chasing: " + equivalenceClass.getAttributesToChangeForBackwardChasing());
            List<BackwardAttribute> backwardAttributes = equivalenceClass.getAttributesToChangeForBackwardChasing();
            GenericMultiCombinationsGenerator<BackwardAttribute> combinationGenerator = new GenericMultiCombinationsGenerator<BackwardAttribute>();
            List<List<BackwardAttribute>> combinations = combinationGenerator.generate(backwardAttributes, subset.size());
            for (List<BackwardAttribute> backwardAttributeCombination : combinations) {
                if (logger.isDebugEnabled()) logger.debug("BackwardAttributeCombination: " + backwardAttributeCombination);
                List<EGDEquivalenceClassTupleCells> forwardTuples = new ArrayList<EGDEquivalenceClassTupleCells>(allTupleCells);
                List<EGDEquivalenceClassTupleCells> backwardTuples = new ArrayList<EGDEquivalenceClassTupleCells>();
                for (int i = 0; i < subset.size(); i++) {
                    BackwardAttribute backwardAttribute = backwardAttributeCombination.get(i);
                    EGDEquivalenceClassTupleCells tupleCells = subset.get(i);
                    CellGroup backwardCellGroup = tupleCells.getCellGroupForBackwardAttribute(backwardAttribute);
                    if (!CostManagerUtility.backwardIsAllowed(backwardCellGroup)) {
                        if (logger.isDebugEnabled()) logger.debug("Backward is not allowed. Discarding cell group " + backwardCellGroup);
                        continue;
                    }
                    forwardTuples.remove(tupleCells);
                    backwardTuples.add(tupleCells);
                }
                if (backwardTuples.isEmpty() || forwardTuples.isEmpty()) {
                    if (logger.isDebugEnabled()) logger.debug("Forward or Backward is empty. Discarding cell group");
                    continue;
                }
                if (CostManagerUtility.joinsAreNotDisrupted(equivalenceClass, forwardTuples, backwardTuples, backwardAttributeCombination)) {
                    if (logger.isDebugEnabled()) logger.debug("Forward or Backward do not disrupt all join . Discarding cell group.\nForward: " + forwardTuples + "\nBackward groups: " + backwardTuples + "\nBackward Attributes " + backwardAttributeCombination);
                    continue;
                }
                Repair repair = generateRepairWithBackwards(forwardTuples, backwardTuples, backwardAttributeCombination, scenario, deltaDB, stepId);
                if (!checkRepairMinimality(repair, forwardTuples, backwardTuples, equivalenceClass)) {
                    if (logger.isDebugEnabled()) logger.debug("Repair is not consistent. Discarding " + repair);
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("Generating repair for: \nForward groups: " + forwardTuples + "\nBackward groups: " + backwardTuples + "\n" + repair);
                result.add(repair);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + result);
        return result;
    }

    private List<EGDEquivalenceClassTupleCells> extractSubset(List<Integer> subsetIndex, List<EGDEquivalenceClassTupleCells> tupleGroups) {
        List<EGDEquivalenceClassTupleCells> result = new ArrayList<EGDEquivalenceClassTupleCells>();
        for (Integer index : subsetIndex) {
            result.add(tupleGroups.get(index));
        }
        return result;
    }

    private Repair generateRepairWithBackwards(List<EGDEquivalenceClassTupleCells> forwardTupleGroups, List<EGDEquivalenceClassTupleCells> backwardTupleGroups, List<BackwardAttribute> backwardAttributes,
            Scenario scenario, IDatabase deltaDB, String stepId) {
        Repair repair = new Repair();
        if (forwardTupleGroups.size() > 1 && haveDifferentConclusionValues(forwardTupleGroups)) {
//        if (forwardTupleGroups.size() > 1) {
            ChangeDescription forwardChanges = CostManagerUtility.generateChangeDescriptionForSymmetricForwardRepair(forwardTupleGroups, scenario, deltaDB, stepId);
            repair.addViolationContext(forwardChanges);
        }
        for (int i = 0; i < backwardTupleGroups.size(); i++) {
            EGDEquivalenceClassTupleCells backwardTupleCells = backwardTupleGroups.get(i);
            BackwardAttribute backwardAttribute = backwardAttributes.get(i);
            CellGroup backwardCellGroups = backwardTupleCells.getCellGroupForBackwardAttribute(backwardAttribute);
            CellGroup backwardCellGroup = backwardCellGroups.clone();
            LLUNValue llunValue = CellGroupIDGenerator.getNextLLUNID();
            backwardCellGroup.setValue(llunValue);
            backwardCellGroup.setInvalidCell(CellGroupIDGenerator.getNextInvalidCell());
            ChangeDescription backwardChangesForGroup = new ChangeDescription(backwardCellGroup, LunaticConstants.CHASE_BACKWARD, CostManagerUtility.buildWitnessCells(backwardTupleGroups));
            repair.addViolationContext(backwardChangesForGroup);
        }
        return repair;
    }

    private boolean haveDifferentConclusionValues(List<EGDEquivalenceClassTupleCells> forwardTupleGroups) {
        IValue firstValue = forwardTupleGroups.get(0).getConclusionGroup().getValue();
        for (EGDEquivalenceClassTupleCells forwardTupleGroup : forwardTupleGroups) {
            IValue otherValue = forwardTupleGroup.getConclusionGroup().getValue();
            if (!firstValue.equals(otherValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRepairMinimality(Repair repair, List<EGDEquivalenceClassTupleCells> forwardTuples, List<EGDEquivalenceClassTupleCells> backwardTuples, EquivalenceClassForSymmetricEGD equivalenceClass) {
        IValue forwardValue = findForwardValue(repair);
        if (forwardValue != null && (forwardValue instanceof LLUNValue)) {
            //Forward repair generates a llun. Backward repairs are needed
            return true;
        }
        for (EGDEquivalenceClassTupleCells backwardTuple : backwardTuples) {
            IValue groupValue = backwardTuple.getConclusionGroup().getValue();
            List<EGDEquivalenceClassTupleCells> tuplesInGroup = equivalenceClass.getTuplesWithConclusionValue(groupValue);
            if (!forwardTuplesInGroup(tuplesInGroup, forwardTuples)) {
                continue;
            }
            if (forwardValue == null || forwardValue.equals(groupValue)) {
                //This backward is useless because forward tuples of the same group have not been changed
                return false;
            }
        }
        return true;
    }

    private IValue findForwardValue(Repair repair) {
        ChangeDescription firstChangeDescription = repair.getChangeDescriptions().get(0);
        if (firstChangeDescription.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
            return null;
        }
        return firstChangeDescription.getCellGroup().getValue();
    }

    private boolean forwardTuplesInGroup(List<EGDEquivalenceClassTupleCells> tuplesInGroup, List<EGDEquivalenceClassTupleCells> forwardTuples) {
        for (EGDEquivalenceClassTupleCells tupleInGroup : tuplesInGroup) {
            if (forwardTuples.contains(tupleInGroup)) {
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
