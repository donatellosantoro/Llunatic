package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormulaWithNegations implements IFormula {

    private IFormula father;
    private PositiveFormula positiveFormula;
    private List<IFormula> negatedSubFormulas = new ArrayList<IFormula>();

    public FormulaWithNegations() {
    }

    public FormulaWithNegations(PositiveFormula positiveFormula) {
        this.positiveFormula = positiveFormula;
    }

    public FormulaWithNegations(IFormula father, PositiveFormula positiveFormula) {
        this.father = father;
        this.positiveFormula = positiveFormula;
    }

    public IFormula getFather() {
        return father;
    }

    public void setFather(IFormula father) {
        this.father = father;
    }

    public PositiveFormula getPositiveFormula() {
        return positiveFormula;
    }

    public void setPositiveFormula(PositiveFormula formula) {
        this.positiveFormula = formula;
    }

    public List<IFormulaAtom> getAtoms() {
        return this.positiveFormula.getAtoms();
    }

    public void addAtom(IFormulaAtom a) {
        this.positiveFormula.addAtom(a);
    }

    public List<IFormula> getNegatedSubFormulas() {
        return negatedSubFormulas;
    }

    public void addNegatedFormula(IFormula formula) {
        this.negatedSubFormulas.add(formula);
    }

    public List<FormulaVariable> getLocalVariables() {
        return this.positiveFormula.getLocalVariables();
    }

    @SuppressWarnings("unchecked")
    public List<FormulaVariable> getAllVariables() {
        if (father == null) {
            return Collections.EMPTY_LIST;
        }
        return father.getPositiveFormula().getAllVariables();
    }

    public void accept(IFormulaVisitor visitor) {
        visitor.visitFormulaWithNegations(this);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public FormulaWithNegations clone() {
        FormulaWithNegations clone = null;
        try {
            clone = (FormulaWithNegations) super.clone();
//            if (this.father != null) {
//                clone.father = this.father.clone();
//            } //Cyclic clone
            clone.positiveFormula = this.positiveFormula.clone();
            clone.negatedSubFormulas = new ArrayList<IFormula>();
            for (IFormula subFormula : this.negatedSubFormulas) {
                IFormula cloneSubFormula = subFormula.clone();
                cloneSubFormula.setFather(clone);
                clone.negatedSubFormulas.add(cloneSubFormula);
            }
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }
}
