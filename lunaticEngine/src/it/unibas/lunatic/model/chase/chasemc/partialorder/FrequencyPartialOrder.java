package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.chasemc.costmanager.SimilarityConfiguration;
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
import speedy.model.database.ConstantValue;
import speedy.model.database.LLUNValue;

public class FrequencyPartialOrder extends StandardPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(FrequencyPartialOrder.class);
    private static final String NO_MAJORITY = "NO_MAJORITY";

    @Override
    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> constantCells, CellGroup cellGroup, Scenario scenario) {
        IValue lubValue = super.generalizeNonAuthoritativeConstantCells(constantCells, cellGroup, scenario);
        if (!(lubValue instanceof LLUNValue)) {
            return lubValue;
        }
        CostManagerConfiguration costManagerConfiguration = scenario.getCostManagerConfiguration();
        SimilarityConfiguration similarityConfiguration = CostManagerUtility.findSimilarityConfigurationForCells(constantCells, scenario.getCostManagerConfiguration());
        Map<IValue, Set<CellGroupCell>> occurrenceHistogram = partitionCellsByValue(constantCells);
        if (logger.isDebugEnabled()) logger.debug("Histogram for cells\n" + occurrenceHistogram);
        List<Map.Entry<IValue, Set<CellGroupCell>>> sortedHistogram = ChaseUtility.sortEntriesWithSizes(occurrenceHistogram);
        IValue firstMaxValue = sortedHistogram.get(0).getKey();
        if (hasMajority(sortedHistogram, constantCells.size())) {
            return firstMaxValue;
        }
        Map<IValue, Integer> similarityHistogram = buildSimilarityHistogram(sortedHistogram, similarityConfiguration, costManagerConfiguration);
        if (logger.isDebugEnabled()) logger.debug("Similarity Histogram for cells\n" + similarityHistogram);
        IValue mostFrequentValue = findValueWithMostSimilarOccurrences(similarityHistogram, costManagerConfiguration.isRequestMajorityInSimilarityCostManager());
        if (mostFrequentValue != null && !mostFrequentValue.toString().startsWith(NO_MAJORITY)) {
            if (logger.isDebugEnabled()) logger.debug("Most frequent value in cell group:" + cellGroup + "\n\t ->" + mostFrequentValue);
            return mostFrequentValue;
        }
        return lubValue;
    }

    private boolean hasMajority(List<Map.Entry<IValue, Set<CellGroupCell>>> sortedHistogram, int size) {
        int occurrencesOfFirstMaxValue = sortedHistogram.get(0).getValue().size();
        return occurrencesOfFirstMaxValue > (size / 2);
    }

    private Map<IValue, Integer> buildSimilarityHistogram(List<Map.Entry<IValue, Set<CellGroupCell>>> sortedHistogram, SimilarityConfiguration similarityConfiguration, CostManagerConfiguration costManagerConfiguration) {
        List<IValue> candidateValues = findCandidateValues(sortedHistogram, costManagerConfiguration.getNumberOfCandidateValuesForSimilarity());
        Map<IValue, Set<CellGroupCell>> similarityMap = new HashMap<IValue, Set<CellGroupCell>>();
        for (IValue value : candidateValues) {
            Set<CellGroupCell> similarCells = findSimilarCells(value, sortedHistogram, similarityConfiguration);
            similarityMap.put(value, similarCells);
        }
        int counterNoMajority = 1;
        Map<IValue, Integer> result = new HashMap<IValue, Integer>();
        for (Set<CellGroupCell> similarCells : similarityMap.values()) {
            IValue mostFrequentValue = findFirstMaximallyFrequentValue(similarCells, costManagerConfiguration.isRequestMajorityInSimilarityCostManager());
            if (mostFrequentValue == null) {
                mostFrequentValue = new ConstantValue(counterNoMajority + (counterNoMajority++));
            }
            result.put(mostFrequentValue, similarCells.size());
        }
        return result;
    }

    private Set<CellGroupCell> findSimilarCells(IValue value, List<Map.Entry<IValue, Set<CellGroupCell>>> sortedHistogram, SimilarityConfiguration similarityConfiguration) {
        Set<CellGroupCell> similarCells = new HashSet<CellGroupCell>();
        for (int i = 0; i < sortedHistogram.size(); i++) {
            IValue valueInMap = sortedHistogram.get(i).getKey();
            if (CostManagerUtility.areSimilar(value, valueInMap, similarityConfiguration)) {
                similarCells.addAll(sortedHistogram.get(i).getValue());
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

    private IValue findFirstMaximallyFrequentValue(Set<CellGroupCell> nonAuthoritativeCells, boolean requestMajority) {
        Map<IValue, Integer> occurrenceHistogram = buildOccurrenceHistogram(nonAuthoritativeCells);
        List<Map.Entry<IValue, Integer>> entryList = ChaseUtility.sortEntriesWithValues(occurrenceHistogram);
        IValue firstMaxValue = entryList.get(0).getKey();
        if (!requestMajority) {
            return firstMaxValue;
        }
        Integer firstMax = entryList.get(0).getValue();
        Integer secondMax = null;
        if (entryList.size() > 1) {
            secondMax = entryList.get(1).getValue();
        }
        if (secondMax == null || firstMax > secondMax) {
            return firstMaxValue;
        }
        return null;
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

    private IValue findValueWithMostSimilarOccurrences(Map<IValue, Integer> occurrenceHistogram, boolean requestMajority) {
        List<Map.Entry<IValue, Integer>> entryList = ChaseUtility.sortEntriesWithValues(occurrenceHistogram);
        IValue firstMaxValue = entryList.get(0).getKey();
        if (!requestMajority) {
            return firstMaxValue;
        }
        Integer firstMax = entryList.get(0).getValue();
        Integer secondMax = null;
        if (entryList.size() > 1) {
            secondMax = entryList.get(1).getValue();
        }
        if (secondMax == null || firstMax > secondMax) {
            return firstMaxValue;
        }
        return null;
    }

    private Map<IValue, Set<CellGroupCell>> partitionCellsByValue(Set<CellGroupCell> nonAuthoritativeCells) {
        Map<IValue, Set<CellGroupCell>> result = new HashMap<IValue, Set<CellGroupCell>>();
        for (CellGroupCell cell : nonAuthoritativeCells) {
            IValue originalValue = cell.getOriginalValue();
            Set<CellGroupCell> cells = result.get(originalValue);
            if (cells == null) {
                cells = new HashSet<CellGroupCell>();
                result.put(originalValue, cells);
            }
            cells.add(cell);
        }
        return result;
    }

    private List<IValue> findCandidateValues(List<Map.Entry<IValue, Set<CellGroupCell>>> sortedHistogram, int numberOfCandidateValuesForSimilarity) {
        List<IValue> result = new ArrayList<IValue>();
        int size = Math.min(sortedHistogram.size(), numberOfCandidateValuesForSimilarity);
        for (int i = 0; i < size; i++) {
            IValue value = sortedHistogram.get(i).getKey();
            result.add(value);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Frequency partial order";
    }

}
