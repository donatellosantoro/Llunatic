/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.db.actions;

import it.unibas.lunatic.gui.window.db.TablePaginationSupport;
import it.unibas.lunatic.gui.window.db.PagedTableView;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SMALL_ICON;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Antonio Galotta
 */
public class ActionFirstPage extends AbstractAction {

    private final TablePaginationSupport offsetCalculator;
    private final PagedTableView pagedTableView;

    public ActionFirstPage(PagedTableView pagedTableView, TablePaginationSupport tablePaginationSupport) {
        this.offsetCalculator = tablePaginationSupport;
        this.pagedTableView = pagedTableView;
        putValue(SMALL_ICON, ImageUtilities.loadImage("it/unibas/lunatic/icons/navigate_beginning.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int newOffset = offsetCalculator.getFirstPageOffset();
        if (newOffset != pagedTableView.getOffset()) {
            pagedTableView.updatePage(newOffset);
        }
    }
}
