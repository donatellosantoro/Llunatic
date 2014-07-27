package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardPartialOrder extends AbstractPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(StandardPartialOrder.class);

    public CellGroup mergeCellGroups(CellGroup group1, CellGroup group2, IValue newValue, Scenario scenario) {
        CellGroup newGroup = new CellGroup(newValue, true);
        mergeCells(group1, group2, newGroup);
        if (newValue instanceof LLUNValue) {
//            int llunId = ChaseUtility.generateLLUNId(newGroup);
//            newValue = new LLUNValue(LunaticConstants.LLUN_PREFIX + LunaticConstants.CHASE_FORWARD + llunId);
//            newGroup.setValue(newValue);
            newGroup.setValue(CellGroupIDGenerator.getNextLLUNID());
        }
        return newGroup;
    }

    public String toString() {
        return "Standard";
    }
}
