package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;

public class FilterLluns implements ICellGroupValueFilter {

    @Override
    public boolean accept(CellGroup cg) {
        IValue cgValue = cg.getValue();
        if (cgValue instanceof LLUNValue) {
            return true;
        }
        return false;
    }
}
