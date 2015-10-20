package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.SymmetricAtoms;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;

public class GenerateSymmetricPremise {

    private static Logger logger = LoggerFactory.getLogger(GenerateSymmetricPremise.class);

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();

    public PositiveFormula generateSymmetricPremise(Dependency dependency) {
        if (!dependency.hasSymmetricChase()) {
            throw new IllegalArgumentException("Dependency must have symmetric atoms: " + dependency);
        }
        if (logger.isDebugEnabled()) logger.debug("Generating symmetring premise for dependency " + dependency.toLongString());
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + dependency.getSymmetricAtoms());
        PositiveFormula premise = dependency.getPremise().getPositiveFormula();
//        PositiveFormula symmetricPremise = new PositiveFormula(premise.getFather());
        PositiveFormula symmetricPremise = premise.clone();
        Set<TableAlias> symmetricAtoms = dependency.getSymmetricAtoms().getSymmetricAliases();
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + symmetricAtoms);
        changeRelationalAtoms(symmetricPremise, symmetricAtoms);
        Map<FormulaVariable, FormulaVariable> substitutionMap = makeVariablesSymmetric(symmetricPremise, dependency);
        changeNonRelationalAtoms(symmetricPremise, substitutionMap);
        removeUselessNonRelationalAtoms(symmetricPremise);
        equivalenceClassFinder.findVariableEquivalenceClasses(symmetricPremise);
        if (logger.isDebugEnabled()) logger.debug("Result: " + symmetricPremise.toLongString());
        return symmetricPremise;
    }

    private void changeRelationalAtoms(PositiveFormula symmetricPremise, Set<TableAlias> symmetricAtoms) {
        for (Iterator<IFormulaAtom> iterator = symmetricPremise.getAtoms().iterator(); iterator.hasNext();) {
            IFormulaAtom atom = iterator.next();
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationAtom = (RelationalAtom) atom;
            if (logger.isDebugEnabled()) logger.debug("Analyzing atom: " + relationAtom);
            if (symmetricAtoms.contains(relationAtom.getTableAlias())) {
                if (logger.isDebugEnabled()) logger.debug("Atom must be made symmetric...");
                relationAtom.setAlias("");
//                makeSymmetricByRemovingAlias(relationAtom);
            } else {
                iterator.remove();
            }
        }
    }

