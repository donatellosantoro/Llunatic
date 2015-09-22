package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;
import speedy.model.expressions.Expression;

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

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void addVariable(FormulaVariable variable) {
        this.variables.add(variable);
    }

    public List<FormulaVariable> getVariables() {
        return this.variables;
    }

    public IFormulaAtom clone() {
        // atoms are superficially cloned; see PositiveFormula.clone() for deep cloning
        try {
            BuiltInAtom clone = (BuiltInAtom) super.clone();
            clone.expression = this.expression.clone();
            clone.variables = new ArrayList<FormulaVariable>(this.variables);
//            for (FormulaVariable variable : this.variables) {
//                clone.variables.add(variable.clone());
//            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public String toLongString() {
        return this.expression.toString() + "\n\tvariables=" + variables;
    }

}
