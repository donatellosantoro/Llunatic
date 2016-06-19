package it.unibas.lunatic.gui.window.cellgroup.filteractions;

import it.unibas.lunatic.gui.node.cellgroup.filters.FilterUnchangedCellgroups;
import it.unibas.lunatic.gui.window.cellgroup.CellGroupExplorerTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_ShowUnchangedOnly=Show unchanged")
public final class ShowUnchangedOnly extends AbstractAction {

    private final CellGroupExplorerTopComponent cellgroupExplorer;
    private FilterUnchangedCellgroups filter = new FilterUnchangedCellgroups();

    @Override
    public void actionPerformed(ActionEvent e) {
        cellgroupExplorer.setCategoryFilter(filter);
        cellgroupExplorer.filter();
    }

    public ShowUnchangedOnly(CellGroupExplorerTopComponent cellgroupExplorer) {
        this.cellgroupExplorer = cellgroupExplorer;
//        putValue(NAME, Bundle.CTL_ShowChangedOnly());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ShowUnchangedOnly());
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/cg-cat-others.png"));
    }
}
