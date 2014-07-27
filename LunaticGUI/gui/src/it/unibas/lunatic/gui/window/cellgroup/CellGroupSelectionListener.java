/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.cellgroup;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import java.util.Collection;

/**
 *
 * @author Antonio Galotta
 */
public class CellGroupSelectionListener extends AbstractSelectionListener<StepCellGroupNode> {

    private ExplorerTopComponent explorerTopComponent;

    @Override
    public void onChange(Collection<? extends StepCellGroupNode> selection) {
        StepCellGroupNode selected = getBean(selection);
        if (selected != null) {
            explorerTopComponent.setRootContext(selected);
        }
    }

    public void register(ExplorerTopComponent explorerTopComponent) {
        this.explorerTopComponent = explorerTopComponent;
        registerBean(StepCellGroupNode.class);
    }
}
