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

public class ParserInstance {

    private static int counter = 0;

    private String id;
    private List<ParserFact> facts = new ArrayList<ParserFact>();

    public ParserInstance() {
        this.id = "instance" + counter++ + ".";
    }

    public String getId() {
        return id;
    }

    public List<ParserFact> getFacts() {
        return facts;
    }

    public void addFact(ParserFact fact) {
        this.facts.add(fact);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------- Parser Instance -----------\n");
        for (ParserFact parserFact : facts) {
            result.append(parserFact).append("\n");
        }
        return result.toString();
    }

}
