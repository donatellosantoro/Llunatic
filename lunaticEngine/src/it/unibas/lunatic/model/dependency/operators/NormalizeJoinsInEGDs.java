package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.FormulaWithNegations;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import speedy.model.expressions.Expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.StringComparator;

public class NormalizeJoinsInEGDs {

    private static final Logger logger = LoggerFactory.getLogger(NormalizeJoinsInEGDs.class.getName());

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();

//    public List<Dependency> normalizeJoinsInEgds(List<Dependency> egds) {
//        List<Dependency> normalizedEgds = new ArrayList<Dependency>();
//        for (Dependency egd : egds) {
//            normalizedEgds.add(normalizeJoinsInEgd(egd));
//        }
//        if (logger.isTraceEnabled()) logger.debug("Normalized egds: " + normalizedEgds);
//        return normalizedEgds;
//    }

    public Dependency normalizeJoinsInEgd(Dependency egd) {
        if (!hasExplicitComparisons(egd)) {
            return egd;
        }
        Dependency clone = egd.clone();
        Map<FormulaVariable, FormulaVariable> variableSubstitutions = generateSubstitutions(clone);
        NormalizeJoinsInEGDVisitor visitor = new NormalizeJoinsInEGDVisitor(variableSubstitutions);
        clone.accept(visitor);
        if (logger.isDebugEnabled()) logger.debug("Initial egd: " + egd.toLongString());
        if (logger.isDebugEnabled()) logger.debug("Normalized egd: " + clone.toLongString());
        //equivalenceClassFinder.findVariableEquivalenceClasses(clone);
        return clone;
    }

    private boolean hasExplicitComparisons(Dependency egd) {
        if (logger.isTraceEnabled()) logger.debug("Checking explicit comparisons on: " + egd);
        List<VariableEquivalenceClass> variableEquivalenceClasses = egd.getPremise().getLocalVariableEquivalenceClasses();
        if (logger.isTraceEnabled()) logger.debug("Variable equivalence classes: " + variableEquivalenceClasses);
        for (VariableEquivalenceClass equivalenceClass : variableEquivalenceClasses) {
            if (equivalenceClass.getVariables().size() > 1) {
                if (logger.isTraceEnabled()) logger.debug("Found equivalence class: " + equivalenceClass);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<FormulaVariable, FormulaVariable> generateSubstitutions(Dependency egd) {
        Map<FormulaVariable, FormulaVariable> result = new HashMap<FormulaVariable, FormulaVariable>();
        for (VariableEquivalenceClass variableEquivalenceClass : egd.getPremise().getLocalVariableEquivalenceClasses()) {
            if (variableEquivalenceClass.getVariables().size() == 1) {
                continue;
            }
            List<FormulaVariable> equivalenceClassVariables = variableEquivalenceClass.getVariables();
            Collections.sort(equivalenceClassVariables, new StringComparator());
            FormulaVariable representative = equivalenceClassVariables.get(0);
            for (int i = 1; i < equivalenceClassVariables.size(); i++) {
                FormulaVariable variable = equivalenceClassVariables.get(i);
                result.put(variable, representative);
            }
        }
        return result;
    }

}

class NormalizeJoinsInEGDVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(NormalizeJoinsInEGDVisitor.class);

    private Map<FormulaVariable, FormulaVariable> variableSubstitutions;

    public NormalizeJoinsInEGDVisitor(Map<FormulaVariable, FormulaVariable> variableSubstitutions) {
        this.variableSubstitutions = variableSubstitutions;
    }

    public void visitDependency(Dependency egd) {
        egd.getPremise().accept(this);
        egd.getConclusion().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        removeReplacedVariables(formula);
        mergeVariableOccurrencesAndRemoveComparisons(formula);
        changeRelationalAtoms(formula);
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    private void removeReplacedVariables(PositiveFormula formula) {
        for (Iterator<FormulaVariable> iterator = formula.getLocalVariables().iterator(); iterator.hasNext();) {
            FormulaVariable variable = iterator.next();
            if (variableSubstitutions.containsKey(variable)) {
                iterator.remove();
            }
        }
    }

    private void mergeVariableOccurrencesAndRemoveComparisons(PositiveFormula formula) {
        Set<IFormulaAtom> atomsToRemove = new HashSet<IFormulaAtom>();
        for (FormulaVariable variable : formula.getLocalVariables()) {
            List<FormulaVariable> variablesToReplace = findVariablesToReplaceForThisVariable(variable);
            for (FormulaVariable variableToReplace : variablesToReplace) {
                mergeRelationalOccurrences(variable, variableToReplace);
                Set<IFormulaAtom> atomsToRemoveForVariable = changeNonRelationalOccurrencesAndFindAtomsToRemove(variable, variableToReplace);
                atomsToRemove.addAll(atomsToRemoveForVariable);
            }
        }
        formula.getAtoms().removeAll(atomsToRemove);
    }

    private List<FormulaVariable> findVariablesToReplaceForThisVariable(FormulaVariable variable) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable variableToReplace : variableSubstitutions.keySet()) {
            if (variableSubstitutions.get(variableToReplace).equals(variable)) {
                result.add(variableToReplace);
            }
        }
        return result;
    }

    private void mergeRelationalOccurrences(FormulaVariable variable, FormulaVariable variableToReplace) {
        for (FormulaVariableOccurrence variableOccurrence : variableToReplace.getPremiseRelationalOccurrences()) {
            variableOccurrence.setVariableId(variable.getId());
            variable.addPremiseRelationalOccurrence(variableOccurrence);
        }
        for (FormulaVariableOccurrence variableOccurrence : variableToReplace.getConclusionRelationalOccurrences()) {
            variableOccurrence.setVariableId(variable.getId());
            variable.addConclusionRelationalOccurrence(variableOccurrence);
        }
    }

    private Set<IFormulaAtom> changeNonRelationalOccurrencesAndFindAtomsToRemove(FormulaVariable variable, FormulaVariable variableToReplace) {
        Set<IFormulaAtom> atomsToRemove = new HashSet<IFormulaAtom>();
        for (IFormulaAtom atom : variableToReplace.getNonRelationalOccurrences()) {
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                changeVariablesInAtom(comparisonAtom, variable, variableToReplace);
                if (isIdentity(comparisonAtom)) {
                    atomsToRemove.add(atom);
                    continue;
                }
                correctExpression(comparisonAtom, variable, variableToReplace);
            }
            if (atom instanceof BuiltInAtom) {
                BuiltInAtom builtInAtom = (BuiltInAtom) atom;
                changeVariablesInAtom(builtInAtom, variable, variableToReplace);
                correctExpression(builtInAtom, variable, variableToReplace);
            }
        }
        return atomsToRemove;
    }

