/*****************************************************************************
JEP 2.4.1, Extensions 1.1.1
April 30 2007
(c) Copyright 2007, Nathan Funk and Richard Morris
See LICENSE-*.txt for license information.
 *****************************************************************************/
package org.nfunk.jep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SymbolTable implements Cloneable {

    private Map<String, Variable> map = new HashMap<String, Variable>();
    private VariableFactory vf;

    /** SymbolTable should always be constructed an associated variable factory. */
    public SymbolTable(VariableFactory varFac) {
        vf = varFac;
    }

    /** Private default constructors, SymbolTable should always be constructed with an explicit variable factory. */
    private SymbolTable() {
    }
    
    public Object clone() {
        try {
            SymbolTable clone = (SymbolTable) super.clone();
            clone.vf = new VariableFactory();
            clone.map = new HashMap<String, Variable>();
            for (String key : map.keySet()) {
                clone.map.put(new String(key), (Variable)map.get(key).clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public List<Variable> getVariables() {
        Collection<Variable> variables = map.values();
        List<Variable> result = new ArrayList<Variable>();
        for (Variable variable : variables) {
            if (!variable.isConstant()) {
                result.add(variable);
            }
        }
        return result;
    }
    
    public List<Object> getConstants() {
        Collection<Variable> variables = map.values();
        List<Object> result = new ArrayList<Object>();
        for (Variable variable : variables) {
            if (variable.isConstant()) {
                result.add(variable.toString());
            }
        }
        return result;
    }

    /**
     * @deprecated The getValue or getVar methods should be used instead. 
     */
    public Object get(Object key) {
        return getValue(key);
    }

    /** Finds the value of the variable with the given name. 
     * Returns null if variable does not exist. */
    public Object getValue(Object key) {
        Variable var = (Variable) map.get(key);
        if (var == null) return null;
        return var.getValue();
    }

    /** Finds the variable with given name. 
     * Returns null if variable does not exist. */
    public Variable getVar(String name) {
        return (Variable) map.get(name);
    }

    /**
     * @deprecated The setVarValue or makeVar methods should be used instead.
     */
    public Object put(Object key, Object val) {
        return makeVarIfNeeded((String) key, val);
    }

    /**
     * Sets the value of variable with the given name.
     * @throws NullPointerException if the variable has not been previously created
     * with {@link #addVariable(String,Object)} first.
     */
    public void setVarValue(String name, Object val) {
        Variable var = (Variable) map.get(name);
        if (var != null) {
            var.setValue(val);
            return;
        }
        throw new NullPointerException("Variable " + name + " does not exist.");
    }

    /**
     * Returns a new variable fro the variable factory. Notifies observers
     * when a new variable is created. If a subclass need to create a new variable it should call this method.
     * 
     * @param name
     * @param val
     * @return an new Variable object.
     */
    protected Variable createVariable(String name, Object val) {
        Variable var = vf.createVariable(name, val);
        return var;
    }

    protected Variable createVariable(String name) {
        Variable var = vf.createVariable(name);
        return var;
    }

    /** Creates a new variable with given value.
     * 
     * @param name name of variable
     * @param val initial value of variable
     * @return a reference to the created variable.
     */
    public Variable addVariable(String name, Object val) {
        Variable var = (Variable) map.get(name);
        if (var != null)
            throw new IllegalStateException("Variable " + name + " already exists.");

        var = createVariable(name, val);
        map.put(name, var);
        var.setValidValue(true);
        return var;
    }

    /** Create a constant variable with the given name and value.
     * Returns null if variable already exists.
     */
    public Variable addConstant(String name, Object val) {
        Variable var = addVariable(name, val);
        var.setIsConstant(true);
        return var;
    }

    /** Create a variable with the given name and value.
     * It silently does nothing if the value cannot be set.
     * @return the Variable.
     */
    public Variable makeVarIfNeeded(String name, Object val) {
        Variable var = (Variable) map.get(name);
        if (var != null) {
            if (var.isConstant())
                throw new IllegalStateException("Attempt to change the value of constant variable " + name);
            var.setValue(val);
            return var;
        }
        var = createVariable(name, val);
        map.put(name, var);
        return var;
    }

    /** If necessary create a variable with the given name.
     * If the variable exists its value will not be changed.
     * @return the Variable.
     */
    public Variable makeVarIfNeeded(String name) {
        Variable var = (Variable) map.get(name);
        if (var != null) return var;

        var = createVariable(name);
        map.put(name, var);
        return var;
    }

    /**
     * Returns a list of variables, one per line.
     */
    public String toString() {
        return map.toString();
    }

    /**
     * Clears the values of all variables.
     * Finer control is available through the
     * {@link Variable#setValidValue Variable.setValidValue} method.
     */
    public void clearValues() {
        for (Variable var : map.values()) {
            var.setValidValue(false);
        }
    }

    /**
     * Returns the variable factory of this instance.
     */
    public VariableFactory getVariableFactory() {
        return vf;
    }

    boolean containsKey(String identString) {
        return map.containsKey(identString);
    }

    public Object remove(String name) {
        return map.remove(name);
    }

    /** Remove all non constant elements */
    public void clearNonConstants() {
        Vector tmp = new Vector();
        for (Variable var : map.values()) {
            if (var.isConstant()) tmp.add(var);
        }
        map.clear();
        for (Variable var : map.values()) {
            map.put(var.getName(), var);
        }
    }
}
