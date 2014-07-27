package it.unibas.lunatic.utility.combinatorial;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class GenericPermutationsGenerator<T> implements Enumeration {
    
    private List<T> list;
    private int n, m;
    private int[] index;
    private boolean hasMore = true;
    
    public GenericPermutationsGenerator(List<T> list) {
        this(list, list.size());
    }
    
    public GenericPermutationsGenerator(List<T> list, int m) {
        this.list = list;
        this.n = list.size();
        this.m = m;
        index = new int[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        reverseAfter(m - 1);
    }
    
    public boolean hasMoreElements() {
        return hasMore;
    }
    
    private void moveIndex() {
        int i = rightmostDip();
        if (i < 0) {
            hasMore = false;
            return;
        }
        int leastToRightIndex = i + 1;
        for (int j = i + 2; j < n; j++) {
            if (index[j] < index[leastToRightIndex] &&  index[j] > index[i])
                leastToRightIndex = j;
        }
        int t = index[i];
        index[i] = index[leastToRightIndex];
        index[leastToRightIndex] = t;
        
        if (m - 1 > i) {
            reverseAfter(i);
            reverseAfter(m - 1);
        }
        
    }

    public List<T> nextElement() {
        if (!hasMore)
            return null;
        List<T> out = new ArrayList<T>();
        for (int i = 0; i < m; i++) {
            out.add(list.get(index[i]));
        }
        moveIndex();
        return out;
    }

    private void reverseAfter(int i) {
        int start = i + 1;
        int end = n - 1;
        while (start < end) {
            int t = index[start];
            index[start] = index[end];
            index[end] = t;
            start++;
            end--;
        }
    }

    private int rightmostDip() {
        for (int i = n - 2; i >= 0; i--) {
            if (index[i] < index[i+1])
                return i;
        }
        return -1;
    }
}