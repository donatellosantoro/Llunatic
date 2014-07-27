/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.db;

/**
 *
 * @author Antonio Galotta
 */
public interface PagedTableView {

    void updatePage(int offset);

    int getOffset();

    int getPageSize();

    int getTableSize();
}
