package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinCostRepairCostManager extends StandardCostManager {

    private static Logger logger = LoggerFactory.getLogger(MinCostRepairCostManager.class);

    //    private String similarityStrategy = SimilarityFactory.SIMPLE_EDITS;
    private String similarityStrategy = SimilarityFactory.LEVENSHTEIN_STRATEGY;   
    
    @Override
    public boolean isDoBackward() {
        return false;
    }

    @Override
    public void setDoBackward(boolean doBackward) {
        if (doBackward) {
            throw new IllegalArgumentException("Min cost repair cost manager cannot do backward repairs");
        }
        super.setDoBackward(doBackward);
    }

    @Override
    public boolean isDoPermutations() {
        return false;
    }

    @Override
    public void setDoPermutations(boolean doPermutations) {
        if (doPermutations) {
            throw new IllegalArgumentException("Min cost repair cost manager cannot do permutations");
        }
        super.setDoPermutations(doPermutations);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Repair> chooseRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        assert(scenario.getPartialOrder() instanceof StandardPartialOrder && scenario.getScriptPartialOrder() == null) : "No partial order allowed in min cost repair cost manager " + scenario;
        assert(this.isDoPermutations() == false) : "No permutations allowed in min cost repair cost manager " + scenario;
        assert(this.isDoBackward() == false) : "No backward allowed in min cost repair cost manager " + scenario;
        List<Repair> repairs = generateStandardRepairStrategy(equivalenceClass, chaseTreeRoot, repairsForDependency, scenario, stepId, occurrenceHandler);
        if (repairs.isEmpty()) {
            return repairs;
        }
        Repair forwardRepair = repairs.get(0);
        assert (forwardRepair.getViolationContexts().size() == 1) : "Forward repair must have only one change set: " + forwardRepair;
        correctValuesInRepair(forwardRepair, equivalenceClass);
        return Arrays.asList(new Repair[]{forwardRepair});
    }
    
    @SuppressWarnings("unchecked")
    private List<Repair> generateStandardRepairStrategy(EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        if (logger.isDebugEnabled()) logger.debug("########Current node: " + chaseTreeRoot.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("########Choosing repair strategy for equivalence class: " + equivalenceClass);
        List<TargetCellsToChangeForEGD> tupleGroupsWithSameConclusionValue = equivalenceClass.getTupleGroups();
        if (DependencyUtility.hasSourceSymbols(equivalenceClass.getEGD()) && isSatisfiedAfterUpgrades(tupleGroupsWithSameConclusionValue, scenario)) {
            return Collections.EMPTY_LIST;
        }
        List<Repair> result = new ArrayList<Repair>();
        // generate forward repair for all groups
        ViolationContext changesForForwardRepair = generateForwardRepair(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId);
        Repair forwardRepair = new Repair();
        forwardRepair.addViolationContext(changesForForwardRepair);
        if (logger.isDebugEnabled()) logger.debug("########Forward repair: " + forwardRepair);
        result.add(forwardRepair);
        if (isDoBackward()) {
            // check if repairs with backward chasing are possible
            int chaseBranching = chaseTreeRoot.getNumberOfLeaves();
            int potentialSolutions = chaseTreeRoot.getPotentialSolutions();
            if (isTreeSizeBelowThreshold(chaseBranching, potentialSolutions)) {
                List<Repair> backwardRepairs = super.generateBackwardRepairs(equivalenceClass.getTupleGroups(), scenario, chaseTreeRoot.getDeltaDB(), stepId, equivalenceClass);
                for (Repair repair : backwardRepairs) {
                    LunaticUtility.addIfNotContained(result, repair);
                }
                if (logger.isDebugEnabled()) logger.debug("########Backward repairs: " + backwardRepairs);
            }
        }
        return result;
    }    

    private void correctValuesInRepair(Repair repair, EquivalenceClassForEGD equivalenceClass) {
        IValue minCostValue = null;
        double minCost = Double.MAX_VALUE;
        for (int i = 0; i < equivalenceClass.getTupleGroups().size(); i++) {
            IValue value = equivalenceClass.getTupleGroups().get(i).getCellGroupForForwardRepair().getValue();
            double valueChangeCost = calculateCost(value, equivalenceClass);
            if (valueChangeCost < minCost) {
                minCost = valueChangeCost;
                minCostValue = value;
            }
        }
        repair.getViolationContexts().get(0).getCellGroup().setValue(minCostValue);
    }

    private double calculateCost(IValue value, EquivalenceClassForEGD equivalenceClass) {
        double cost = 0;
        for (TargetCellsToChangeForEGD targetCellsToChange : equivalenceClass.getTupleGroups()) {
            CellGroup cellGroup = targetCellsToChange.getCellGroupForForwardRepair();
            double distance = 1.0 - SimilarityFactory.getInstance().getStrategy(similarityStrategy).computeSimilarity(value, cellGroup.getValue());
            cost += distance * cellGroup.getOccurrences().size();
        }
        return cost;
    }
    
    private boolean isSatisfiedAfterUpgrades(List<TargetCellsToChangeForEGD> tupleGroups, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Checking violations between tuple groups\n" + LunaticUtility.printCollection(tupleGroups));
        List<CellGroup> cellGroups = extractCellGroups(tupleGroups);
        Set<IValue> differentValues = CellGroupUtility.findDifferentValuesInCellGroupsWithOccurrences(cellGroups);
        return (differentValues.size() == 1);
    }

    @Override
    public String toString() {
        return "MinCost";
    }
    
}
