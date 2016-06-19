package it.unibas.lunatic.gui.window.db.actions;

import it.unibas.lunatic.gui.window.db.PagedTableView;
import it.unibas.lunatic.gui.window.db.TablePaginationSupport;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;

public class ActionNextPage extends AbstractAction {

    private final TablePaginationSupport tablePaginationSupport;
    private final PagedTableView pagedTableView;

    public ActionNextPage(PagedTableView pagedTableView, TablePaginationSupport tablePaginationSupport) {
        this.tablePaginationSupport = tablePaginationSupport;
        this.pagedTableView = pagedTableView;
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/navigate_right.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int offset = pagedTableView.getOffset();
        int pageSize = pagedTableView.getPageSize();
        int tableSize = pagedTableView.getTableSize();
        int newOffset = tablePaginationSupport.getNextPageOffset(offset, pageSize, tableSize);
        if (offset != newOffset) {
            pagedTableView.updatePage(newOffset);
        }
    }
}
