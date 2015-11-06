package it.unibas.lunatic.model.chase.chasemc.costmanager.symmetric;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTuple;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForSymmetricEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import it.unibas.lunatic.model.dependency.Dependency;
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
            List<Repair> repairsForDependency, Scenario scenario, String stepId, IOccurrenceHandler occurrenceHandler) {
        EquivalenceClassForSymmetricEGD equivalenceClass = (EquivalenceClassForSymmetricEGD) equivalenceClassProxy.getEquivalenceClass();
        if (logger.isInfoEnabled()) logger.info("Chasing dependency " + equivalenceClass.getEGD().getId() + " with cost manager " + this.getClass().getSimpleName() + " and partial order " + scenario.getPartialOrder().getClass().getSimpleName());
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + stepId);
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass.toLongString());
        List<Repair> result = new ArrayList<Repair>();
        Repair forwardRepair = CostManagerUtility.generateSymmetricForwardRepair(equivalenceClass.getAllTupleCells(), scenario);
        result.add(forwardRepair);
        if (canDoBackward(chaseTreeRoot, equivalenceClass.getEGD(), scenario.getCostManagerConfiguration())) {
            List<Repair> backwardRepairs = generateBackwardRepairs(equivalenceClass, scenario);
            for (Repair repair : backwardRepairs) {
//                if (result.contains(repair)) {
//                    throw new ChaseException("Result already contains repair " + repair + "\nResult: " + result);
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
        if (logger.isDebugEnabled()) logger.debug("Skipping backward. " + costManagerConfiguration.toString() + " Chase tree leaves: " + chaseTreeRoot.getNumberOfLeaves());
        return false;
    }

    private List<Repair> generateBackwardRepairs(EquivalenceClassForSymmetricEGD equivalenceClass, Scenario scenario) {
        List<EGDEquivalenceClassTuple> allTupleCells = equivalenceClass.getAllTupleCells();
        if (allTupleCells.size() > 10) {
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
            List<EGDEquivalenceClassTuple> subset = extractSubset(subsetIndex, allTupleCells);
            if (logger.isDebugEnabled()) logger.debug("Generating backward repairs for subset indexes: " + subsetIndex);
            if (logger.isDebugEnabled()) logger.debug("Attributes to change for backward chasing: " + equivalenceClass.getAttributesToChangeForBackwardChasing());
            List<BackwardAttribute> backwardAttributes = equivalenceClass.getAttributesToChangeForBackwardChasing();
            GenericMultiCombinationsGenerator<BackwardAttribute> combinationGenerator = new GenericMultiCombinationsGenerator<BackwardAttribute>();
            List<List<BackwardAttribute>> combinations = combinationGenerator.generate(backwardAttributes, subset.size());
            for (List<BackwardAttribute> backwardAttributeCombination : combinations) {
                if (logger.isDebugEnabled()) logger.debug("BackwardAttributeCombination: " + backwardAttributeCombination);
                List<EGDEquivalenceClassTuple> forwardTuples = new ArrayList<EGDEquivalenceClassTuple>(allTupleCells);
                List<EGDEquivalenceClassTuple> backwardTuples = new ArrayList<EGDEquivalenceClassTuple>();
                for (int i = 0; i < subset.size(); i++) {
                    BackwardAttribute backwardAttribute = backwardAttributeCombination.get(i);
                    EGDEquivalenceClassTuple tupleCells = subset.get(i);
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
                List<CellGroup> backwardCellGroups = extractBackwardCellGroups(backwardTuples, backwardAttributeCombination);
                Repair repair = CostManagerUtility.generateSymmetricRepairWithBackwards(forwardTuples, backwardTuples, backwardCellGroups, scenario);
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

    private List<CellGroup> extractBackwardCellGroups(List<EGDEquivalenceClassTuple> backwardTuples, List<BackwardAttribute> backwardAttributeCombination) {
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (int i = 0; i < backwardTuples.size(); i++) {
            EGDEquivalenceClassTuple tuple = backwardTuples.get(i);
            BackwardAttribute attribute = backwardAttributeCombination.get(i);
            result.add(tuple.getCellGroupForBackwardAttribute(attribute));
        }
        return result;
    }

    private List<EGDEquivalenceClassTuple> extractSubset(List<Integer> subsetIndex, List<EGDEquivalenceClassTuple> tupleGroups) {
        List<EGDEquivalenceClassTuple> result = new ArrayList<EGDEquivalenceClassTuple>();
        for (Integer index : subsetIndex) {
            result.add(tupleGroups.get(index));
        }
        return result;
    }

    private boolean checkRepairMinimality(Repair repair, List<EGDEquivalenceClassTuple> forwardTuples, List<EGDEquivalenceClassTuple> backwardTuples, EquivalenceClassForSymmetricEGD equivalenceClass) {
        IValue forwardValue = findForwardValue(repair);
        if (forwardValue != null && (forwardValue instanceof LLUNValue)) {
            //Forward repair generates a llun. Backward repairs are needed
            return true;
        }
        for (EGDEquivalenceClassTuple backwardTuple : backwardTuples) {
            IValue groupValue = backwardTuple.getConclusionGroup().getValue();
            List<EGDEquivalenceClassTuple> tuplesInGroup = equivalenceClass.getTuplesWithConclusionValue(groupValue);
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

    private boolean forwardTuplesInGroup(List<EGDEquivalenceClassTuple> tuplesInGroup, List<EGDEquivalenceClassTuple> forwardTuples) {
        for (EGDEquivalenceClassTuple tupleInGroup : tuplesInGroup) {
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
