/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.cellgroup.filteractions;

import it.unibas.lunatic.gui.node.cellgroup.filters.FilterLluns;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupExplorerTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_ShowLlunValueOnly=Llun value only")
public final class ShowLlunValueOnly extends AbstractAction {

    private final CellGroupExplorerTopComponent cellgroupExplorer;
    private FilterLluns filter = new FilterLluns();

    @Override
    public void actionPerformed(ActionEvent e) {
        cellgroupExplorer.setValueFilter(filter);
        cellgroupExplorer.filter();
    }

    public ShowLlunValueOnly(CellGroupExplorerTopComponent tc) {
        this.cellgroupExplorer = tc;
//        putValue(NAME, Bundle.CTL_ShowLlunValueOnly());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ShowLlunValueOnly());
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/cg-llun.png"));
    }
}
