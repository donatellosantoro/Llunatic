/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.cellgroup.filteractions;

import it.unibas.lunatic.gui.node.cellgroup.filters.DefaultFilter;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupExplorerTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_ShowAllCategories=Show all")
public final class ShowAllCategories extends AbstractAction {

    private final CellGroupExplorerTopComponent cellgroupExplorer;
    private DefaultFilter filter = new DefaultFilter();

    @Override
    public void actionPerformed(ActionEvent e) {
        cellgroupExplorer.setCategoryFilter(filter);
        cellgroupExplorer.filter();
    }

    public ShowAllCategories(CellGroupExplorerTopComponent cellgroupExplorer) {
        this.cellgroupExplorer = cellgroupExplorer;
//        putValue(NAME, Bundle.CTL_ShowAllCategories());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ShowAllCategories());
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/cg-cat-all.png"));
    }
}
