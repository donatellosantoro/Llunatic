package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneFormulaVisitor implements IFormulaVisitor {
    
    private static final Logger logger = LoggerFactory.getLogger(CloneFormulaVisitor.class.getName());

    private Map<FormulaVariable, FormulaVariable> variableClones = new HashMap<FormulaVariable, FormulaVariable>();
    private IFormula result;
    private IFormula lastFormulaWithNegations;

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        FormulaWithNegations newFormula = new FormulaWithNegations();
        if (result == null) {
            result = newFormula;
            lastFormulaWithNegations = result;
        } else {
            newFormula.setFather(lastFormulaWithNegations);
            lastFormulaWithNegations.addNegatedFormula(newFormula);
            lastFormulaWithNegations = newFormula;
        }
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
            lastFormulaWithNegations = newFormula;
        }

    }

    public void visitPositiveFormula(PositiveFormula formula) {
        PositiveFormula clone = formula.superficialClone();
        if (result == null) {
            result = clone;
        } else {
            lastFormulaWithNegations.setPositiveFormula(clone);
        }
        cloneVariables(formula, clone);
        Map<IFormulaAtom, IFormulaAtom> atomClones = cloneAtoms(formula, clone);
        changeVariablesInAtoms(clone, variableClones);
        changeAtomsInVariables(clone, atomClones);
        cloneEquivalenceClasses(clone, variableClones);
    }

    private void cloneVariables(PositiveFormula formula, PositiveFormula formulaClone) {
        formulaClone.setLocalVariables(new ArrayList<FormulaVariable>());
        for (FormulaVariable variable : formula.getLocalVariables()) {
            FormulaVariable variableClone = variable.clone();
            formulaClone.getLocalVariables().add(variableClone);
            variableClones.put(variable, variableClone);
        }
    }

    private Map<IFormulaAtom, IFormulaAtom> cloneAtoms(PositiveFormula formula, PositiveFormula formulaClone) {
        formulaClone.setAtoms(new ArrayList<IFormulaAtom>());
        Map<IFormulaAtom, IFormulaAtom> atomClones = new HashMap<IFormulaAtom, IFormulaAtom>();
        for (IFormulaAtom atom : formula.getAtoms()) {
            IFormulaAtom atomClone = atom.clone();
            atomClone.setFormula(formulaClone);
            formulaClone.getAtoms().add(atomClone);
            atomClones.put(atom, atomClone);
        }
        return atomClones;
    }

    private void changeVariablesInAtoms(PositiveFormula clone, Map<FormulaVariable, FormulaVariable> variableClones) {
        for (IFormulaAtom atomClone : clone.getAtoms()) {
            if (atomClone instanceof RelationalAtom) {
                continue;
            }
            for (int i = 0; i < atomClone.getVariables().size(); i++) {
                FormulaVariable variable = atomClone.getVariables().get(i);
                FormulaVariable variableClone = variableClones.get(variable);
                atomClone.getVariables().set(i, variableClone);
                if (atomClone instanceof ComparisonAtom) {
                    ComparisonAtom comparisonAtom = (ComparisonAtom) atomClone;
                    comparisonAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                }
                if (atomClone instanceof BuiltInAtom) {
                    BuiltInAtom builtIn = (BuiltInAtom) atomClone;
                    builtIn.getExpression().setVariableDescription(variable.getId(), variableClone);
                }
            }
        }
    }

    private void changeAtomsInVariables(PositiveFormula clone, Map<IFormulaAtom, IFormulaAtom> atomClones) {
        for (FormulaVariable variableClone : clone.getLocalVariables()) {
            for (int i = 0; i < variableClone.getNonRelationalOccurrences().size(); i++) {
                IFormulaAtom atom = variableClone.getNonRelationalOccurrences().get(i);
                IFormulaAtom atomClone = atomClones.get(atom);
                variableClone.getNonRelationalOccurrences().set(i, atomClone);
            }
        }
    }

    private void cloneEquivalenceClasses(PositiveFormula clone, Map<FormulaVariable, FormulaVariable> variableClones) {
        List<VariableEquivalenceClass> equivalenceClasses = clone.getLocalVariableEquivalenceClasses();
        clone.setLocalVariableEquivalenceClasses(new ArrayList<VariableEquivalenceClass>());
        for (int i = 0; i < equivalenceClasses.size(); i++) {
            VariableEquivalenceClass equivalenceClass = equivalenceClasses.get(i);
            VariableEquivalenceClass equivalenceClassClone = new VariableEquivalenceClass();
            for (FormulaVariable variable : equivalenceClass.getVariables()) {
                equivalenceClassClone.addVariable(variableClones.get(variable));
            }
            clone.getLocalVariableEquivalenceClasses().add(equivalenceClassClone);
        }
    }

    public Map<FormulaVariable, FormulaVariable> getVariableClones() {
        return variableClones;
    }

    public IFormula getResult() {
        return result;
    }    

    public void reset() {
        this.result = null;
        this.lastFormulaWithNegations = null;
    }

    public void visitDependency(Dependency dependency) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
