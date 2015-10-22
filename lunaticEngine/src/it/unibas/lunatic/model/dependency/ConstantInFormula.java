package it.unibas.lunatic.model.dependency;

import speedy.model.database.AttributeRef;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.List;

public class ConstantInFormula {

    private final Object constantValue;
    private final FormulaVariable formulaVariable;
    private final boolean premise;

    public ConstantInFormula(Object constantValue, boolean premise) {
        this.constantValue = constantValue;
        this.formulaVariable = new FormulaVariable(DependencyUtility.buildVariableIdForConstant(constantValue));
        this.premise = premise;
    }

    public Object getConstantValue() {
        return constantValue;
    }

    public boolean isPremise() {
        return premise;
    }

    public FormulaVariable getFormulaVariable() {
        return formulaVariable;
    }

    public String getVariableId() {
        return this.formulaVariable.getId();
    }

    public List<FormulaVariableOccurrence> getPremiseRelationalOccurrences() {
        return this.formulaVariable.getPremiseRelationalOccurrences();
    }

    public void addPremiseRelationalOccurrence(AttributeRef attributeRef) {
        this.getPremiseRelationalOccurrences().add(new FormulaVariableOccurrence(attributeRef, DependencyUtility.buildVariableIdForConstant(constantValue)));
    }

    public List<FormulaVariableOccurrence> getConclusionRelationalOccurrences() {
        return this.formulaVariable.getConclusionRelationalOccurrences();
    }

    public void addConclusionRelationalOccurrence(AttributeRef attributeRef) {
        this.getConclusionRelationalOccurrences().add(new FormulaVariableOccurrence(attributeRef, DependencyUtility.buildVariableIdForConstant(constantValue)));
    }

    public List<IFormulaAtom> getNonRelationalOccurrences() {
        return this.formulaVariable.getNonRelationalOccurrences();
    }
    
    @Override
    public String toString() {
        return "ConstantOccurrences{" + constantValue
                + "\nnew variable=" + formulaVariable
                + "\npremiseOccurrences=" + getPremiseRelationalOccurrences()
                + "\nconclusionOccurrences=" + getConclusionRelationalOccurrences()
                + "\nnonRelationalOccurrences=" + getNonRelationalOccurrences() + '}';
    }

}
