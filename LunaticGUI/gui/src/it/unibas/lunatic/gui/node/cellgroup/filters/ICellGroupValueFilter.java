/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup.filters;

import it.unibas.lunatic.model.chasemc.CellGroup;

/**
 *
 * @author Antonio Galotta
 */
public interface ICellGroupValueFilter {

    public boolean accept(CellGroup cg);
    
}
