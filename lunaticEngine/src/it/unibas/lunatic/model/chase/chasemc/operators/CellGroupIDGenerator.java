package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;

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
        return new ConstantValue(valueString + SpeedyConstants.VALUE_LABEL + (counter++));
    }

    public static LLUNValue getNextLLUNID() {
        return new LLUNValue(SpeedyConstants.LLUN_PREFIX + (counter++));
    }

    public static CellGroupCell getNextInvalidCell() {
        return new CellGroupCell(new TupleOID(IntegerOIDGenerator.getNextOID()), LunaticConstants.INVALID_ATTRIBUTE_REF, LunaticConstants.BOTTOM_VALUE, LunaticConstants.BOTTOM_VALUE, LunaticConstants.TYPE_INVALID, true);
    }

    public static CellGroupCell getNextUserCell(IValue value) {
        return new CellGroupCell(new TupleOID(IntegerOIDGenerator.getNextOID()), LunaticConstants.USER_ATTRIBUTE_REF, value, value, LunaticConstants.TYPE_USER, true);
    }

    public static IValue getCellGroupValueFromGroupID(IValue cellGroupId) {
        IValue value = cellGroupId;
        if (cellGroupId instanceof ConstantValue) {
            if (cellGroupId.toString().contains(SpeedyConstants.VALUE_LABEL)) {
                value = new ConstantValue(cellGroupId.toString().substring(0, cellGroupId.toString().indexOf(SpeedyConstants.VALUE_LABEL)));
            }
        }
        return value;
    }
}
