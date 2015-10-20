package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import java.util.Comparator;
import speedy.model.database.IValue;

public interface IValueComparator extends Comparator<IValue>{

    public int compare(IValue value1, IValue value2);

    public void setSort(String sort);
    public String getSort();
    
}
