package it.unibas.lunatic.test.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactValue implements IExpectedValue {

    private static Logger logger = LoggerFactory.getLogger(FactValue.class);

    public final String NULL = "NULL";
    public final String SKOLEM = "N_";

    private String value;

    public FactValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isUniversal() {
        return !isNullOrSkolem();
    }

    public boolean isNull() {
        return value.equalsIgnoreCase(NULL);
    }

    public boolean isSkolem() {
        return value.startsWith(SKOLEM);
    }

    public boolean isNullOrSkolem() {
        return isNull() || isSkolem();

    }

    public boolean equals(Object obj) {
        String valueString = obj.toString();
        if (valueString.charAt(0) == '\"') {
            valueString = valueString.substring(1);
        }
        if (valueString.charAt(valueString.length() - 1) == '\"') {
            valueString = valueString.substring(0, valueString.length() - 1);
        }
        return this.getValue().equals(valueString);
    }

    public boolean equalsSkolem(IExpectedValue otherValue) {
        return this.isSkolem() && otherValue.isNullOrSkolem() && this.value.equals(otherValue.getValue());
    }

    public String toString() {
        return value;
    }

}
