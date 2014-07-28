package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;

public interface ICellGroupValueFilter {

    public boolean accept(CellGroup cg);
    
}
