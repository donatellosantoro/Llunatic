package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;

public class VariableEquivalenceClass {

    private List<FormulaVariable> variables = new ArrayList<FormulaVariable>();

    public List<FormulaVariable> getVariables() {
        return variables;
    }

    public void addVariable(FormulaVariable variable) {
        this.variables.add(variable);
    }

    public void addVariables(List<FormulaVariable> variables) {
        this.variables.addAll(variables);
    }

    public boolean contains(FormulaVariable variable) {
        return this.variables.contains(variable);
    }

    public List<FormulaVariableOccurrence> getPremiseRelationalOccurrences() {
        List<FormulaVariableOccurrence> result = new ArrayList<FormulaVariableOccurrence>();
        for (FormulaVariable variable : variables) {
            result.addAll(variable.getPremiseRelationalOccurrences());
        }
        return result;
    }
    
    public List<FormulaVariableOccurrence> getConclusionRelationalOccurrences() {
        List<FormulaVariableOccurrence> result = new ArrayList<FormulaVariableOccurrence>();
        for (FormulaVariable variable : variables) {
            result.addAll(variable.getConclusionRelationalOccurrences());
        }
        return result;
    }
    
    public List<IFormulaAtom> getNonRelationalOccurrences() {
        List<IFormulaAtom> result = new ArrayList<IFormulaAtom>();
        for (FormulaVariable variable : variables) {
            result.addAll(variable.getNonRelationalOccurrences());
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "VariableEquivalenceClass:" + variables;
    }

}
