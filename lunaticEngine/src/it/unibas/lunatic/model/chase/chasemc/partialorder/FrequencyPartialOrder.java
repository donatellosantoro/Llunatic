package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import java.util.ArrayList;
import speedy.model.database.IValue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;

public class FrequencyPartialOrder extends StandardPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(FrequencyPartialOrder.class);

    @Override
    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> constantCells, CellGroup cellGroup, Scenario scenario) {
        IValue lubValue = super.generalizeNonAuthoritativeConstantCells(constantCells, cellGroup, scenario);
        if (!(lubValue instanceof LLUNValue)) {
            return lubValue;
        }
        Map<IValue, Integer> occurrenceHistogram = buildSimilarityHistogram(constantCells, scenario.getCostManagerConfiguration());
        if (logger.isDebugEnabled()) logger.debug("Histogram for cells " + constantCells + "\n" + occurrenceHistogram);
        IValue mostFrequentValue = findValueWithMostSimilarOccurrences(occurrenceHistogram);
        if (mostFrequentValue != null) {
            if (logger.isDebugEnabled()) logger.debug("Most frequent value in cell group:" + cellGroup + "\n\t ->" + mostFrequentValue);
            return mostFrequentValue;
        }
        return lubValue;
    }

    private IValue findValueWithMostSimilarOccurrences(Map<IValue, Integer> occurrenceHistogram) {
        List<Map.Entry<IValue, Integer>> entryList = ChaseUtility.sortEntriesWithValues(occurrenceHistogram);
        IValue firstMaxValue = entryList.get(0).getKey();
        return firstMaxValue;
    }

    private Map<IValue, Integer> buildSimilarityHistogram(Set<CellGroupCell> nonAuthoritativeCells, CostManagerConfiguration costManagerConfiguration) {
        Set<IValue> allValues = extractAllOriginalValues(nonAuthoritativeCells);
        Map<IValue, Set<CellGroupCell>> similarityMap = new HashMap<IValue, Set<CellGroupCell>>();
        for (IValue value : allValues) {
            Set<CellGroupCell> similarCells = findSimilarCells(nonAuthoritativeCells, value, costManagerConfiguration);
            similarityMap.put(value, similarCells);
        }
        Map<IValue, Integer> result = new HashMap<IValue, Integer>();
        for (Set<CellGroupCell> similarCells : similarityMap.values()) {
            IValue mostFrequentValue = findFirstMaximallyFrequentValue(similarCells);
            result.put(mostFrequentValue, similarCells.size());
        }
        return result;
    }

    private Set<CellGroupCell> findSimilarCells(Set<CellGroupCell> nonAuthoritativeCells, IValue value, CostManagerConfiguration costManagerConfiguration) {
        String similarityStrategy = costManagerConfiguration.getSimilarityStrategy();
        double similarityThreshold = costManagerConfiguration.getSimilarityThreshold();
        Set<CellGroupCell> similarCells = new HashSet<CellGroupCell>();
        for (CellGroupCell nonAuthoritativeCell : nonAuthoritativeCells) {
            IValue originalValue = nonAuthoritativeCell.getOriginalValue();
            if (CostManagerUtility.areSimilar(value, originalValue, similarityStrategy, similarityThreshold)) {
                similarCells.add(nonAuthoritativeCell);
            }
        }
        return similarCells;
    }

    private Set<IValue> extractAllOriginalValues(Set<CellGroupCell> nonAuthoritativeCells) {
        Set<IValue> result = new HashSet<IValue>();
        for (CellGroupCell cell : nonAuthoritativeCells) {
            IValue originalValue = cell.getOriginalValue();
            result.add(originalValue);
        }
        return result;
    }

    private IValue findFirstMaximallyFrequentValue(Set<CellGroupCell> nonAuthoritativeCells) {
        Map<IValue, Integer> occurrenceHistogram = buildOccurrenceHistogram(nonAuthoritativeCells);
        List<Map.Entry<IValue, Integer>> entryList = ChaseUtility.sortEntriesWithValues(occurrenceHistogram);
        IValue firstMaxValue = entryList.get(0).getKey();
        return firstMaxValue;
//        Integer firstMax = entryList.get(0).getValue();
//        Integer secondMax = null;
//        if (entryList.size() > 1) {
//            secondMax = entryList.get(1).getValue();
//        }
//        if (secondMax == null || firstMax > secondMax) { 
//            return firstMaxValue;
//        }
//        return null;
    }

    private Map<IValue, Integer> buildOccurrenceHistogram(Set<CellGroupCell> nonAuthoritativeCells) {
        Map<IValue, Integer> result = new HashMap<IValue, Integer>();
        for (CellGroupCell nonAuthoritativeCell : nonAuthoritativeCells) {
            IValue originalValue = nonAuthoritativeCell.getOriginalValue();
            Integer occurrences = result.get(originalValue);
            if (occurrences == null) {
                result.put(originalValue, 1);
            } else {
                occurrences++;
                result.put(originalValue, occurrences);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Frequency partial order";
    }

}
