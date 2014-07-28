package it.unibas.lunatic.gui.window.db;

public interface PagedTableView {

    void updatePage(int offset);

    int getOffset();

    int getPageSize();

    int getTableSize();
}
