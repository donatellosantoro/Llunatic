package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClass;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChange;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Arrays;
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
    public List<Repair> chooseRepairStrategy(EquivalenceClass equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency, Scenario scenario, String stepId,
            OccurrenceHandlerMC occurrenceHandler) {
        assert(scenario.getPartialOrder() instanceof StandardPartialOrder && scenario.getScriptPartialOrder() == null) : "No partial order allowed in min cost repair cost manager " + scenario;
        assert(this.isDoPermutations() == false) : "No permutations allowed in min cost repair cost manager " + scenario;
        assert(this.isDoBackward() == false) : "No backward allowed in min cost repair cost manager " + scenario;
        List<Repair> repairs = super.chooseRepairStrategy(equivalenceClass, chaseTreeRoot, repairsForDependency, scenario, stepId, occurrenceHandler);
        if (repairs.isEmpty()) {
            return repairs;
        }
        Repair forwardRepair = repairs.get(0);
        assert (forwardRepair.getChanges().size() == 1) : "Forward repair must have only one change set: " + forwardRepair;
        correctValuesInRepair(forwardRepair, equivalenceClass);
        return Arrays.asList(new Repair[]{forwardRepair});
    }

    private void correctValuesInRepair(Repair repair, EquivalenceClass equivalenceClass) {
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
        repair.getChanges().get(0).getCellGroup().setValue(minCostValue);
    }

    private double calculateCost(IValue value, EquivalenceClass equivalenceClass) {
        double cost = 0;
        for (TargetCellsToChange targetCellsToChange : equivalenceClass.getTupleGroups()) {
            CellGroup cellGroup = targetCellsToChange.getCellGroupForForwardRepair();
            double distance = 1.0 - SimilarityFactory.getInstance().getStrategy(similarityStrategy).computeSimilarity(value, cellGroup.getValue());
            cost += distance * cellGroup.getOccurrences().size();
        }
        return cost;
    }
    
    @Override
    public boolean isNotViolation(List<TargetCellsToChange> tupleGroups, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Checking violations between tuple groups\n" + LunaticUtility.printCollection(tupleGroups));
        List<CellGroup> cellGroups = extractCellGroups(tupleGroups);
        Set<IValue> differentValues = findDifferentValuesInCellGroupsWithOccurrences(cellGroups);
        return (differentValues.size() == 1);
    }

    @Override
    public String toString() {
        return "MinCost";
    }
    
}
