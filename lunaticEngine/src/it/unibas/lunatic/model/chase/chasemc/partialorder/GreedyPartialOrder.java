package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.CorrectCellGroupID;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import speedy.model.database.IValue;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.comparator.StringComparator;

public class GreedyPartialOrder extends StandardPartialOrder {

    private static final Logger logger = LoggerFactory.getLogger(GreedyPartialOrder.class);
    private final CorrectCellGroupID cellGroupIDFixer = new CorrectCellGroupID();
    private final String similarityStrategy = SimilarityFactory.LEVENSHTEIN_STRATEGY;   

    @Override
    public void setCellGroupValue(CellGroup lubCellGroup, Scenario scenario) {
        IValue lubValue = findMinCostValue(lubCellGroup, scenario);
        if (logger.isDebugEnabled()) logger.debug("LubValue: " + lubValue);
        lubCellGroup.setValue(lubValue);
        cellGroupIDFixer.correctCellGroupId(lubCellGroup);
        IValue finalCellGroupValue = lubCellGroup.getValue();
        for (CellGroupCell occurrence : lubCellGroup.getOccurrences()) {
            occurrence.setValue(finalCellGroupValue);
        }
    }

    private IValue findMinCostValue(CellGroup lubCellGroup, Scenario scenario) {
        IValue minCostValue = null;
        double minCost = Double.MAX_VALUE;
        List<CellGroupCell> orderedOccurrences = new ArrayList<CellGroupCell>(lubCellGroup.getOccurrences());
        Collections.sort(orderedOccurrences, new StringComparator());
        for (CellGroupCell occurrence : orderedOccurrences) {
            IValue value = occurrence.getOriginalValue(); 
//            if (value instanceof LLUNValue) {
//                throw new IllegalArgumentException("Lluns are not allowed in GreedyCostManager..." + lubCellGroup);
//            }
//            double valueChangeCost = calculateCost(value, lubCellGroup.getOccurrences(), scenario.getCostManagerConfiguration().getSimilarityStrategy());
            double valueChangeCost = calculateCost(value, lubCellGroup.getOccurrences(), similarityStrategy);
            if (valueChangeCost < minCost) {
                minCost = valueChangeCost;
                minCostValue = value;
            }
        }
        return minCostValue;
    }

    private double calculateCost(IValue value, Set<CellGroupCell> occurrences, String similarityStrategy) {
        double cost = 0;
        for (CellGroupCell occurrence : occurrences) {
            double distance = 1.0 - SimilarityFactory.getInstance().getStrategy(similarityStrategy).computeSimilarity(value, occurrence.getValue());
            cost += distance;
        }
        return cost;
    }

    @Override
    public String toString() {
        return "Greedy partial order";
    }

}
