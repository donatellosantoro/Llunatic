package it.unibas.lunatic.utility;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.ViolationContext;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IDataSourceNullValue;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.nodes.AttributeNode;
import speedy.model.database.mainmemory.datasource.nodes.LeafNode;
import speedy.model.database.mainmemory.datasource.nodes.MetadataNode;
import speedy.model.database.mainmemory.datasource.nodes.SequenceNode;
import speedy.model.database.mainmemory.datasource.nodes.SetNode;
import speedy.model.database.mainmemory.datasource.nodes.TupleNode;
import speedy.persistence.Types;

public class LunaticUtility {

    @SuppressWarnings("unchecked")
    public static void addIfNotContained(List list, Object object) {
        if (!list.contains(object)) {
            list.add(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addAllIfNotContained(List dst, Collection src) {
        for (Object object : src) {
            addIfNotContained(dst, object);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean equalLists(List list1, List list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        List list2Clone = new ArrayList(list2);
        for (Object o : list1) {
            if (!list2Clone.contains(o)) {
                return false;
            } else {
                list2Clone.remove(o);
            }
        }
        return (list2Clone.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public static boolean areEqualConsideringOrder(List listA, List listB) {
        return !areDifferentConsideringOrder(listA, listB);
    }

    @SuppressWarnings("unchecked")
    public static boolean areDifferentConsideringOrder(List listA, List listB) {
        if (listA.size() != listB.size()) {
            return true;
        }
        for (int i = 0; i < listA.size(); i++) {
            Object valueA = listA.get(i);
            Object valueB = listB.get(i);
            if (!valueA.equals(valueB)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean contained(Collection list1, Collection list2) {
        if (list1.isEmpty()) {
            return true;
        }
        return (list2.containsAll(list1));
    }

    public static INode createRootNode(INode node) {
        String type = node.getClass().getSimpleName();
        INode rootNode = createNode(type, node.getLabel(), IntegerOIDGenerator.getNextOID());
        rootNode.setRoot(true);
        return rootNode;
    }

    public static INode createNode(String nodeType, String label, Object value) {
        if (nodeType.equals("SetNode")) {
            return new SetNode(label, value);
        }
        if (nodeType.equals("TupleNode")) {
            return new TupleNode(label, value);
        }
        if (nodeType.equals("SequenceNode")) {
            return new SequenceNode(label, value);
        }
        if (nodeType.equals("AttributeNode")) {
            return new AttributeNode(label, value);
        }
        if (nodeType.equals("MetadataNode")) {
            return new MetadataNode(label, value);
        }
        if (nodeType.equals("LeafNode")) {
            return new LeafNode(label, value);
        }
        return null;
    }

    public static String removeRootLabel(String pathString) {
        return pathString.substring(pathString.indexOf(".") + 1);
    }

    public static String generateFolderPath(String filePath) {
        return FilenameUtils.getFullPath(filePath);
    }

    public static String generateSetNodeLabel() {
        return "Set";
    }

    public static String generateTupleNodeLabel() {
        return "Tuple";
    }

    public static Object findAttributeValue(INode tuple, String attributeLabel) {
        INode attribute = tuple.getChild(attributeLabel);
        if (attribute == null) {
            throw new IllegalArgumentException("Unable to find attribute: " + attributeLabel + " in tuple " + tuple);
        }
        INode leaf = attribute.getChild(0);
        return leaf.getValue();
    }

    public static FormulaVariable findPremiseVariableInDepedency(FormulaVariableOccurrence occurrence, Dependency dependency) {
        return findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
    }

    public static FormulaVariable findVariableInList(FormulaVariableOccurrence occurrence, List<FormulaVariable> variables) {
        for (FormulaVariable formulaVariable : variables) {
            if (formulaVariable.getId().equals(occurrence.getVariableId())) {
                return formulaVariable;
            }
        }
        return null;
    }

    public static Tuple createTuple(INode tupleNode, String tableName) {
        TupleOID tupleOID = new TupleOID(tupleNode.getValue());
        Tuple tuple = new Tuple(tupleOID);
        Cell oidCell = new Cell(tupleOID, new AttributeRef(tableName, SpeedyConstants.OID), new ConstantValue(tupleOID));
        tuple.addCell(oidCell);
        for (INode attributeNode : tupleNode.getChildren()) {
            String attributeName = attributeNode.getLabel();
            Object attributeValue = attributeNode.getChild(0).getValue();
            IValue value;
            if (attributeValue instanceof IDataSourceNullValue) {
                value = new NullValue(attributeValue);
            } else if (attributeValue instanceof NullValue) {
                value = (NullValue) attributeValue;
            } else if (attributeValue instanceof LLUNValue) {
                value = (LLUNValue) attributeValue;
            } else {
                value = new ConstantValue(attributeValue);
            }
            Cell cell = new Cell(tupleOID, new AttributeRef(tableName, attributeName), value);
            tuple.addCell(cell);
        }
        return tuple;
    }

    /////////////////////////////////////   PRINT METHODS   /////////////////////////////////////
    public static String printCollection(Collection l) {
        return printCollection(l, "");
    }

    public static String printCollection(Collection l, String indent) {
        if (l == null) {
            return indent + "(null)";
        }
        if (l.isEmpty()) {
            return indent + "(empty collection)";
        }
        StringBuilder result = new StringBuilder();
        for (Object o : l) {
            result.append(indent).append(o).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String printDependencyIds(List<Dependency> dependencies) {
        StringBuilder result = new StringBuilder();
        for (Dependency dependency : dependencies) {
            result.append(dependency.getId()).append(" ");
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static String printMap(Map m) {
        String indent = "    ";
        StringBuilder result = new StringBuilder("----------------------------- MAP (size =").append(m.size()).append(") ------------\n");
        List<Object> keys = new ArrayList<Object>(m.keySet());
        Collections.sort(keys, new StringComparator());
        for (Object key : keys) {
            result.append("***************** Key ******************\n").append(key).append("\n");
            Object value = m.get(key);
            result.append(indent).append("---------------- Value ---------------------\n");
            if (value instanceof Collection) {
                result.append("size: ").append(((Collection) value).size()).append("\n");
                result.append(printCollection((Collection) value, indent)).append("\n");
            } else {
                result.append(indent).append(value).append("\n");
            }
        }
        return result.toString();
    }

    public static String printVariablesWithOccurrences(List<FormulaVariable> variables) {
        StringBuilder result = new StringBuilder();
        for (FormulaVariable formulaVariable : variables) {
            result.append(formulaVariable.toLongString()).append("\n");
        }
        return result.toString();
    }

    public static String printTupleIterator(Iterator<Tuple> iterator) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            result.append(tuple.toStringWithAlias()).append("\n");
            counter++;
        }
        result.insert(0, "Number of tuples: " + counter + "\n");
        return result.toString();
    }

    public static void removeChars(int charsToRemove, StringBuilder result) {
        if (charsToRemove > result.length()) {
            throw new IllegalArgumentException("Unable to remove " + charsToRemove + " chars from a string with " + result.length() + " char!");
        }
        result.delete(result.length() - charsToRemove, result.length());
    }

    public static IValue getAttributevalueInTuple(Tuple tuple, String attribute) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equalsIgnoreCase(attribute)) {
                return cell.getValue();
            }
        }
        throw new IllegalArgumentException("Unable to find attribute " + attribute + "  in tuple " + tuple);
    }

    public static List<Cell> createCellsFromCellRefs(List<CellRef> cellRefs, IValue value) {
        List<Cell> result = new ArrayList<Cell>();
        for (CellRef cellRef : cellRefs) {
            result.add(new Cell(cellRef, value));
        }
        return result;
    }

    public static void addAll(List<Cell> allCells, List<Cell> newCells) {
        for (Cell cell : newCells) {
            if (!allCells.contains(cell)) {
                allCells.add(cell);
            }
        }
    }

    public static String printIterator(ITupleIterator iterator) {
        StringBuilder result = new StringBuilder();
        while (iterator.hasNext()) {
            result.append(SpeedyConstants.INDENT).append(iterator.next().toStringWithOID()).append("\n");
        }
        iterator.reset();
        return result.toString();
    }

    public static boolean canDoBackward(Scenario scenario) {
        if (!scenario.getSymmetricCostManager().isDoBackward()) {
            return false;
        }
        return true;
    }

    public static List<AttributeRef> extractAttributesInCellGroups(List<CellGroup> cellGroups) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (CellGroup cellGroup : cellGroups) {
            for (Cell cell : cellGroup.getOccurrences()) {
                if (!result.contains(cell.getAttributeRef())) {
                    result.add(cell.getAttributeRef());
                }
            }
        }
        return result;
    }

    public static String printNodeIds(List<DeltaChaseStep> nodes) {
        StringBuilder result = new StringBuilder();
        for (DeltaChaseStep node : nodes) {
            result.append(node.getId()).append(", ");
        }
        return result.toString();
    }
//    public static String extractValueFromLabel(String label) {
//        if (!label.contains(SpeedyConstants.VALUE_LABEL)) {
//            return label;
//        }
//        return label.substring(0, label.indexOf(SpeedyConstants.VALUE_LABEL));
//    }

    public static Attribute getAttribute(AttributeRef attributeRef, IDatabase db) {
        ITable table = db.getTable(attributeRef.getTableName());
        return table.getAttribute(attributeRef.getName());
    }

    public static boolean isAuthoritative(String tableName, Scenario scenario) {
        return scenario.getAuthoritativeSources().contains(tableName);
    }

    public static String findType(Object value) {
        try {
            Integer.parseInt(value.toString());
            return Types.INTEGER;
        } catch (NumberFormatException e) {
        }
        try {
            Double.parseDouble(value.toString());
            return Types.DOUBLE;
        } catch (NumberFormatException e) {
        }
        return Types.STRING;
    }

    public static String printViolationContextIDs(List<ViolationContext> backwardContexts) {
        StringBuilder sb = new StringBuilder();
        for (ViolationContext backwardContext : backwardContexts) {
            sb.append(backwardContext.toShortString()).append(" ");
        }
        return sb.toString();
    }

}

class StringComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
