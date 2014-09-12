package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DEPartialOrder extends AbstractPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(DEPartialOrder.class);
    

    public CellGroup mergeCellGroups(CellGroup group1, CellGroup group2, IValue newValue, Scenario scenario) {
        CellGroup newGroup = new CellGroup(newValue, true);
        mergeCells(group1, group2, newGroup);
        if (newValue instanceof LLUNValue) {
            throw new ChaseFailedException("Unable to equate cell groups " + group1 + " and " + group2);
        }
        return newGroup;
    }

    @Override
    public String toString() {
        return "Standard";
    }
}
