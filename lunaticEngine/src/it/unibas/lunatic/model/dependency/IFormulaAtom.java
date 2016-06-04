package it.unibas.lunatic.model.dependency;

import java.util.List;
import speedy.model.expressions.Expression;

public interface IFormulaAtom extends Cloneable {

    public void addVariable(FormulaVariable variable);

    public List<FormulaVariable> getVariables();

    public Expression getExpression();
    
    public void setExpression(Expression expression);

    public IFormula getFormula();

    public void setFormula(IFormula formula);

    public String toLongString();
    
    public String toSaveString();
    
    public String toCFString();

    // atoms are superficially cloned; see PositiveFormula.clone() for deop cloning
    public IFormulaAtom clone();
}
