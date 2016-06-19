package it.unibas.lunatic;

import org.openide.util.Lookup;

public interface IGlobalLookupProvider {

    void add(Object inst);

    void put(Object inst);

    void remove(Object inst);

    void remove(Class<?> key);

    public Lookup getApplicationLookup();
}
