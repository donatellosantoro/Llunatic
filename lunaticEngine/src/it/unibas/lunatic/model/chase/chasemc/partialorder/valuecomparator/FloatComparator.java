package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.exceptions.ChaseException;
import speedy.model.database.IValue;

public class FloatComparator extends StringComparatorForIValues {

    @Override
    public int compare(IValue v1, IValue v2) {
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
