package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.StringComparatorForIValues;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

@SuppressWarnings("unchecked")
public class AlgebraUtility {
    
    private static Logger logger = LoggerFactory.getLogger(AlgebraUtility.class);
        
    public static void addIfNotContained(List list, Object object) {
        LunaticUtility.addIfNotContained(list, object);
    }

    public static IValue getCellValue(Tuple tuple, AttributeRef attributeRef) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attributeRef)) {
                return cell.getValue();
            }
        }
        throw new IllegalArgumentException("Unable to find attribute " + attributeRef + " in tuple " + tuple.toStringWithOIDAndAlias());
    }

    public static boolean contains(Tuple tuple, AttributeRef attributeRef) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attributeRef)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<Object> getTupleValuesExceptOIDs(Tuple tuple) {
        List<Object> values = new ArrayList<Object>();
        for (Cell cell : tuple.getCells()) {
            if(cell.getAttribute().equals(SpeedyConstants.OID)){
                continue;
            }
            IValue attributeValue = cell.getValue();
            values.add(attributeValue.getPrimitiveValue().toString());
        }
        return values;
    }

    public static List<Object> getNonOidTupleValues(Tuple tuple) {
        List<Object> values = new ArrayList<Object>();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equals(SpeedyConstants.OID)) {
                continue;
            }                
            IValue attributeValue = cell.getValue();
            values.add(attributeValue.getPrimitiveValue());
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static boolean equalLists(List list1, List list2) {
        return (list1.containsAll(list2) && list2.containsAll(list1));
    }

    public static boolean areEqualExcludingOIDs(Tuple t1, Tuple t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return equalLists(getTupleValuesExceptOIDs(t1), getTupleValuesExceptOIDs(t2));
    }

    public static void removeDuplicates(List result) {
        if (result.isEmpty()) {
            return;
        }
        Collections.sort(result, new StringComparatorForIValues());
        Iterator tupleIterator = result.iterator();
        String prevValues = tupleIterator.next().toString();
        while (tupleIterator.hasNext()) {
            Object currentTuple = tupleIterator.next();
            String currentValues = currentTuple.toString();
            if (prevValues.equals(currentValues)) {
                tupleIterator.remove();
            } else {
                prevValues = currentValues;
            }
        }
    }
    
    public static List<TableAlias> findAliasesForAtom(IFormulaAtom atom) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (FormulaVariable variable : atom.getVariables()) {
            for (TableAlias tableAlias : findAliasesForVariable(variable)) {
                if (!result.contains(tableAlias)) {
                    result.add(tableAlias);
                }
            }            
        }
        return result;
    }

    public static List<TableAlias> findAliasesForVariable(FormulaVariable variable) {
        List<TableAlias> result = new ArrayList<TableAlias>();        
        for (FormulaVariableOccurrence occurrence : variable.getPremiseRelationalOccurrences()) {
            TableAlias tableAlias = occurrence.getAttributeRef().getTableAlias();
            if (!result.contains(tableAlias)) {
                result.add(tableAlias);
            }
        }
        return result;
    }


    public static List<TableAlias> findAliasesForFormula(PositiveFormula formula) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                result.add(((RelationalAtom)atom).getTableAlias());
            }
        }
        return result;
    }
    
    public static FormulaVariable findVariable(String variableId, List<FormulaVariable> variables) {
        for (FormulaVariable formulaVariable : variables) {
            if (formulaVariable.getId().equals(variableId)) {
                return formulaVariable;
            }
        }
        return null;
    }

    public static String getPlaceholderId(FormulaVariable variable) {
        return "$$" + variable.getId() + "";
    }
    
    public static boolean isPlaceholder(Variable jepVariable) {
        return jepVariable.getDescription().toString().startsWith("$$");
//        return jepVariable.getDescription().toString().startsWith("$$") &&
//                jepVariable.getDescription().toString().endsWith("#");
    }

}

