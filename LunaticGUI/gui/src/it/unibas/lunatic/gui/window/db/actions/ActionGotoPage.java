package it.unibas.lunatic.gui.window.db.actions;

import it.unibas.lunatic.gui.window.db.DbPagedTableTopComponent;
import it.unibas.lunatic.gui.window.db.TablePaginationSupport;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ActionGotoPage extends AbstractAction {

    private final DbPagedTableTopComponent pagedTableView;
    private final TablePaginationSupport tablePaginationSupport;

    public ActionGotoPage(DbPagedTableTopComponent view, TablePaginationSupport tablePaginationSupport) {
        this.pagedTableView = view;
        this.tablePaginationSupport = tablePaginationSupport;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        int offset = pagedTableView.getOffset();
        int pageSize = pagedTableView.getPageSize();
        int tableSize = pagedTableView.getTableSize();
        String newPageText = pagedTableView.getPageText();
        try {
            int newPage = Integer.parseInt(newPageText);
            int newOffset = tablePaginationSupport.getPageOffset(offset, pageSize, tableSize, newPage);
            if (offset != newOffset) {
                pagedTableView.updatePage(newOffset);
            }
        } catch (Exception e) {
        }
        pagedTableView.updatePageText();
    }
}
