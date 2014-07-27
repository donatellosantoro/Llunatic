package it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator;

import it.unibas.lunatic.model.database.IValue;

public interface IValueComparator {

    public Integer compare(IValue value1, IValue value2);

    public void setSort(String sort);
    public String getSort();
}
