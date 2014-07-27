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

import java.util.ArrayList;
import java.util.List;

public class ParserTable {

    private String set;
    private List<ParserAttribute> attributes = new ArrayList<ParserAttribute>();

    public ParserTable(String set) {
        this.set = set;
    }

    public void addAttribute(ParserAttribute attribute) {
        this.attributes.add(attribute);
    }

    public List<ParserAttribute> getAttributes() {
        return attributes;
    }

    public String getSet() {
        return set;
    }

    public String toString() {
        String result = set + "(";
        for (int i = 0; i < attributes.size(); i++) {
            result += attributes.get(i);
            if (i != attributes.size() - 1) {
                result += ", ";
            }
        }
        result += ")";
        return result;
    }
}
