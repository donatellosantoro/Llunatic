/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com

This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool

++Spicy is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

++Spicy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibas.lunatic.parser;

import it.unibas.lunatic.model.expressions.Expression;

public class ParserAttribute implements Cloneable {

    private String name;
    private String variable;
    private Object value;

    public ParserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        cleanValue();
    }

    public void setValue(Object value) {
        this.value = value;
        cleanValue();
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getVariable() {
        return variable;
    }

    public boolean isVariable() {
        return this.variable != null;
    }

    public boolean isExpression() {
        return (this.value instanceof Expression );
    }

    private void cleanValue() {
        if (this.value instanceof String) {
            String stringValue = (String) value;
            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                this.value = stringValue.substring(1, stringValue.length() - 1);
            }
        }
    }

    public ParserAttribute clone() {
        try {
            return (ParserAttribute) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        String result = name + ": ";
        String printValue = this.value + "";
        if (this.variable != null) {
            printValue = "$" + this.variable;
        }
        return result + printValue;
    }
}
