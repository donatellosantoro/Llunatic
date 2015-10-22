package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import speedy.model.database.IValue;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.SpeedyUtility;

public class CheckSatisfactionAfterUpgradesEGD {

    private static final Logger logger = LoggerFactory.getLogger(CheckSatisfactionAfterUpgradesEGD.class.getName());

    public boolean isSatisfiedAfterUpgrades(List<CellGroup> cellGroups) {
        if (logger.isDebugEnabled()) logger.debug("Checking satisfaction after upgrades in cellgroups\n" + SpeedyUtility.printCollection(cellGroups, "\t"));
        Set<IValue> differentValues = CellGroupUtility.findDifferentValuesInCellGroupsWithOccurrences(cellGroups);
        if (logger.isDebugEnabled()) logger.debug("Different values: " + differentValues);
        if (differentValues.size() > 1) {
            return false;
        }
        return CellGroupUtility.checkContainment(cellGroups);
    }

}
