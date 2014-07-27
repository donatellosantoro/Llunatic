package it.unibas.lunatic.model.database;

import it.unibas.lunatic.LunaticConstants;

public class LLUNValue implements IValue {

    private Object value;

    public LLUNValue(Object value) {
        this.value = value;
    }    

    public String getType() {
        return LunaticConstants.LLUN;
    }

    public Object getValue() {
        return this.value;
    }
    
    public Object getPrimitiveValue() {
        return this.toString();
    }

    @Override
    public String toString() {
        if(value == null){
            return "NULL";
        }
        return value.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final LLUNValue other = (LLUNValue) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) return false;
        return true;
    }

}
