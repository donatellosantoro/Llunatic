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

    @Override
    public String toString() {
        return "VariableEquivalenceClass:" + variables;
    }
        
}
