package it.unibas.lunatic.persistence.csv;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasemc.CellChange;
import it.unibas.lunatic.persistence.DAOUtility;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.comparator.StringComparator;

public class CompareCSVInstances {

    private final static Logger logger = LoggerFactory.getLogger(CompareCSVInstances.class);
    private String separator = ",";
    private DAOUtility utility = new DAOUtility();

    public List<CellChange> compare(String sourceFile, String destFile) {
        return compare(sourceFile, destFile, true, false);
    }

    public List<CellChange> compare(String sourceFile, String destFile, boolean useSecondHeader) {
        return compare(sourceFile, destFile, true, useSecondHeader);
    }

    public List<CellChange> compare(String sourceFile, String destFile, boolean destHasHeader, boolean useSecondHeader) {
        assert (sourceFile != null && destFile != null);
        BufferedReader source = null;
        BufferedReader dest = null;
        List<CellChange> result = new ArrayList<CellChange>();
        try {
            source = utility.getBufferedReader(sourceFile);
            dest = utility.getBufferedReader(destFile);
            List<String> srcHeader = readLine(source);
            List<String> destHeader = null;
            if (destHasHeader) {
                destHeader = readLine(dest);
//                if (!equals(srcHeader, destHeader)) {
                if (srcHeader.size() != destHeader.size()) {
                    throw new DAOException("Source and Target headers must be equal.\n\tSource header: " + srcHeader + "\n\tTarget header: " + destHeader);
                }
            }
            List<String> header = srcHeader;
            if (destHasHeader && useSecondHeader) {
                header = destHeader;
            }
            compareReaders(header, source, dest, result);
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            try {
                if (source != null) source.close();
                if (dest != null) dest.close();
            } catch (IOException ex) {
            }
        }
        return result;
    }

    public void export(List<CellChange> changes, String exportFile) {
        Collections.sort(changes, new StringComparator());
        BufferedWriter result = null;
        try {
            result = new BufferedWriter(new FileWriter(exportFile));
            for (CellChange change : changes) {
                result.append(change.toString()).append("\n");
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            try {
                if (result != null) result.close();
            } catch (IOException ex) {
            }
        }
    }

    private void compareReaders(List<String> header, BufferedReader source, BufferedReader dest, List<CellChange> result) throws IOException {
        int row = 0;
        boolean areEquals = true;
        while (true) {
            row++;
            List<String> srcRow = readLine(source);
            List<String> destRow = readLine(dest);
            if (srcRow == null && destRow == null) {
                if (areEquals) {
                    logger.warn("No differences");
                }
                return;
            }
            if (srcRow == null || destRow == null) {
                throw new DAOException("Source and Target files must have the same number of rows");
            }
            if (srcRow.size() != header.size() || destRow.size() != header.size()) {
                throw new DAOException("Source and Target files must have the same number of columns\n\tSource: " + srcRow + "\n\tTarget: " + destRow);
            }
            for (int col = 0; col < header.size(); col++) {
                String attribute = header.get(col);
                String srcValue = srcRow.get(col);
                String destValue = destRow.get(col);
                if (srcValue.equals(destValue)) {
                    continue;
                }
                areEquals = false;
                CellChange cellChange = new CellChange();
                cellChange.setOid(row);
                cellChange.setAttribute(attribute);
                cellChange.setOriginalValue(srcValue);
                cellChange.setNewValue(destValue);
                result.add(cellChange);
            }
        }
    }

    private List<String> readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        line += " "; //To handle row that finish with separator
        List<String> result = new ArrayList<String>();
        for (String token : line.split(separator)) {
            result.add(token.trim());
        }
        return result;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

}
