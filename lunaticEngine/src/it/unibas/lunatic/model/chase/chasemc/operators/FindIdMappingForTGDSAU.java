package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToInsertForTGD;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindIdMappingForTGDSAU {

    private static final Logger logger = LoggerFactory.getLogger(FindIdMappingForTGDSAU.class.getName());

    public boolean satisfiedAfterRepairs(TargetCellsToInsertForTGD update, CellGroup canonicalCellGroup, Scenario scenario) {
        CellGroup existingCellGroup = update.getCellGroup();
        Set<CellGroupCell> newCells = update.getNewCells();
        if (logger.isDebugEnabled()) logger.debug("Canonical cell group: " + canonicalCellGroup);
        if (logger.isDebugEnabled()) logger.debug("Canonical new cells: " + newCells);
        if (logger.isDebugEnabled()) logger.debug("Existing cell group: " + existingCellGroup);

        Map<String, Set<TupleOID>> oidMapForNewCells = generateOidMap(update.getNewCells());
        Map<String, Set<TupleOID>> oidMapForExisting = generateOidMap(existingCellGroup.getAllCells());
        for (Map<TupleOID, TupleOID> idMapping : generateIdMapping(oidMapForNewCells, oidMapForExisting)) {
            CellGroup canonicalWithMapping = generateCanonicalUpdateWithMapping(canonicalCellGroup, newCells, idMapping);
            List<CellGroup> cellGroupsToCheck = Arrays.asList(new CellGroup[]{canonicalWithMapping, existingCellGroup});
            boolean lubIsIdempotent = scenario.getCostManager().checkContainment(cellGroupsToCheck);
            if (lubIsIdempotent) {
                if (logger.isDebugEnabled()) logger.debug("Found idempotent cell groups with mapping:\n" + LunaticUtility.printMap(idMapping) + "\n"+ canonicalWithMapping + "\n" + existingCellGroup);
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

    private Iterable<Map<TupleOID, TupleOID>> generateIdMapping(Map<String, Set<TupleOID>> oidMapForNewCells, Map<String, Set<TupleOID>> oidMapForExisting) {
        return Collections.EMPTY_LIST;
    }

    private CellGroup generateCanonicalUpdateWithMapping(CellGroup canonicalCellGroup, Set<CellGroupCell> newCells, Map<TupleOID, TupleOID> idMapping) {
        CellGroup canonicalClone = canonicalCellGroup.clone();
        for (CellGroupCell newCell : newCells) {
            CellGroupCell newCellClone = (CellGroupCell) newCell.clone();
            TupleOID newTupleId = idMapping.get(newCellClone.getTupleOID());
            newCellClone.setTupleOid(newTupleId);
            canonicalClone.addOccurrenceCell(newCellClone);
        }
        return canonicalClone;
    }

}
