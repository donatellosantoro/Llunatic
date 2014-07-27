package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardValueComparator implements IValueComparator {

    private static Logger logger = LoggerFactory.getLogger(StandardValueComparator.class);

    private String sort;

    public Integer compare(IValue value1, IValue value2) {
        if (logger.isDebugEnabled()) logger.debug("Comparing value " + value1 + " with " + value2 + " using " + toString());
        if (isNull(value1) && isNull(value2)) {
            return PartialOrderConstants.NO_ORDER;
        }
        if (isNull(value1)) {
            return PartialOrderConstants.PRECEDES;
        }
        if (isNull(value2)) {
            return PartialOrderConstants.FOLLOWS;
        }
        if (value1.equals(value2)) {
            return PartialOrderConstants.EQUALS;
        }
        Integer order = compare(value1.toString(), value2.toString());
        if (order == PartialOrderConstants.NO_ORDER) {
            return PartialOrderConstants.NO_ORDER;
        }
        if ("DESC".equals(sort)) {
            order = -order;
        }
        if (order < 0) {
            return PartialOrderConstants.FOLLOWS;
        } else if (order > 0) {
            return PartialOrderConstants.PRECEDES;
        } else {
            return PartialOrderConstants.EQUALS;
        }
    }

    public Integer compare(String v1, String v2) {
        return PartialOrderConstants.NO_ORDER;
    }

    private boolean isNull(IValue value) {
        if (value == null) {
            return true;
        }
        if (value.getType().equals(LunaticConstants.NULL)) {
            return true;
        }
        if (value.toString().equals("NULL")) {
            return true;
        }
        return false;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }
}
