package it.unibas.lunatic.persistence.csv;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasemc.CellChange;
import it.unibas.lunatic.persistence.DAOUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyChangesToCSVInstance {

    private String separator = ",";
    private DAOUtility utility = new DAOUtility();

    public void applyChanges(List<CellChange> changes, String originalFile, String outputFile) {
        Map<Long, List<CellChange>> changesForOID = groupChangesByOID(changes);
        BufferedReader source = null;
        PrintWriter out = null;
        try {
            source = utility.getBufferedReader(originalFile);
            out = utility.getPrintWriter(outputFile);
            List<String> headers = readLine(source);
            writeLine(headers, out);
            List<String> line = null;
            long oid = 1;
            while ((line = readLine(source)) != null) {
                if (line == null) {
                    continue;
                }
                if (changesForOID.containsKey(oid)) {
                    line = correctTuple(line, headers, changesForOID.get(oid));
                }
                writeLine(line, out);
                oid++;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            try {
                if (source != null) source.close();
                if (out != null) out.close();
            } catch (IOException ex) {
            }
        }
    }

    private Map<Long, List<CellChange>> groupChangesByOID(List<CellChange> changes) {
        Map<Long, List<CellChange>> result = new HashMap<Long, List<CellChange>>();
        for (CellChange change : changes) {
            Long oid = change.getOid();
            List<CellChange> changesForOID = result.get(oid);
            if (changesForOID == null) {
                changesForOID = new ArrayList<CellChange>();
                result.put(oid, changesForOID);
            }
            changesForOID.add(change);
        }
        return result;
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

    private List<String> correctTuple(List<String> line, List<String> headers, List<CellChange> changesForOID) {
        List<String> result = new ArrayList<String>(line);
        for (CellChange cellChange : changesForOID) {
            String attribute = cellChange.getAttribute();
            int headerPosition = headers.indexOf(attribute);
            if (headerPosition == -1) {
                throw new DAOException("Table doesn't contains attribute for change " + cellChange + " - Headers: " + headers);
            }
            String oldValue = line.get(headerPosition);
            if (cellChange.getOriginalValue() != null && !cellChange.getOriginalValue().equals(oldValue)) {
                throw new DAOException("Wrong cell change: " + cellChange + " - Original tuple value: " + oldValue);
            }
            result.set(headerPosition, cellChange.getNewValue());
        }
        return result;
    }

    private void writeLine(List<String> line, PrintWriter out) {
        for (int i = 0; i < line.size(); i++) {
            String value = line.get(i);
            out.print(value);
            if (i != line.size() - 1) {
                out.print(separator);
            } else {
                out.print("\n");
            }
        }
    }
}
