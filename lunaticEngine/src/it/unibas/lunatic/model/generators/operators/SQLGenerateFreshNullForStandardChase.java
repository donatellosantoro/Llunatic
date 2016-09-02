package it.unibas.lunatic.model.generators.operators;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.utility.SpeedyUtility;

public class SQLGenerateFreshNullForStandardChase {

    private final static Logger logger = LoggerFactory.getLogger(SQLGenerateFreshNullForStandardChase.class);
    private static long counter = 0L;
    private Map<String, String> valueCache = new HashMap<String, String>();

    public String generateSQL(String variableInDependency, String type) {
        if (logger.isDebugEnabled()) logger.debug("Generating value for variable " + variableInDependency);
        String cachedValue = valueCache.get(variableInDependency);
        if (cachedValue != null) {
            return cachedValue;
        }
        String newValue = "'" + SpeedyConstants.SKOLEM_PREFIX + "-" + counter++ + "'";
        if (SpeedyUtility.isBigInt(type)) {
            newValue = SpeedyConstants.BIGINT_SKOLEM_PREFIX + counter;
        }
        if (SpeedyUtility.isDoublePrecision(type)) {
            newValue = SpeedyConstants.BIGINT_SKOLEM_PREFIX + counter; //Automatic conversion of bigint to doubleprecision
        }
        valueCache.put(variableInDependency, newValue);
        if (logger.isDebugEnabled()) logger.debug("-> " + newValue);
        return newValue;
    }

}
