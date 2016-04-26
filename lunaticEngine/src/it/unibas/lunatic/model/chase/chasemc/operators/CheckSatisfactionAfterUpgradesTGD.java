package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.TGDEquivalenceClassCells;
import speedy.model.database.IValue;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericCombinationsGenerator;
import speedy.utility.combinatorics.GenericListGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleOID;

public class CheckSatisfactionAfterUpgradesTGD {

    private static final Logger logger = LoggerFactory.getLogger(CheckSatisfactionAfterUpgradesTGD.class.getName());
    
    private GenericListGenerator<Map<TupleOID, TupleOID>> listGenerator = new GenericListGenerator<Map<TupleOID, TupleOID>>();

    public boolean isSatisfiedAfterUpgrades(TGDEquivalenceClassCells update, CellGroup canonicalCellGroup, Dependency tgd, Scenario scenario) {
        if (!DependencyUtility.hasSourceSymbols(tgd)) {
            return false;
        }
        CellGroup existingCellGroup = update.getCellGroup();
        Set<CellGroupCell> newCells = update.getNewCells();
        if (logger.isDebugEnabled()) logger.debug("Canonical cell group: " + canonicalCellGroup);
        if (logger.isDebugEnabled()) logger.debug("Canonical new cells: " + newCells);
        if (logger.isDebugEnabled()) logger.debug("Existing cell group: " + existingCellGroup);

        Map<String, Set<TupleOID>> oidMapForNewCells = generateOidMap(update.getNewCells());
        Map<String, Set<TupleOID>> oidMapForExisting = generateOidMap(existingCellGroup.getAllCells());
        for (Map<TupleOID, TupleOID> idMapping : generateIdMapping(oidMapForNewCells, oidMapForExisting)) {
            CellGroup canonicalWithMapping = generateCanonicalUpdateWithMapping(canonicalCellGroup, existingCellGroup, newCells, idMapping);
            if (canonicalWithMapping == null) {
                continue;
            }
            List<CellGroup> cellGroupsToCheck = Arrays.asList(new CellGroup[]{canonicalWithMapping, existingCellGroup});
            boolean lubIsIdempotent = CellGroupUtility.checkContainment(cellGroupsToCheck);
            if (lubIsIdempotent) {
                if (logger.isDebugEnabled()) logger.debug("Found idempotent cell groups with mapping:\n" + LunaticUtility.printMap(idMapping) + "\n" + canonicalWithMapping + "\n" + existingCellGroup);
                return true;
            }
        }
        return false;
    }

