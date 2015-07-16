package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringComparator implements IValueComparator {

    private static Logger logger = LoggerFactory.getLogger(StringComparator.class);

    private String sort;

    public int compare(IValue value1, IValue value2) {
        if (logger.isDebugEnabled()) logger.debug("Comparing value " + value1 + " with " + value2 + " using " + toString());
        int order = value1.toString().compareTo(value2.toString());
        if ("DESC".equals(sort)) {
            order = -order;
        }
        return order;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return "StringComparator(" + getSort() + ")";
    }
}
