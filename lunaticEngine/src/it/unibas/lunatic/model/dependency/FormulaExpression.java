package it.unibas.lunatic.model.dependency;

import speedy.model.expressions.Expression;

public class FormulaExpression implements IFormulaValue {

    private Expression expression;

    public FormulaExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isVariable() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public IFormulaValue clone() {
        try {
            FormulaExpression c = (FormulaExpression) super.clone();
            c.expression = this.expression.clone();
            return c;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone FormulaValue " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public String toLongString() {
        return "Expression : " + expression.toString();
    }
}
