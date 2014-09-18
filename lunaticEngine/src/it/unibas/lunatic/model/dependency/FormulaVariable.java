package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;

public class FormulaVariable implements Cloneable{

    private String id;
    private List<FormulaVariableOccurrence> premiseOccurrences = new ArrayList<FormulaVariableOccurrence>();
    private List<FormulaVariableOccurrence> conclusionOccurrences = new ArrayList<FormulaVariableOccurrence>();
    private List<IFormulaAtom> nonRelationalOccurrences = new ArrayList<IFormulaAtom>();

    public FormulaVariable(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<FormulaVariableOccurrence> getPremiseOccurrences() {
        return premiseOccurrences;
    }
    
    public void addPremiseOccurrence(FormulaVariableOccurrence occurrence) {
        this.premiseOccurrences.add(occurrence);
    }

    public List<FormulaVariableOccurrence> getConclusionOccurrences() {
        return conclusionOccurrences;
    }
    
    public void addConclusionOccurrence(FormulaVariableOccurrence occurrence) {
        this.conclusionOccurrences.add(occurrence);
    }

    public List<IFormulaAtom> getNonRelationalOccurrences() {
        return nonRelationalOccurrences;
    }

    public void addNonRelationalOccurrence(IFormulaAtom atom) {
        if (atom instanceof RelationalAtom) {
            throw new IllegalArgumentException("This is a relational atom occurrence: " + atom);
        }
        if (this.nonRelationalOccurrences.contains(atom)) {
            return;
        }
        this.nonRelationalOccurrences.add(atom);
    }

    public void setPremiseOccurrences(List<FormulaVariableOccurrence> premiseOccurrences) {
        this.premiseOccurrences = premiseOccurrences;
    }

    public void setConclusionOccurrences(List<FormulaVariableOccurrence> conclusionOccurrences) {
        this.conclusionOccurrences = conclusionOccurrences;
    }

    public void setNonRelationalOccurrences(List<IFormulaAtom> nonRelationalOccurrences) {
        this.nonRelationalOccurrences = nonRelationalOccurrences;
    }

    
    public boolean isUniversal() {
        return this.premiseOccurrences.size() > 0;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    public String toLongString() {
        return this.toString() + " Occurrences - Premise: " + premiseOccurrences + " Conclusion: " + conclusionOccurrences  + " NonRelational: " + nonRelationalOccurrences;
    }
       
    
    @Override
    public FormulaVariable clone(){
        FormulaVariable clone = null;
        try {
            clone = (FormulaVariable) super.clone();
            clone.premiseOccurrences = new ArrayList<FormulaVariableOccurrence>();
            for (FormulaVariableOccurrence occurrence : premiseOccurrences) {
                clone.premiseOccurrences.add((FormulaVariableOccurrence) occurrence.clone());
            }
            clone.conclusionOccurrences = new ArrayList<FormulaVariableOccurrence>();
            for (FormulaVariableOccurrence occurrence : conclusionOccurrences) {
                clone.conclusionOccurrences.add((FormulaVariableOccurrence) occurrence.clone());
            }            
            clone.nonRelationalOccurrences = new ArrayList<IFormulaAtom>(this.nonRelationalOccurrences);
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }
}
