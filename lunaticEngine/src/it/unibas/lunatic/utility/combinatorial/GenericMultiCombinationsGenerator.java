package it.unibas.lunatic.utility.combinatorial;

import java.util.ArrayList;
import java.util.List;

public class GenericMultiCombinationsGenerator<T> {

    private List<List<T>> result = new ArrayList<List<T>>();

    public List<List<T>> generate(List<T> objects, int size) {
        generate(objects, new ArrayList<T>(), size);
        return result;
    }

    private void generate(List<T> objects, List<T> currentOutput, int size) {
        if (size == 0) {
            result.add(new ArrayList<T>(currentOutput));
        } else {
            for (T object : objects) {
                currentOutput.add(object);
                generate(objects, currentOutput, size - 1);
                currentOutput.remove(currentOutput.size() - 1);
            }
        }
    }
}
