package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindVariableEquivalenceClasses {

    public void findVariableEquivalenceClasses(IFormula formula) {
        FindVariableEquivalenceClassesVisitor visitor = new FindVariableEquivalenceClassesVisitor();
        formula.accept(visitor);
    }

    public void findVariableEquivalenceClasses(Dependency dependency) {
        FindVariableEquivalenceClassesVisitor visitor = new FindVariableEquivalenceClassesVisitor();
        dependency.accept(visitor);
    }
}

class FindVariableEquivalenceClassesVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindFormulaVariablesVisitor.class);

    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        generateVariableEquivalenceClasses(formula);
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }

    List<VariableEquivalenceClass> generateVariableEquivalenceClasses(IFormula formula) {
        List<FormulaVariable> variables = formula.getLocalVariables();
        List<VariableEquivalenceClass> result = initiEquivalenceClasses(variables);
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (!comparisonAtom.isVariableEqualityComparison() || !isLocalComparison(comparisonAtom, variables)) {
                continue;
            }
            mergeClasses(comparisonAtom, result);
        }
        return result;
    }

    private List<VariableEquivalenceClass> initiEquivalenceClasses(List<FormulaVariable> variables) {
        List<VariableEquivalenceClass> result = new ArrayList<VariableEquivalenceClass>();
        for (FormulaVariable variable : variables) {
            VariableEquivalenceClass newClass = new VariableEquivalenceClass();
            newClass.addVariable(variable);
            result.add(newClass);
        }
        return result;
    }

    private boolean isLocalComparison(ComparisonAtom comparisonAtom, List<FormulaVariable> localVariables) {
        return localVariables.contains(comparisonAtom.getLeftVariable()) && localVariables.contains(comparisonAtom.getRightVariable());
    }

    private void mergeClasses(ComparisonAtom comparisonAtom, List<VariableEquivalenceClass> equivalenceClasses) {
        VariableEquivalenceClass firstClass = findEquivalenceClassForVariable(comparisonAtom.getLeftVariable(), equivalenceClasses);
        VariableEquivalenceClass secondClass = findEquivalenceClassForVariable(comparisonAtom.getRightVariable(), equivalenceClasses);
        equivalenceClasses.remove(secondClass);
        firstClass.addVariables(secondClass.getVariables());
    }

    private VariableEquivalenceClass findEquivalenceClassForVariable(FormulaVariable variable, List<VariableEquivalenceClass> equivalenceClasses) {
        for (VariableEquivalenceClass equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.contains(variable)) {
                return equivalenceClass;
            }
        }
        throw new IllegalArgumentException("Unable to find equivalence class for variable " + variable + "\n" + equivalenceClasses);
    }

}
