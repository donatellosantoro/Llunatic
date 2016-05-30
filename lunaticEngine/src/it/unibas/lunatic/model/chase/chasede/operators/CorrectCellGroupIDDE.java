package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.CorrectCellGroupID;
import it.unibas.lunatic.model.chase.chasemc.operators.ICorrectCellGroupID;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;

public class CorrectCellGroupIDDE implements ICorrectCellGroupID {

    private static Logger logger = LoggerFactory.getLogger(CorrectCellGroupIDDE.class);

    @Override
    public void correctCellGroupId(CellGroup cellGroup) {
        if (logger.isDebugEnabled()) logger.debug("Correcting cell group id/value for cell group:\n" + cellGroup.toLongString());
        IValue mostFrequentCellGroupId = findMostFrequentCellGroupId(cellGroup);
        if (logger.isDebugEnabled()) logger.debug("Using existingClusterId " + mostFrequentCellGroupId);
        cellGroup.setId(mostFrequentCellGroupId);
        correctToSaveInCells(cellGroup.getOccurrences(), mostFrequentCellGroupId);
        if (logger.isDebugEnabled()) logger.debug("Result " + cellGroup.toLongString());
    }

    private IValue findMostFrequentCellGroupId(CellGroup cellGroup) {
        Map<IValue, Integer> variableOccurrenceHistogram = buildOccurrenceHistogramForCellGroupId(cellGroup);
        if (logger.isDebugEnabled()) logger.debug("Occurrence histogram: " + LunaticUtility.printMap(variableOccurrenceHistogram));
        IValue result = ChaseUtility.findFirstOrderedValue(variableOccurrenceHistogram);
        return result;
    }

    private Map<IValue, Integer> buildOccurrenceHistogramForCellGroupId(CellGroup cellGroup) {
        Map<IValue, Integer> result = new HashMap<IValue, Integer>();
        for (CellGroupCell cell : cellGroup.getOccurrences()) {
            IValue newValue = cell.getValue();
            Integer occurrences = result.get(newValue);
            if (occurrences == null) {
                result.put(newValue, 1);
            } else {
                occurrences++;
                result.put(newValue, occurrences);
            }
        }
        return result;
    }

    private void correctToSaveInCells(Set<CellGroupCell> cells, IValue mostFrequentCellGroupId) {
        for (CellGroupCell cell : cells) {
            correctToSaveInCell(cell, mostFrequentCellGroupId);
        }
    }

    private void correctToSaveInCell(CellGroupCell cell, IValue mostFrequentCellGroupId) {
        if (cell.getValue().equals(mostFrequentCellGroupId)) {
            cell.setToSave(false);
        } else {
            cell.setToSave(true);
            cell.setLastSavedCellGroupId(mostFrequentCellGroupId); // new cell group id will be saved
        }
    }
}
