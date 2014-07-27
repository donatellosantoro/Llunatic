package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellGroupIDGenerator {

    private static Logger logger = LoggerFactory.getLogger(CellGroupIDGenerator.class);

    private static Long counter = 0L;

    public static void resetCounter() {
        counter = 0L;
    }
        
    public static IValue generateNewId(IValue value) {
        if (value instanceof NullValue || value instanceof LLUNValue) {
            return value;
        }
        String valueString = value.toString();
        return new ConstantValue(valueString + LunaticConstants.VALUE_LABEL + (counter++));
    }

    public static LLUNValue getNextLLUNID() {
        return new LLUNValue(LunaticConstants.LLUN_PREFIX + (counter++));
    }

    public static IValue getValue(IValue cellGroupId) {
        IValue value = cellGroupId;
        if (cellGroupId instanceof ConstantValue) {
            if (cellGroupId.toString().contains(LunaticConstants.VALUE_LABEL)) {
                value = new ConstantValue(cellGroupId.toString().substring(0, cellGroupId.toString().indexOf(LunaticConstants.VALUE_LABEL)));
            }
        }
        return value;
    }
}