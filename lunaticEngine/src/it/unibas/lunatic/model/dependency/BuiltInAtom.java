package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public class BuiltInAtom implements IFormulaAtom {
    
    private IFormula formula;
    private Expression expression;
    private List<FormulaVariable> variables = new ArrayList<FormulaVariable>();

    public BuiltInAtom(IFormula formula, Expression expression) {
        this.formula = formula;
        this.expression = expression;
    }

    public IFormula getFormula() {
        return formula;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }
    
    public Expression getExpression() {
        return expression;
    }
   
    public void addVariable(FormulaVariable variable) {
        this.variables.add(variable);
    }

    public List<FormulaVariable> getVariables() {
        return this.variables;
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public String toLongString() {
        return this.expression.toString() + "\n\tvariables=" + variables;
    }
    
}