    private void changeVariablesInAtom(IFormulaAtom comparisonAtom, FormulaVariable variable, FormulaVariable variableToReplace) {
        for (int i = 0; i < comparisonAtom.getVariables().size(); i++) {
            FormulaVariable variableInAtom = comparisonAtom.getVariables().get(i);
            if (variableInAtom.equals(variableToReplace)) {
                comparisonAtom.getVariables().set(i, variable);
            }
        }
    }

    private boolean isIdentity(ComparisonAtom comparisonAtom) {
        return (comparisonAtom.isVariableEqualityComparison() && comparisonAtom.getVariables().get(0).equals(comparisonAtom.getVariables().get(1)));
    }

    private void correctExpression(IFormulaAtom atom, FormulaVariable variable, FormulaVariable variableToReplace) {
        String expressionWithDelimiters = atom.getExpression().toVariableDelimitedString();
//        String newExpressionString = expressionWithDelimiters.replaceAll("§" + variableToReplace.getId() + "#", "\\$" + variable.getId());
        String newExpressionString = expressionWithDelimiters.replaceAll("§" + variableToReplace.getId() + "#", variable.getId());
        newExpressionString = newExpressionString.replaceAll("§", "");
        newExpressionString = newExpressionString.replaceAll("#", "");
        Expression newExpression = new Expression(newExpressionString);
        atom.setExpression(newExpression);
        for (FormulaVariable variableInAtom : atom.getVariables()) {
            newExpression.setVariableDescription(variableInAtom.getId(), variableInAtom);
        }
    }

    private void changeRelationalAtoms(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
                if (!formulaAttribute.getValue().isVariable()) {
                    continue;
                }
                FormulaVariableOccurrence variableOccurrence = (FormulaVariableOccurrence) formulaAttribute.getValue();
                FormulaVariable newVariable = findNewVariable(variableOccurrence);
                if (newVariable != null) {
                    variableOccurrence.setVariableId(newVariable.getId());
                }
            }
        }
    }

    private FormulaVariable findNewVariable(FormulaVariableOccurrence variableOccurrence) {
        for (FormulaVariable variableToReplace : variableSubstitutions.keySet()) {
            if (variableToReplace.getId().equals(variableOccurrence.getVariableId())) {
                return variableSubstitutions.get(variableToReplace);
            }
        }
        return null;
    }

    public Object getResult() {
        return null;
    }
}
