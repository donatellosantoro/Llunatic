package it.unibas.lunatic.model.generators;

import it.unibas.lunatic.model.algebra.operators.EvaluateExpression;
import speedy.SpeedyConstants;
import speedy.model.database.IValue;
import speedy.model.database.ConstantValue;
import speedy.model.database.LLUNValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.expressions.Expression;
import speedy.utility.SpeedyUtility;

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
        Object result = evaluator.evaluateFunction(expression, sourceTuple);
        if (result == null || SpeedyUtility.isSkolem(result.toString())) {
            return new NullValue(result);
        }
        if (result.toString().startsWith(SpeedyConstants.LLUN_PREFIX)) {
            return new LLUNValue(result);
        }
        return new ConstantValue(result);
    }

    public String toString() {
        String result = "";
        result += "[" + this.expression + "]";
        return result;
    }

    public String toSQLString() {
        return toString();
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
