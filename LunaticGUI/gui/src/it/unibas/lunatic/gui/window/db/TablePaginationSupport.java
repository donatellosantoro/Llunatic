package it.unibas.lunatic.gui.window.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Antonio Galotta
 */
public class TablePaginationSupport {

    private Log logger = LogFactory.getLog(getClass());

    public int getFirstPageOffset() {
        return 0;
    }

    public int getPreviousPageOffset(int offset, int pageSize, int tableSize) {
        int currentPageNumber = getCurrentPageNumber(offset, pageSize);
        return getPageOffset(offset, pageSize, tableSize, currentPageNumber - 1);
    }

    public int getNextPageOffset(int offset, int pageSize, int tableSize) {
        int currentPageNumber = getCurrentPageNumber(offset, pageSize);
        return getPageOffset(offset, pageSize, tableSize, currentPageNumber + 1);
    }

    public int getLastPageOffset(int offset, int pageSize, int tableSize) {
        int pages = countTotalPages(tableSize, pageSize);
        return getPageOffset(offset, pageSize, tableSize, pages + 1);
    }

    private int countTotalPages(int tableSize, int pageSize) {
        if (logger.isDebugEnabled()) logger.debug("Count total pages: tableSize=" + tableSize + ", pageSize=" + pageSize);
        int totalPages = tableSize / pageSize;
        if (logger.isDebugEnabled()) logger.debug("Result: pages=" + totalPages);
        return totalPages;
    }

    public int getPageOffset(int offset, int pageSize, int tableSize, int newPage) {
        if (logger.isDebugEnabled()) logger.debug("Calculate page " + newPage + " offset: currentOffset=" + offset + ", pageSize=" + pageSize + ", tableSize=" + tableSize + "");
        newPage--;
        int newOffset = 0;
        if (newPage > 0) {
            int totalPages = countTotalPages(tableSize, pageSize);
            if (newPage > totalPages) {
                newPage = totalPages;
            }
            if (newPage > 0 && (newPage * pageSize) == tableSize) {
                newPage--;
            }
            newOffset = newPage * pageSize;
        }
        if (logger.isDebugEnabled()) logger.debug("Result: offset=" + newOffset);
        return newOffset;
    }

    public int getCurrentPageNumber(int offset, int pageSize) {
        if (logger.isDebugEnabled()) logger.debug("Calculate page number: currentOffset=" + offset + ", pageSize=" + pageSize);
        int pageNumber = (offset / pageSize) + 1;
        if (logger.isDebugEnabled()) logger.debug("Result: pageNumber=" + pageNumber);
        return pageNumber;
    }
}
