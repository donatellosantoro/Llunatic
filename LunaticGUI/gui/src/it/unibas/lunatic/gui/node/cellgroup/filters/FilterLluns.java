/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.model.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.LLUNValue;

/**
 *
 * @author Antonio Galotta
 */
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
