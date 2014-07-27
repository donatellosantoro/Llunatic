package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrequencyPartialOrder extends AbstractPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(FrequencyPartialOrder.class);

    public CellGroup mergeCellGroups(CellGroup group1, CellGroup group2, IValue newValue, Scenario scenario) {
        CellGroup newGroup = new CellGroup(newValue, true);
        mergeCells(group1, group2, newGroup);
        if (newValue instanceof LLUNValue) {
            IValue frequencyValue = checkFrequency(group1, group2);
            if (frequencyValue != null) {
                newGroup.setValue(frequencyValue);
            } else {
//                int llunId = ChaseUtility.generateLLUNId(newGroup);
//                newValue = new LLUNValue(LunaticConstants.LLUN_PREFIX + LunaticConstants.CHASE_FORWARD + llunId);
                newGroup.setValue(CellGroupIDGenerator.getNextLLUNID());
            }
        }
        return newGroup;
    }

    private IValue checkFrequency(CellGroup group1, CellGroup group2) {
        if(group1.getOccurrences().isEmpty() || group2.getOccurrences().isEmpty()){
            return null;
        }
//        if (group1.getOccurrences().size() >= group2.getOccurrences().size()) {
        if (group1.getOccurrences().size() > group2.getOccurrences().size()) {
            return group1.getValue();
        }
        if (group1.getOccurrences().size() < group2.getOccurrences().size()) {
            return group2.getValue();
        }
        return null;
    }

    public String toString() {
        return "Frequency partial order";
    }
}
