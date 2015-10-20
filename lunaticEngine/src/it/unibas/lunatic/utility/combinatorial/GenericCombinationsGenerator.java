package it.unibas.lunatic.utility.combinatorial;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class GenericCombinationsGenerator<T> implements Enumeration {
    
    protected List<T> inputCollections;
    protected int inputCollectionSize, dimension;
    protected int[] array;
    protected boolean hasMore = true;

    public GenericCombinationsGenerator(List<T> inputCollections, int dimension) {
        this.inputCollections = inputCollections;
        this.inputCollectionSize = inputCollections.size();
        this.dimension = dimension;
        assert(0 <= dimension && dimension <= inputCollectionSize) : "Unable to generate combinations of size: " + dimension + " of " + inputCollectionSize + " elements";
        array = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            array[i] = i;
        }
    }
    
    public boolean hasMoreElements() {
        return hasMore;
    }

    protected void moveIndex() {
        int i = rightmostIndexBelowMax();
        if (i >= 0) {
            array[i] = array[i] + 1;
            for (int j = i + 1; j < dimension; j++) {
                array[j] = array[j-1] + 1;
            }
        } else {
            hasMore = false;
        }
    }

    public List<T> nextElement() {
        if (!hasMore) {
            return null;
        }
        List<T> combination = new ArrayList<T>();
        for (int i = 0; i < dimension; i++) {
            combination.add(i, this.inputCollections.get(array[i]));
        }
        moveIndex();
        return combination;
    }

    protected int rightmostIndexBelowMax() {       
        for (int i = dimension - 1; i >= 0; i--) {
            if (array[i] < inputCollectionSize - dimension + i) {
                return i;
            }
        }
        return -1;
    }
}
