package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.PositiveFormula;

public interface IFormulaVisitor {

    public void visitDependency(Dependency dependency);
    public void visitPositiveFormula(PositiveFormula formula);
    public void visitFormulaWithNegations(FormulaWithNegations formula);
    public Object getResult();
    
}
