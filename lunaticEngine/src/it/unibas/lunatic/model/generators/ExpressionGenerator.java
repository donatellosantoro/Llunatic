package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.algebra.operators.EvaluateExpression;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.expressions.Expression;

public class ExpressionGenerator implements IValueGenerator {

    protected Expression expression;

    public ExpressionGenerator(Expression function) {
        this.expression = function.clone();
    }

    public Expression getExpression() {
        return expression;
    }

    public IValue generateValue(Tuple sourceTuple) {
        EvaluateExpression evaluator = new EvaluateExpression();
        return new ConstantValue(evaluator.evaluateFunction(expression, sourceTuple));
    }

    public String toString() {
        String result = "";
        result += "[" + this.expression + "]";
        return result;
    }

    @Override
    public IValueGenerator clone() {
        ExpressionGenerator clone = null;
        try {
            clone = (ExpressionGenerator) super.clone();
            clone.expression = this.expression.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }
}
