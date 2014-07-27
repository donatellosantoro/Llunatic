package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.ExpressionSyntaxException;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.expressions.Expression;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluateExpression {

    private static Logger logger = LoggerFactory.getLogger(EvaluateExpression.class);

    public Object evaluateFunction(Expression expression, Tuple tuple) throws ExpressionSyntaxException {
        if (logger.isDebugEnabled()) logger.debug("Evaluating function: " + expression + " on tuple " + tuple);
        setVariableValues(expression, tuple);
        Object value = expression.getJepExpression().getValueAsObject();
        if (expression.getJepExpression().hasError()) {
            throw new ExpressionSyntaxException(expression.getJepExpression().getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of function: " + value);
        return value;
    }

    public Double evaluateCondition(Expression expression, Tuple tuple) throws ExpressionSyntaxException {
        if (logger.isDebugEnabled()) logger.debug("Evaluating condition: " + expression + " on tuple " + tuple);
        if (expression.toString().equals("true")) {
            return LunaticConstants.TRUE;
        }
        setVariableValues(expression, tuple);
        Object value = expression.getJepExpression().getValueAsObject();
        if (expression.getJepExpression().hasError()) {
            throw new ExpressionSyntaxException(expression.getJepExpression().getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of condition: " + value);
        try {
            Double result = Double.parseDouble(value.toString());
            return result;
        } catch (NumberFormatException numberFormatException) {
            logger.error(numberFormatException.toString());
            throw new ExpressionSyntaxException(numberFormatException.getMessage());
        }
    }

    private void setVariableValues(Expression expression, Tuple tuple) {
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable jepVariable : symbolTable.getVariables()) {
            if (AlgebraUtility.isPlaceholder(jepVariable)) {
                continue;
            }
            Object variableDescription = jepVariable.getDescription();
            Object variableValue = findAttributeValue(tuple, variableDescription);
            assert (variableValue != null) : "Value of variable: " + jepVariable + " is null in tuple " + tuple;
            if (logger.isTraceEnabled()) logger.trace("Setting var value: " + jepVariable.getDescription() + " = " + variableValue);
            jepExpression.setVarValue(jepVariable.getName(), variableValue);
        }
    }

    private Object findAttributeValue(Tuple tuple, Object description) {
        if (logger.isTraceEnabled()) logger.trace("Searching variable: " + description + " in tuple " + tuple);
        AttributeRef attributeRef = null;
        if (description instanceof FormulaVariable) {
            FormulaVariable formulaVariable = (FormulaVariable) description;
            attributeRef = findOccurrenceInTuple(formulaVariable, tuple);
        } else if (description instanceof AttributeRef) {
            attributeRef = (AttributeRef) description;
        } else {
            throw new IllegalArgumentException("Illegal variable description in expression: " + description);
        }
        return AlgebraUtility.getCellValue(tuple, attributeRef).toString();
    }

    private AttributeRef findOccurrenceInTuple(FormulaVariable formulaVariable, Tuple tuple) {
        for (FormulaVariableOccurrence occurrence : formulaVariable.getPremiseOccurrences()) {
            AttributeRef attributeRef = occurrence.getAttributeRef();
            if (AlgebraUtility.contains(tuple, attributeRef)) {
                return attributeRef;
            }
        }
        throw new IllegalArgumentException("Unable to find values for variable " + formulaVariable.toLongString() + " in tuple " + tuple);
    }

}
