package it.unibas.lunatic.model.expressions;

import it.unibas.lunatic.exceptions.ExpressionSyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.nfunk.jep.IExpressionVisitor;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Expression implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(Expression.class);
    
    private JEP jepExpression;

    public Expression(String expression) throws ExpressionSyntaxException {
        jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression(expression);
        if (jepExpression.hasError()) {
            throw new ExpressionSyntaxException(jepExpression.getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Created expression: " + jepExpression);
    }

    public JEP getJepExpression() {
        return jepExpression;
    }

    public void accept(IExpressionVisitor visitor) {
        if (this.jepExpression != null) {
            jepExpression.getTopNode().accept(visitor);
        }
    }

    public List<String> getVariables() {
        List<String> result = new ArrayList<String>();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            String variableDescription = variable.getDescription().toString();
            if (!result.contains(variableDescription)) {
                result.add(variableDescription);
            }
        }
        return result;
    }
    
    public List<Object> getConstants() {
        return jepExpression.getSymbolTable().getConstants();
    }
    
    public void setVariableDescription(String variable, Object description) {
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variableInExpression : symbolTable.getVariables()) {
            if (variableInExpression.getDescription().equals(variable)) {
                variableInExpression.setDescription(description);
            }
        }
    }
    
    public void setVariableValue(String variable, Object value) {
        jepExpression.setVarValue(variable, value);
    }

    public Object getValue() throws ExpressionSyntaxException {
        Object value = jepExpression.getValueAsObject();
        if (jepExpression.hasError()) {
            throw new ExpressionSyntaxException(jepExpression.getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of function: " + value);
        return value;
    }
   
    @Override
    public Expression clone() {
        Expression clone = null;
        try {
            clone = (Expression) super.clone();
            clone.jepExpression = (JEP) this.jepExpression.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }

    @Override
    public String toString() {
        return this.jepExpression.toString();
    }
    
    public String toSQLString() {
        return this.jepExpression.toSQLString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }

    public boolean equalsUpToVariableIds(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }
    private static Expression trueExpression = new Expression("true");

    public static Expression getTrueExpression() {
        return trueExpression;
    }
}
