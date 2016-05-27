package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import java.util.List;

public interface IFormula extends Cloneable{
    
    public IFormula getFather();
    public void setFather(IFormula father);
    public PositiveFormula getPositiveFormula();
    public void setPositiveFormula(PositiveFormula formula);
    public List<IFormula> getNegatedSubFormulas();
    public void addNegatedFormula(IFormula formula);
    public List<IFormulaAtom> getAtoms();
    public void addAtom(IFormulaAtom a);
    public List<FormulaVariable> getLocalVariables();
    public List<VariableEquivalenceClass> getLocalVariableEquivalenceClasses();
    public List<FormulaVariable> getAllVariables();
    public void accept(IFormulaVisitor visitor);
    public IFormula clone();
    public String toSaveString();
    
}
