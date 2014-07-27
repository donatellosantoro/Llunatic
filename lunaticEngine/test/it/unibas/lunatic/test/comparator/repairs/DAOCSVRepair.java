package it.unibas.lunatic.test.comparator.repairs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOCSVRepair {

    private static Logger logger = LoggerFactory.getLogger(DAOCSVRepair.class);

    private static String SEPARATOR = ",";
    private DAOUtility utility = new DAOUtility();

    public Collection<Repair> loadRepair(String fileName) throws DAOException {
        return loadRepairMap(fileName).values();
    }

    public Map<String, Repair> loadRepairMap(String fileName) throws DAOException {
        Map<String, Repair> result = new HashMap<String, Repair>();
        try {
            BufferedReader reader = utility.getBufferedReader(fileName);
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("+++++++++++++++") || line.trim().isEmpty()){
                    continue;
                }
                String[] tokens = line.split(SEPARATOR, -1);
                Repair repair = new Repair(tokens[0], tokens[1], tokens[2]);
                result.put(repair.getCellId(), repair);
            }
        } catch (IOException exception) {
            throw new DAOException("Unable to load file: " + fileName + "\n" + exception);
        }
        return result;
    }

    public List<Map<String, Repair>> loadMultipleRepair(String fileName) throws DAOException {
        List<Map<String, Repair>> result = new ArrayList<Map<String, Repair>>();
        try {
            BufferedReader reader = utility.getBufferedReader(fileName);
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Map<String, Repair> repairs = new HashMap<String, Repair>();
                while ((line = reader.readLine()) != null && !line.isEmpty()) {                    
                    String[] tokens = line.split(SEPARATOR, -1);
                    Repair repair = new Repair(tokens[0], tokens[1], tokens[2]);
                    repairs.put(repair.getCellId(), repair);
                }
                if (!repairs.isEmpty()) {
                    result.add(repairs);
                }
            }

        } catch (IOException exception) {
            throw new DAOException("Unable to load file: " + fileName + "\n" + exception);
        }
        return result;
    }
}
