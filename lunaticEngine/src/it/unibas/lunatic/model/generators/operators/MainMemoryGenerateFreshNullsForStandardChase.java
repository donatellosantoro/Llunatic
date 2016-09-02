package it.unibas.lunatic.model.generators.operators;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.utility.SpeedyUtility;

public class MainMemoryGenerateFreshNullsForStandardChase {

    private final static Logger logger = LoggerFactory.getLogger(MainMemoryGenerateFreshNullsForStandardChase.class);
    private static long counter = 0L;
    private Tuple currentTuple;
    private Map<String, IValue> valueCache = new HashMap<String, IValue>();

    public IValue generateValue(Tuple sourceTuple, String variableInDependency, String type) {
        if (logger.isDebugEnabled()) logger.debug("Generating value for variable " + variableInDependency + " and tuple " + sourceTuple);
        if (currentTuple != null && !currentTuple.equals(sourceTuple)) {
            if (logger.isDebugEnabled()) logger.debug("Resetting cache...");
            valueCache.clear();
        }
        currentTuple = sourceTuple;
        IValue cachedValue = valueCache.get(variableInDependency);
        if (cachedValue != null) {
            return cachedValue;
        }
        IValue newValue = new NullValue(SpeedyConstants.SKOLEM_PREFIX + "-" + counter++);
        if (SpeedyUtility.isBigInt(type)) {
            newValue = new NullValue(SpeedyConstants.BIGINT_SKOLEM_PREFIX + counter);
        }
        if (SpeedyUtility.isDoublePrecision(type)) {
            newValue = new NullValue(SpeedyConstants.BIGINT_SKOLEM_PREFIX + counter); //Automatic conversion of bigint to doubleprecision
        }
        valueCache.put(variableInDependency, newValue);
        return newValue;
    }

}
