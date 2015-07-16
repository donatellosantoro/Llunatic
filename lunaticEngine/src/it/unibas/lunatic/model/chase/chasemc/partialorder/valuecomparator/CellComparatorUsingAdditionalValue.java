package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import java.util.Comparator;

public class CellComparatorUsingAdditionalValue implements Comparator<CellGroupCell> {
    
    private IValueComparator valueComparator;
    
    public CellComparatorUsingAdditionalValue(IValueComparator valueComparator) {
        this.valueComparator = valueComparator;
    }
    
    public int compare(CellGroupCell o1, CellGroupCell o2) {
        return valueComparator.compare(o1.getAdditionalValue(), o2.getAdditionalValue());
    }
    
}
