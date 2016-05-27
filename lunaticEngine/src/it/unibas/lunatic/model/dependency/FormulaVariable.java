package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;
import speedy.model.database.AttributeRef;
import speedy.model.database.IVariableDescription;

public class FormulaVariable implements Cloneable, IVariableDescription {

    private String id;
    private List<FormulaVariableOccurrence> premiseRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
    private List<FormulaVariableOccurrence> conclusionRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
    private List<IFormulaAtom> nonRelationalOccurrences = new ArrayList<IFormulaAtom>();

    public FormulaVariable(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<FormulaVariableOccurrence> getPremiseRelationalOccurrences() {
        return premiseRelationalOccurrences;
    }

    public void addPremiseRelationalOccurrence(FormulaVariableOccurrence occurrence) {
        this.premiseRelationalOccurrences.add(occurrence);
    }

    public List<FormulaVariableOccurrence> getConclusionRelationalOccurrences() {
        return conclusionRelationalOccurrences;
    }

    public void addConclusionRelationalOccurrence(FormulaVariableOccurrence occurrence) {
        this.conclusionRelationalOccurrences.add(occurrence);
    }

    public List<IFormulaAtom> getNonRelationalOccurrences() {
        return nonRelationalOccurrences;
    }

    public void addNonRelationalOccurrence(IFormulaAtom atom) {
        if(atom == null){
            throw new IllegalArgumentException("Adding null atom to occurrence");
        }
        if (atom instanceof RelationalAtom) {
            throw new IllegalArgumentException("This is a relational atom occurrence: " + atom);
        }
        if (this.nonRelationalOccurrences.contains(atom)) {
            return;
        }
        this.nonRelationalOccurrences.add(atom);
    }

    public void setPremiseRelationalOccurrences(List<FormulaVariableOccurrence> premiseOccurrences) {
        this.premiseRelationalOccurrences = premiseOccurrences;
    }

    public void setConclusionRelationalOccurrences(List<FormulaVariableOccurrence> conclusionOccurrences) {
        this.conclusionRelationalOccurrences = conclusionOccurrences;
    }

    public void setNonRelationalOccurrences(List<IFormulaAtom> nonRelationalOccurrences) {
        this.nonRelationalOccurrences = nonRelationalOccurrences;
    }

    public boolean isUniversal() {
        return this.premiseRelationalOccurrences.size() > 0;
    }

    public List<AttributeRef> getAttributeRefs() {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence variableOccurrence : this.getPremiseRelationalOccurrences()) {
            result.add(variableOccurrence.getAttributeRef());
        }
        return result;
    }

    @Override
    public FormulaVariable clone() {
        FormulaVariable clone = null;
        try {
            clone = (FormulaVariable) super.clone();
            clone.premiseRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
            for (FormulaVariableOccurrence occurrence : premiseRelationalOccurrences) {
                clone.premiseRelationalOccurrences.add((FormulaVariableOccurrence) occurrence.clone());
            }
            clone.conclusionRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
            for (FormulaVariableOccurrence occurrence : conclusionRelationalOccurrences) {
                clone.conclusionRelationalOccurrences.add((FormulaVariableOccurrence) occurrence.clone());
            }
            clone.nonRelationalOccurrences = new ArrayList<IFormulaAtom>(this.nonRelationalOccurrences);
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    @Override
    public String toString() {
        return id;
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());
        sb.append("\n Occurrences - Premise: ");
        for (FormulaVariableOccurrence occurrence : premiseRelationalOccurrences) {
            sb.append(occurrence.toLongString()).append(" ");
        }
        sb.append("\n\t Conclusion: ");
        for (FormulaVariableOccurrence occurrence : conclusionRelationalOccurrences) {
            sb.append(occurrence.toLongString()).append(" ");
        }
        sb.append("\n\t NonRelational: ").append(nonRelationalOccurrences);
        return sb.toString();
    }

}
