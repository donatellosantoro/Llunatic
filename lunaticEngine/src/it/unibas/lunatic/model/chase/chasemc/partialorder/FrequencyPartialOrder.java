package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import speedy.model.database.IValue;
import java.util.HashMap;
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
        Map<IValue, Integer> occurrenceHistogram = buildOccurrenceHistogram(constantCells);
        if (logger.isDebugEnabled()) logger.debug("Histogram for cells " + constantCells + "\n" + occurrenceHistogram);
        IValue mostFrequentValue = ChaseUtility.findMostFrequentValueIfAny(occurrenceHistogram);
        if (mostFrequentValue != null) {
            if (logger.isDebugEnabled()) logger.debug("Most frequent value in cell group:" + cellGroup + "\n\t ->" + mostFrequentValue);
            return mostFrequentValue;
        }
        return lubValue;
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
