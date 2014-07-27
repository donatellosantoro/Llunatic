package it.unibas.lunatic.utility.combinatorial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericListGenerator<T> {
    
    private static Logger logger = LoggerFactory.getLogger(GenericListGenerator.class);
    
    @SuppressWarnings("unchecked")
    public List<List<T>> generateListsOfElements(List<List<T>> listOfLists) {
        if (listOfLists.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return generate(listOfLists);
    }
    
    private int estimateResultSize(List<List<T>> listOfLists) {
        int result = 1;
        for (List<T> list : listOfLists) {
            result *= list.size();
        }
        return result;
    }
    
    private List<List<T>> generate(List<List<T>> listOfLists) {
        if (listOfLists.size() == 1) {
            if (logger.isTraceEnabled()) logger.trace("Base call with list of size = " + listOfLists.size());
            List<List<T>> result = new ArrayList<List<T>>();
            List<T> firstListOfElements = listOfLists.get(0);
            for (T element : firstListOfElements) {
                List<T> singletonList = new ArrayList<T>();
                singletonList.add(element);
                result.add(singletonList);
            }
            if (logger.isTraceEnabled()) logger.trace("Result: " + result);
            return result;
        }
        if (logger.isDebugEnabled()) logger.debug("Recursive call with list of size = " + listOfLists.size());
        List<List<T>> combinationsOfRest = generateListsOfElements(rest(listOfLists));
        if (logger.isDebugEnabled()) logger.debug("Returning from recursive call = " + listOfLists.size());
        List<T> firstList = listOfLists.get(0);
        if (logger.isTraceEnabled()) logger.trace("First list of variables = " + firstList);
        List<List<T>> result = new ArrayList<List<T>>();
        for (T elem : firstList) {
            if (logger.isTraceEnabled()) logger.trace("Adding element: " + elem);
            int j=0;
            for (List<T> combination : combinationsOfRest) {
                if (logger.isTraceEnabled()) logger.trace("To combination: " + combination);
                List<T> newCombination = generateNewAlternative(elem, combination);
                if (logger.isTraceEnabled()) logger.trace("New combination: " + newCombination);
                result.add(newCombination);
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private List<List<T>> rest(List<List<T>> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Empty list does not have rest");
        }
        List<List<T>> clone = (List<List<T>>)((ArrayList<List<T>>)list).clone();
        clone.remove(0);
        return clone;
    }
    
    @SuppressWarnings("unchecked")
    private List<T> generateNewAlternative(T element, List<T> combination) {
        List<T> newCombination = (List<T>)((ArrayList<T>)combination).clone();
        newCombination.add(0, element);
        return newCombination;
    }
    
}
