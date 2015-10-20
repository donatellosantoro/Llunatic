package it.unibas.lunatic.utility.combinatorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenericPowersetGenerator<T> {
    
    public List<List<T>> generatePowerSet(List<T> v1) {
        List<T> v = new ArrayList<T>(v1);
        List<List<T>> result = new ArrayList<List<T>>();
        if (v.isEmpty()) {
            result.add( new ArrayList<T>() );
            return result;
        }
        T x = v.remove(0);
        List<List<T>> p = generatePowerSet(v);
        Iterator<List<T>> i = p.iterator();
        while (i.hasNext()) {
            List<T> s = i.next(); 
            List<T> t = new ArrayList<T>(s); 
            t.add(x); 
            result.add(s);
            result.add(t);
        }
        return result;
    }
}