//    private void makeSymmetricByRemovingAlias(RelationalAtom atom) {
//        atom.setAlias("");
//    }
    private Map<FormulaVariable, FormulaVariable> makeVariablesSymmetric(PositiveFormula symmetricPremise, Dependency dependency) {
        Map<FormulaVariable, FormulaVariable> substitutionMap = new HashMap<FormulaVariable, FormulaVariable>();
        for (int i = 0; i < symmetricPremise.getLocalVariables().size(); i++) {
            FormulaVariable variable = symmetricPremise.getLocalVariables().get(i);
            FormulaVariable newVariable = makeSymmetric(variable, dependency.getSymmetricAtoms());
            symmetricPremise.getLocalVariables().set(i, newVariable);
            substitutionMap.put(variable, newVariable);
        }
        return substitutionMap;
    }

    private FormulaVariable makeSymmetric(FormulaVariable variable, SymmetricAtoms symmetricAtoms) {
        if (logger.isDebugEnabled()) logger.debug("Making symmetric variable: " + variable + " with symmetric atoms: " + symmetricAtoms);
        FormulaVariable symmetricVariable = new FormulaVariable(variable.getId());
//        symmetricVariable.setNonRelationalOccurrences(variable.getNonRelationalOccurrences());
        for (FormulaVariableOccurrence premiseOccurrence : variable.getPremiseRelationalOccurrences()) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing occurrence: " + premiseOccurrence.toLongString());
            if (appearsInSymmetricAtom(premiseOccurrence.getTableAlias(), symmetricAtoms)) {
                AttributeRef symmetricAttribute = premiseOccurrence.getAttributeRef().clone();
                symmetricAttribute.getTableAlias().setAlias("");
                FormulaVariableOccurrence symmetricOccurrence = new FormulaVariableOccurrence(symmetricAttribute, premiseOccurrence.getVariableId());
                if (logger.isDebugEnabled()) logger.debug("Symmetric occurrence: " + symmetricOccurrence.toLongString());
                symmetricVariable.addPremiseRelationalOccurrence(symmetricOccurrence);
            }
//            if (premiseOccurrence.getTableAlias().getTableName().equals(symmetricTable)) {
//                AttributeRef symmetricAttribute = new AttributeRef(symmetricTable, premiseOccurrence.getAttributeRef().getName());
//                FormulaVariableOccurrence symmetricOccurrence = new FormulaVariableOccurrence(symmetricAttribute, premiseOccurrence.getVariableId());
//                symmetricVariable.addPremiseOccurrence(symmetricOccurrence);
//            } else {
//                symmetricVariable.addPremiseOccurrence(premiseOccurrence);
//            }
        }
        return symmetricVariable;
    }

    private boolean appearsInSymmetricAtom(TableAlias tableAlias, SymmetricAtoms symmetricAtoms) {
        return symmetricAtoms.getSymmetricAliases().contains(tableAlias);
    }

    private void changeNonRelationalAtoms(PositiveFormula symmetricPremise, Map<FormulaVariable, FormulaVariable> substitutionMap) {
        for (Iterator<IFormulaAtom> iterator = symmetricPremise.getAtoms().iterator(); iterator.hasNext();) {
            IFormulaAtom atom = iterator.next();
            if ((atom instanceof RelationalAtom)) {
                continue;
            }
            for (int i = 0; i < atom.getVariables().size(); i++) {
                FormulaVariable variable = atom.getVariables().get(i);
                FormulaVariable variableClone = substitutionMap.get(variable);
                if (variableClone != null) {
                    atom.getVariables().set(i, variableClone);
                    if (atom instanceof ComparisonAtom) {
                        ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                        comparisonAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                    if (atom instanceof BuiltInAtom) {
                        BuiltInAtom builtInAtom = (BuiltInAtom) atom;
                        builtInAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                    if (logger.isDebugEnabled()) logger.debug("Replacing variable in atom " + atom.toLongString() + "\n Old variable " + variable.toLongString() + " \nwith " + variableClone.toLongString());
                    if (logger.isDebugEnabled()) logger.debug("New atom " + atom.toLongString());
                }
            }
        }
    }

    private void removeUselessNonRelationalAtoms(PositiveFormula symmetricPremise) {
        for (Iterator<IFormulaAtom> it = symmetricPremise.getAtoms().iterator(); it.hasNext();) {
            IFormulaAtom atom = it.next();
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (!comparisonAtom.isVariableEqualityComparison()) {
                continue;
            }
            FormulaVariable leftVariable = comparisonAtom.getLeftVariable();
            FormulaVariable rightVariable = comparisonAtom.getRightVariable();
            if (logger.isDebugEnabled()) logger.debug("Checking atom " + comparisonAtom.toLongString());
            if (leftVariable.getPremiseRelationalOccurrences().isEmpty()
                    || rightVariable.getPremiseRelationalOccurrences().isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Removing atom without premise occurrences...");
                removeOccurrencesInVariable(comparisonAtom);
                it.remove();
            } else if (leftVariable.getPremiseRelationalOccurrences().equals(rightVariable.getPremiseRelationalOccurrences())
                    && leftVariable.getConclusionRelationalOccurrences().equals(rightVariable.getConclusionRelationalOccurrences())) {
                if (logger.isDebugEnabled()) logger.debug("Removing atom...");
                removeOccurrencesInVariable(comparisonAtom);
                it.remove();
            }
        }
    }

    private void removeOccurrencesInVariable(ComparisonAtom comparisonAtom) {
        comparisonAtom.getLeftVariable().getNonRelationalOccurrences().remove(comparisonAtom);
        comparisonAtom.getRightVariable().getNonRelationalOccurrences().remove(comparisonAtom);
    }

}
