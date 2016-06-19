package it.unibas.lunatic.persistence.csv;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasemc.CellChange;
import it.unibas.lunatic.persistence.DAOUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import speedy.utility.comparator.StringComparator;

public class DAOCellChanges {

    private String separator = ",";
    private DAOUtility utility = new DAOUtility();

    public List<CellChange> loadChanges(String file) {
        assert (file != null);
        List<CellChange> result = new ArrayList<CellChange>();
        BufferedReader source = null;
        BufferedReader dest = null;
        try {
            source = utility.getBufferedReader(file);
            String line = null;
            while ((line = source.readLine()) != null) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                String[] tokens = line.split(separator);
                if (tokens.length != 3) {
                    continue;
                }
                String cellString = tokens[0];
                String[] cellTokens = cellString.split("\\.");
                if (cellTokens.length != 2) {
                    continue;
                }
                CellChange change = new CellChange();
                change.setOid(Long.parseLong(cellTokens[0]));
                change.setAttribute(cellTokens[1]);
                change.setOriginalValue(tokens[1]);
                change.setNewValue(tokens[2]);
                result.add(change);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            try {
                if (source != null) source.close();
                if (dest != null) dest.close();
            } catch (IOException ex) {
            }
        }
        Collections.sort(result, new StringComparator());
        return result;
    }

}
