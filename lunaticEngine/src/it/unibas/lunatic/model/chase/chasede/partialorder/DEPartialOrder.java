package it.unibas.lunatic.model.chase.chasede.partialorder;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasede.operators.CorrectCellGroupIDDE;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.ICorrectCellGroupID;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.utility.SpeedyUtility;

public class DEPartialOrder implements IPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(DEPartialOrder.class);
    private ICorrectCellGroupID cellGroupIDFixer = new CorrectCellGroupIDDE();

    public CellGroup findLUB(List<CellGroup> cellGroups, Scenario scenario) throws PartialOrderException {
        if (logger.isDebugEnabled()) logger.debug("Finding lub of cell groups\n" + LunaticUtility.printCollection(cellGroups));
        CellGroup lubCellGroup = new CellGroup(LunaticConstants.NULL_IVALUE, true);
        for (CellGroup cellGroup : cellGroups) {
            CellGroupUtility.mergeCells(cellGroup.clone(), lubCellGroup);
        }
        if (logger.isDebugEnabled()) logger.debug("LubCellGroup before setting value: \n" + lubCellGroup.toStringWithAdditionalCells());
        setCellGroupValue(lubCellGroup, scenario);
        if (logger.isDebugEnabled()) logger.debug("LubCellGroup after setting value: \n" + lubCellGroup.toStringWithAdditionalCells());
        return lubCellGroup;
    }

    public void setCellGroupValue(CellGroup lubCellGroup, Scenario scenario) {
        IValue lubValue = findLubValue(lubCellGroup);
        if (logger.isDebugEnabled()) logger.debug("LubValue: " + lubValue);
        lubCellGroup.setValue(lubValue);
        if (lubValue instanceof NullValue) {
            cellGroupIDFixer.correctCellGroupId(lubCellGroup);
        }
        IValue finalCellGroupValue = lubCellGroup.getValue();
        for (Iterator<CellGroupCell> it = lubCellGroup.getOccurrences().iterator(); it.hasNext();) {
            CellGroupCell occurrence = it.next();
            if (occurrence.getValue() instanceof ConstantValue) {
                it.remove();
            } else {
                occurrence.setValue(finalCellGroupValue);
            }
        }
    }

    private IValue findLubValue(CellGroup lubCellGroup) {
        // Occurrences and nonAuthoritative Justifications
        Set<CellGroupCell> constantCells = extractConstantCells(lubCellGroup);
        if (!constantCells.isEmpty()) {
            checkThatCellsHaveEqualValue(constantCells);
            return constantCells.iterator().next().getValue();
        }
        // Null Value
        return new NullValue(SpeedyConstants.NULL_VALUE);
    }

    private Set<CellGroupCell> extractConstantCells(CellGroup cellGroup) {
        Set<CellGroupCell> result = new HashSet<CellGroupCell>();
        for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
            if (occurrence.getOriginalValue().getType().equals(PartialOrderConstants.CONST)) {
                result.add(occurrence);
            }
        }
        return result;
    }

    private void checkThatCellsHaveEqualValue(Set<CellGroupCell> constantCells) {
        IValue firstConstantValue = constantCells.iterator().next().getValue();
        for (CellGroupCell constantCell : constantCells) {
            if (constantCell.getValue().equals(firstConstantValue)) {
                continue;
            }
            throw new ChaseFailedException("Unable to equate different constants in DE. " + SpeedyUtility.printCollection(constantCells));
        }
    }

    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> nonAuthoritativeCells, CellGroup cellGroup, Scenario scenario) {
        throw new ChaseFailedException("Unable to generalize constant cells in DE");
    }

    @Override
    public String toString() {
        return "DEPartialOrder";
    }

}
