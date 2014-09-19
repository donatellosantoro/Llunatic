package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public class ComparisonAtom implements IFormulaAtom {

    private IFormula formula;
    private Expression expression;
    private String operator;
    private List<FormulaVariable> variables = new ArrayList<FormulaVariable>();

    public ComparisonAtom(IFormula formula, Expression expression, String operator) {
        this.formula = formula;
        this.expression = expression;
        this.operator = operator;
    }

    public Expression getExpression() {
        return expression;
    }

    public IFormula getFormula() {
        return formula;
    }

    public String getOperator() {
        return operator;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }

    public void addVariable(FormulaVariable variable) {
        this.variables.add(variable);
    }

    public List<FormulaVariable> getVariables() {
        return this.variables;
    }
    
    public FormulaVariable getFirstVariable() {
        return variables.get(0);
    }

    public FormulaVariable getSecondVariable() {
        if (variables.size() == 1) {
            return null;
        }
        return variables.get(1);
    }

    public boolean isEqualityComparison() {
        return LunaticConstants.EQUAL.equals(operator);
    }

    public boolean isVariableEqualityComparison() {
        return isEqualityComparison() && variables.size() == 2;
    }

    public IFormulaAtom clone() {
        try {
            ComparisonAtom clone = (ComparisonAtom) super.clone();
            clone.expression = this.expression.clone();
            clone.variables = new ArrayList<FormulaVariable>(this.variables);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone ComparisonAtom " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }

}
