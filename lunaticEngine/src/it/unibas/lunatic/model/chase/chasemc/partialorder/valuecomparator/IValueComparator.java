package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.model.database.IValue;
import java.util.Comparator;

public interface IValueComparator extends Comparator<IValue>{

    public int compare(IValue value1, IValue value2);

    public void setSort(String sort);
    public String getSort();
    
}
