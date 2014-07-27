package it.unibas.lunatic.gui.window.cellgroup.filteractions;

import it.unibas.lunatic.gui.node.cellgroup.filters.FilterChangedCellgroups;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupExplorerTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_ShowChangedOnly=Show changed")
public final class ShowChangedOnly extends AbstractAction {

    private final CellGroupExplorerTopComponent cellgroupExplorer;
    private FilterChangedCellgroups filter = new FilterChangedCellgroups();

    @Override
    public void actionPerformed(ActionEvent e) {
        cellgroupExplorer.setCategoryFilter(filter);
        cellgroupExplorer.filter();
    }

    public ShowChangedOnly(CellGroupExplorerTopComponent cellgroupExplorer) {
        this.cellgroupExplorer = cellgroupExplorer;
//        putValue(NAME, Bundle.CTL_ShowChangedOnly());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ShowChangedOnly());
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/cg-cat-new.png"));
    }
}
