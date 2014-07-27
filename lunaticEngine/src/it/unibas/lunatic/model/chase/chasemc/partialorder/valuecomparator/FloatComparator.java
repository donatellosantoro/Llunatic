package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.exceptions.ChaseException;

public class FloatComparator extends StandardValueComparator {

    @Override
    public Integer compare(String v1, String v2) {
        try {
            Float f1 = Float.parseFloat(v1.toString());
            Float f2 = Float.parseFloat(v2.toString());
            return f1.compareTo(f2);
        } catch (NumberFormatException ex) {
            throw new ChaseException("Unable to parse float value " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return "FloatComparator(" + getSort() + ")";
    }
}
