/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

import org.openide.util.Lookup;

/**
 *
 * @author Antonio Galotta
 */
public interface IGlobalLookupProvider {

    void add(Object inst);

    void put(Object inst);

    void remove(Object inst);

    void remove(Class<?> key);

    public Lookup getApplicationLookup();
}
