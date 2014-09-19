package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.expressions.Expression;
import java.util.List;

public interface IFormulaAtom extends Cloneable{
    
    public void addVariable(FormulaVariable variable);
    public List<FormulaVariable> getVariables();
    public Expression getExpression();
    public IFormula getFormula();
    public void setFormula(IFormula formula);
    public String toLongString();
    public IFormulaAtom clone();
}
