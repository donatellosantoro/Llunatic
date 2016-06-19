/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nfunk.jep.function.PostfixMathCommandI;

/*
 * A Hashtable which holds a list of functions.
 */
public class FunctionTable implements Cloneable {

    private Map<String, PostfixMathCommandI> map = new HashMap<String, PostfixMathCommandI>();
    
    public FunctionTable() { }

    @Override
    public Object clone() {        
        try {
            FunctionTable clone = (FunctionTable) super.clone();
            clone.map = (Map<String, PostfixMathCommandI>) ((HashMap)this.map).clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    /** adds the PostfixMathCommandI for the function with name s. 
     * RJM addition Oct 03
     */
    public Object put(String s, PostfixMathCommandI pfmc) {
        return map.put(s, pfmc);
    }

    /** overrides the standard hashtable method.
     * If the arguments are of the wrong type then throws
     * ClassCastException
     * RJM addition Oct 03
     * TODO is Hashtable always index by Strings?
     */
    public Object put(Object o, Object p) {
        return put((String) o, (PostfixMathCommandI) p);
    }

    /** returns the PostfixMathCommandI for function with name s. 
     * RJM addition Oct 03
     */
    public PostfixMathCommandI get(String s) {
        return (PostfixMathCommandI) map.get(s);
    }

    /** overrides the standard hashtable method.
     * If the argument is of the wrong type (i.e. not a String) 
     * then throws ClassCastException
     * RJM addition Oct 03
     */
    public Object get(Object o) {
        return get((String) o);
    }

    boolean containsKey(String key) {
        return map.containsKey(key);
    }

    Object remove(String name) {
        return map.remove(name);
    }
}