    private Map<String, Set<TupleOID>> generateOidMap(Set<CellGroupCell> cells) {
        Map<String, Set<TupleOID>> result = new HashMap<String, Set<TupleOID>>();
        for (CellGroupCell cell : cells) {
            Set<TupleOID> oidsForTable = result.get(cell.getAttributeRef().getTableName());
            if (oidsForTable == null) {
                oidsForTable = new HashSet<TupleOID>();
                result.put(cell.getAttributeRef().getTableName(), oidsForTable);
            }
            oidsForTable.add(cell.getTupleOID());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Map<TupleOID, TupleOID>> generateIdMapping(Map<String, Set<TupleOID>> oidMapForNewCells, Map<String, Set<TupleOID>> oidMapForExisting) {
        List<MatchingOidsForTable> listOfMatchingOids = new ArrayList<MatchingOidsForTable>();
        for (String tableName : oidMapForNewCells.keySet()) { 
            Set<TupleOID> oidsInNewCells = oidMapForNewCells.get(tableName);
            Set<TupleOID> oidsInExisting = oidMapForExisting.get(tableName);
            if (oidsInExisting == null || oidsInExisting.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            listOfMatchingOids.add(new MatchingOidsForTable(tableName, oidsInNewCells, oidsInExisting));
        }
        List<List<Map<TupleOID, TupleOID>>> listOfLists = new ArrayList<List<Map<TupleOID, TupleOID>>>();
        for (MatchingOidsForTable machingOid : listOfMatchingOids) {
            listOfLists.add(machingOid.generateAllIdMappings());            
        }
        List<List<Map<TupleOID, TupleOID>>> combinedLists = listGenerator.generateListsOfElements(listOfLists);
        List<Map<TupleOID, TupleOID>> result = new ArrayList<Map<TupleOID, TupleOID>>();
        for (List<Map<TupleOID, TupleOID>> combinedList : combinedLists) {
            Map<TupleOID, TupleOID> map = new HashMap<TupleOID, TupleOID>();
            for (Map<TupleOID, TupleOID> mapInList : combinedList) {
                map.putAll(mapInList);
            }
            result.add(map);
        }
        return result;
    }

    private CellGroup generateCanonicalUpdateWithMapping(CellGroup canonicalCellGroup, CellGroup existingCellGroup, Set<CellGroupCell> newCells, Map<TupleOID, TupleOID> idMapping) {
        CellGroup canonicalClone = canonicalCellGroup.clone();
        for (CellGroupCell newCell : newCells) {
            CellGroupCell exsistingCell = findCorrespondingCell(newCell, existingCellGroup, idMapping);
            if (!valuesAreCompatible(newCell, exsistingCell)) {
                return null;
            }
            canonicalClone.addOccurrenceCell((CellGroupCell) exsistingCell.clone());
        }
        return canonicalClone;
    }

    private CellGroupCell findCorrespondingCell(CellGroupCell newCell, CellGroup existingCellGroup, Map<TupleOID, TupleOID> idMapping) {
        TupleOID mappedOID = idMapping.get(newCell.getTupleOID());
        for (CellGroupCell cell : existingCellGroup.getOccurrences()) {
            if (cell.getTupleOID().equals(mappedOID)
                    && cell.getAttributeRef().equals(newCell.getAttributeRef())) {
                return cell;
            }
        }
        throw new IllegalArgumentException("Unable to find matching cell for " + newCell + " in cell group\n" + existingCellGroup);
    }

    private boolean valuesAreCompatible(CellGroupCell newCell, CellGroupCell existingCell) {
        IValue existingValue = existingCell.getValue();
        IValue newValue = newCell.getValue();
        if (newValue instanceof NullValue) {
            return true;
        }
        if (newValue instanceof LLUNValue) {
            return (existingValue instanceof LLUNValue);
        }
        return (existingValue instanceof LLUNValue || existingValue.equals(newValue));

    }
}

class MatchingOidsForTable {

    String tableName;
    List<TupleOID> canonicalOids;
    List<TupleOID> existingOids;

    public MatchingOidsForTable(String tableName, Set<TupleOID> canonicalOids, Set<TupleOID> existingOids) {
        this.tableName = tableName;
        this.canonicalOids = new ArrayList<TupleOID>(canonicalOids);
        this.existingOids = new ArrayList<TupleOID>(existingOids);
    }

    @SuppressWarnings("unchecked")
    List<Map<TupleOID, TupleOID>> generateAllIdMappings() {
        List<Map<TupleOID, TupleOID>> result = new ArrayList<Map<TupleOID, TupleOID>>();
        int size = canonicalOids.size();
        GenericCombinationsGenerator combinationGenerator = new GenericCombinationsGenerator(existingOids, size);
        while (combinationGenerator.hasMoreElements()) {
            List<TupleOID> combination = combinationGenerator.nextElement();
            Map<TupleOID, TupleOID> mapping = new HashMap<TupleOID, TupleOID>();
            for (int i = 0; i < combination.size(); i++) {
                mapping.put(canonicalOids.get(i), combination.get(i));                
            }
            result.add(mapping);
        }
        return result;
    }

    @Override
    public String toString() {
        return "MatchingOidsForTable{" + "tableName=" + tableName + ", canonicalOids=" + canonicalOids + ", existingOids=" + existingOids + '}';
    }

}

