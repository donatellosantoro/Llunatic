package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public class ComparisonAtom implements IFormulaAtom {

    private IFormula formula;
    private Expression expression;
    private List<FormulaVariable> variables = new ArrayList<FormulaVariable>();
    private String leftConstant;
    private String rightConstant;
    private String operator;

    public ComparisonAtom(IFormula formula, Expression expression, String leftConstant, String rightConstant, String operator) {
        this.formula = formula;
        this.expression = expression;
        this.leftConstant = leftConstant;
        this.rightConstant = rightConstant;
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

    public boolean isEqualityComparison() {
        return LunaticConstants.EQUAL.equals(operator.trim());
    }

    public FormulaVariable getLeftVariable() {
        if (leftConstant == null) {
            return variables.get(0);
        }
        return null;
    }

    public FormulaVariable getRightVariable() {
        if (rightConstant == null && leftConstant != null) {
            return variables.get(0);
        } else if (rightConstant == null && leftConstant == null) {
            return variables.get(1);
        }
        return null;
    }

    public String getLeftConstant() {
        return leftConstant;
    }

    public void setLeftConstant(String leftConstant) {
        this.leftConstant = leftConstant;
    }

    public String getRightConstant() {
        return rightConstant;
    }

    public void setRightConstant(String rightConstant) {
        this.rightConstant = rightConstant;
    }

    public String getLeftArgument() {
        if (leftConstant == null) {
            return getLeftVariable().toString();
        }
        return leftConstant;
    }

    public String getRightArgument() {
        if (rightConstant == null) {
            return getRightVariable().toString();
        }
        return rightConstant;
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

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.expression.toString());
        sb.append("\n\t LeftV ariable: ").append((getLeftVariable() != null ? getLeftVariable().toLongString() : "null"));
        sb.append("\n\t Right Variable: ").append((getRightVariable()!= null ? getRightVariable().toLongString() : "null"));
        sb.append("\n\t Left Constant: ").append(leftConstant);
        sb.append("\n\t Right Constant: ").append(rightConstant);
        sb.append("\n\t Operator: ").append(operator);
        return sb.toString();
    }

}
