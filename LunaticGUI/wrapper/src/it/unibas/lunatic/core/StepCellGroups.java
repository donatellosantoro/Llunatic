/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chasemc.CellGroup;
import java.util.List;

/**
 *
 * @author Antonio Galotta
 */
public class StepCellGroups {

    private final List<CellGroup> cellGroups;
    private final List<CellGroup> changedCellGroups;

    public StepCellGroups(List<CellGroup> cellGroups, List<CellGroup> changedCellGroups) {
        this.cellGroups = cellGroups;
        this.changedCellGroups = changedCellGroups;
    }

    public List<CellGroup> getAll() {
        return cellGroups;
    }

    public List<CellGroup> getChangedCellGroups() {
        return changedCellGroups;
    }
}
