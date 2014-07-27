package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.IValueComparator;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPartialOrder implements IPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(AbstractPartialOrder.class);
//    public CellGroup findLUB(List<CellGroup> cellGroups, IDatabase deltaDB, String stepId) throws PartialOrderException {

    public CellGroup findLUB(List<CellGroup> cellGroups, Scenario scenario) throws PartialOrderException {
        CellGroup lub = cellGroups.get(0);
        for (int i = 1; i < cellGroups.size(); i++) {
            CellGroup group = cellGroups.get(i);
            lub = generalize(lub, group, scenario);
        }
        return lub;
    }

//    public CellGroup generalize(CellGroup group1, CellGroup group2, IDatabase deltaDB, String stepId) throws PartialOrderException {
    public CellGroup generalize(CellGroup group1, CellGroup group2, Scenario scenario) throws PartialOrderException {
        if (logger.isDebugEnabled()) logger.debug("########### Comparing groups:" + group1 + " - " + group2);
        String c1type = group1.getValue().getType();
        String c2type = group2.getValue().getType();
        if (logger.isDebugEnabled()) logger.debug("########### Types :" + c1type + " and " + c2type);
        //cells with nulls values are equivalent to each other
        if (c1type.equals(PartialOrderConstants.NULL) && c2type.equals(PartialOrderConstants.NULL)) {
            if (logger.isDebugEnabled()) logger.debug("########### Result : equals");
            return mergeCellGroups(group1, group2, group1.getValue(), scenario);
        }
        //cells with llun and constant values are always preferred to nulls
        if (!c1type.equals(PartialOrderConstants.NULL) && c2type.equals(PartialOrderConstants.NULL)) {
            if (logger.isDebugEnabled()) logger.debug("########### Result : follows");
            return mergeCellGroups(group1, group2, group1.getValue(), scenario);
        }
        if (c1type.equals(PartialOrderConstants.NULL) && !c2type.equals(PartialOrderConstants.NULL)) {
            if (logger.isDebugEnabled()) logger.debug("########### Result : precedes");
            return mergeCellGroups(group1, group2, group2.getValue(), scenario);
        }
        // constants from CONST
        if (c1type.equals(PartialOrderConstants.CONST) || c2type.equals(PartialOrderConstants.CONST)) {
            boolean includeSourceValue1 = !group1.getProvenances().isEmpty();
            boolean includeSourceValue2 = !group2.getProvenances().isEmpty();
            if (includeSourceValue1 && (!includeSourceValue2 || LunaticUtility.contained(group2.getProvenances(), group1.getProvenances()))) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : follows");
                return mergeCellGroups(group1, group2, group1.getValue(), scenario);
            }
            if (includeSourceValue2 && (!includeSourceValue1 || LunaticUtility.contained(group1.getProvenances(), group2.getProvenances()))) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : precedes");
                return mergeCellGroups(group1, group2, group2.getValue(), scenario);
            }
            if (includeSourceValue1 && includeSourceValue2 && !group1.getProvenances().equals(group2.getProvenances())) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : no order");
                return mergeCellGroups(group1, group2, CellGroupIDGenerator.getNextLLUNID(), scenario);
            }
        }
        //a llun is preferred to a const if it contains his cell
        Set<CellRef> cellRefs1 = group1.getOccurrences();
        Set<CellRef> cellRefs2 = group2.getOccurrences();
        Set<Cell> prov1 = group1.getProvenances();
        Set<Cell> prov2 = group2.getProvenances();
        if (c1type.equals(PartialOrderConstants.LLUN) && c2type.equals(PartialOrderConstants.CONST)) {
            if (LunaticUtility.contained(cellRefs2, cellRefs1) && LunaticUtility.contained(prov2, prov1)) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : follows");
                return mergeCellGroups(group1, group2, group1.getValue(), scenario);
            }
        }
        if (c1type.equals(PartialOrderConstants.CONST) && c2type.equals(PartialOrderConstants.LLUN)) {
            if (LunaticUtility.contained(cellRefs1, cellRefs2) && LunaticUtility.contained(prov1, prov2)) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : precedes");
                return mergeCellGroups(group1, group2, group2.getValue(), scenario);
            }
        }
        //a llun is preferred to another lluns if it contains all his cells
        if (c1type.equals(PartialOrderConstants.LLUN) && c2type.equals(PartialOrderConstants.LLUN)) {
//            if (logger.isDebugEnabled()) logger.debug("Database: \n" + deltaDB);
            if (LunaticUtility.contained(cellRefs2, cellRefs1) && LunaticUtility.contained(prov2, prov1)) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : follows");
                return mergeCellGroups(group1, group2, group1.getValue(), scenario);
            }
            if (LunaticUtility.contained(cellRefs1, cellRefs2) && LunaticUtility.contained(prov1, prov2)) {
                if (logger.isDebugEnabled()) logger.debug("########### Result : precedes");
                return mergeCellGroups(group1, group2, group2.getValue(), scenario);
            }
        }
        //ordering attribute
        OrderingAttribute orderingAttribute = extractOrderingAttribute(cellRefs1, cellRefs2, scenario);
        if (orderingAttribute != null) {
            return mergeGroupsUsingOrderingAttribute(group1, group2, orderingAttribute, scenario);
        }
        if (logger.isDebugEnabled()) logger.debug("########### Result : NO ORDER");
        return mergeCellGroups(group1, group2, CellGroupIDGenerator.getNextLLUNID(), scenario);
    }

    void mergeCells(CellGroup group1, CellGroup group2, CellGroup newGroup) {
        newGroup.getOccurrences().addAll(group1.getOccurrences());
        newGroup.getOccurrences().addAll(group2.getOccurrences());
        newGroup.getProvenances().addAll(group1.getProvenances());
        newGroup.getProvenances().addAll(group2.getProvenances());
        newGroup.addAllAdditionalCells(group1.getAdditionalCells());
        newGroup.addAllAdditionalCells(group2.getAdditionalCells());
    }

    private OrderingAttribute extractOrderingAttribute(Set<CellRef> cellRefs1, Set<CellRef> cellRefs2, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Extracting ordering attributes for cell ref1:\n\t" + cellRefs1 + "\n\t" + cellRefs2);
        AttributeRef attributeToOrder = extractAttributeIfSame(cellRefs1, cellRefs2);
        if (attributeToOrder == null) {
            if (logger.isDebugEnabled()) logger.debug("These cells have not a common attribute");
            return null;
        }
        for (OrderingAttribute orderingAttribute : scenario.getOrderingAttributes()) {
            if (orderingAttribute.getAttribute().equals(attributeToOrder)) {
                return orderingAttribute;
            }
        }
        return null;
    }

    private AttributeRef extractAttributeIfSame(Set<CellRef> cellRefs1, Set<CellRef> cellRefs2) {
        List<CellRef> cellRefs = new ArrayList<CellRef>();
        cellRefs.addAll(cellRefs1);
        cellRefs.addAll(cellRefs2);
        AttributeRef attributeRef = cellRefs.get(0).getAttributeRef();
        for (CellRef cellRef : cellRefs) {
            if (!cellRef.getAttributeRef().equals(attributeRef)) {
                return null;
            }
        }
        return attributeRef;
    }

    private CellGroup mergeGroupsUsingOrderingAttribute(CellGroup group1, CellGroup group2, OrderingAttribute orderingAttribute, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Merging cell group using ordering attributes...");
        if (logger.isDebugEnabled()) logger.debug("Cell group 1: " + group1.toStringWithAdditionalCells());
        if (logger.isDebugEnabled()) logger.debug("Cell group 2: " + group2.toStringWithAdditionalCells());
        IValueComparator valueComparator = orderingAttribute.getValueComparator();
        Set<Cell> values1 = group1.getAdditionalCells().get(orderingAttribute.getAssociatedAttribute());
        IValue maxValue1 = findMaxValue(values1, valueComparator);
        Set<Cell> values2 = group2.getAdditionalCells().get(orderingAttribute.getAssociatedAttribute());
        IValue maxValue2 = findMaxValue(values2, valueComparator);
        IValue maxValue = CellGroupIDGenerator.getNextLLUNID();
        Integer order = valueComparator.compare(maxValue1, maxValue2);
        if (order == PartialOrderConstants.FOLLOWS) {
            maxValue = group1.getValue();
        }
        if (order == PartialOrderConstants.PRECEDES) {
            maxValue = group2.getValue();
        }
        return mergeCellGroups(group1, group2, maxValue, scenario);
    }

    private IValue findMaxValue(Set<Cell> values, IValueComparator valueComparator) {
        if (values == null) {
            return null;
        }
        IValue max = null;
        for (Iterator<Cell> it = values.iterator(); it.hasNext();) {
            IValue value = it.next().getValue();
            if (max == null || PartialOrderConstants.PRECEDES.equals(valueComparator.compare(max, value))) {
                max = value;
            }
        }
        return max;
    }
}
