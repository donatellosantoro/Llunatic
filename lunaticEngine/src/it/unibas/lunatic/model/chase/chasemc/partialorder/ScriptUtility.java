package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptUtility {

    private static Logger logger = LoggerFactory.getLogger(ScriptUtility.class);

    public static Integer findPreferredValue(CellGroup cellGroup1, CellGroup cellGroup2, AttributeRef attribute, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Find preferred value:");
        if (logger.isDebugEnabled()) logger.debug("\tCellGroup1: " + cellGroup1);
        if (logger.isDebugEnabled()) logger.debug("\tCellGroup2: " + cellGroup2);
        if (logger.isDebugEnabled()) logger.debug("\tAttribute: " + attribute);
        Set<Cell> additionalCells1 = cellGroup1.getAdditionalCells().get(attribute);
        Set<Cell> additionalCells2 = cellGroup2.getAdditionalCells().get(attribute);
        if (additionalCells1 == null || additionalCells2 == null) {
            throw new ChaseException("In order to compare groups " + cellGroup1.getValue() + " and " + cellGroup2.getValue() + " we need as additional cells " + attribute);
        }
        Set<Cell> additionalCell = new HashSet<Cell>();
        additionalCell.addAll(additionalCells1);
        additionalCell.addAll(additionalCells2);
        List<CellGroup> additionalCellGroups = createCellGroups(additionalCell);
        CellGroup llub = findLLUB(additionalCellGroups, scenario);
        CellGroup dominatingCellGroup = searchDominatingCellGroup(additionalCellGroups, llub);
        if (dominatingCellGroup == null) {
            if (logger.isDebugEnabled()) logger.debug("NO_ORDER");
            return PartialOrderConstants.NO_ORDER;
        }
        boolean isDominating1 = cellsAppearInDominating(additionalCells1, dominatingCellGroup);
        boolean isDominating2 = cellsAppearInDominating(additionalCells2, dominatingCellGroup);
        if (isDominating1 && isDominating2) {
            if (logger.isDebugEnabled()) logger.debug("EQUALS");
            return PartialOrderConstants.EQUALS;
        }
        if (isDominating1) {
            if (logger.isDebugEnabled()) logger.debug("FOLLOWS");
            return PartialOrderConstants.FOLLOWS;
        }
        if (logger.isDebugEnabled()) logger.debug("PRECEDES");
        return PartialOrderConstants.PRECEDES;
    }

    private static CellGroup findLLUB(List<CellGroup> cellGroups, Scenario scenario) {
        ScriptPartialOrder scriptPO = scenario.getScriptPartialOrder();
        if (scriptPO != null && scriptPO.canHandleAttributes(LunaticUtility.extractAttributesInCellGroups(cellGroups))) {
            CellGroup scriptResult = scriptPO.findLUB(cellGroups, scenario);
            if (scriptResult.getValue() instanceof ConstantValue) {
                return scriptResult;
            }
        }
        return scenario.getPartialOrder().findLUB(cellGroups, scenario);
    }

    private static CellGroup searchDominatingCellGroup(List<CellGroup> additionalCellGroups, CellGroup llub) {
        for (CellGroup cellGroup : additionalCellGroups) {
            if (cellGroup.getValue().equals(llub.getValue())) {
                return cellGroup;
            }
        }
        return null;
    }

    private static boolean cellsAppearInDominating(Set<Cell> additionalCells, CellGroup dominatingCellGroup) {
        for (Cell cell : additionalCells) {
            if (dominatingCellGroup.getOccurrences().contains(new CellRef(cell))) {
                return true;
            }
        }
        return false;
    }

    private static List<CellGroup> createCellGroups(Set<Cell> cells) {
        if (logger.isDebugEnabled()) logger.debug("Creating cell groups for cells: " + cells);
        Map<IValue, Set<Cell>> partition = new HashMap<IValue, Set<Cell>>();
        for (Cell cell : cells) {
            Set<Cell> valueCells = partition.get(cell.getValue());
            if (valueCells == null) {
                valueCells = new HashSet<Cell>();
                partition.put(cell.getValue(), valueCells);
            }
            valueCells.add(cell);
        }
        List<CellGroup> result = new ArrayList<CellGroup>();
        for (IValue value : partition.keySet()) {
            CellGroup cellGroup = new CellGroup(value, true);
            cellGroup.setOccurrences(createCellRefsFromCells(partition.get(value)));
            result.add(cellGroup);
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting group: " + LunaticUtility.printCollection(result));
        return result;
    }

    public static Set<CellRef> createCellRefsFromCells(Collection<Cell> cells) {
        Set<CellRef> result = new HashSet<CellRef>();
        for (Cell cell : cells) {
            result.add(new CellRef(cell));
        }
        return result;
    }

    public static boolean containAttributeInOccurrences(CellGroup cellGroup, AttributeRef attributeRef) {
        for (CellRef cellRef : cellGroup.getOccurrences()) {
            if (cellRef.getAttributeRef().equals(attributeRef)) {
                return true;
            }
        }
        return false;
    }
}
