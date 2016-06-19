package it.unibas.lunatic.gui.window.db.actions;

import it.unibas.lunatic.gui.window.db.DbPagedTableTopComponent;
import it.unibas.lunatic.gui.window.db.PagedTableView;
import it.unibas.lunatic.gui.window.db.TablePaginationSupport;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;

public class ActionPreviousPage extends AbstractAction {

    private final TablePaginationSupport tablePaginationSupport;
    private final PagedTableView pagedTableView;

    public ActionPreviousPage(PagedTableView pagedTableView, TablePaginationSupport tablePaginationSupport) {
        this.tablePaginationSupport = tablePaginationSupport;
        this.pagedTableView = pagedTableView;
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/navigate_left.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int offset = pagedTableView.getOffset();
        int pageSize = pagedTableView.getPageSize();
        int tableSize = pagedTableView.getTableSize();
        int newOffset = tablePaginationSupport.getPreviousPageOffset(offset, pageSize, tableSize);
        if (offset != newOffset) {
            pagedTableView.updatePage(newOffset);
        }
    }
}
