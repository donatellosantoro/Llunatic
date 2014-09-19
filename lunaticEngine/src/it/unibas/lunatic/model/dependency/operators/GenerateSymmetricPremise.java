package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.SymmetricAtoms;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateSymmetricPremise {

    private static Logger logger = LoggerFactory.getLogger(GenerateSymmetricPremise.class);

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();

    public PositiveFormula generateSymmetricPremise(Dependency dependency) {
        if (!dependency.hasSymmetricAtoms()) {
            throw new IllegalArgumentException("Dependency must have symmetric atoms: " + dependency);
        }
        if (logger.isDebugEnabled()) logger.debug("Generating symmetring premise for dependency " + dependency.toLongString());
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + dependency.getSymmetricAtoms());
        PositiveFormula premise = dependency.getPremise().getPositiveFormula();
        PositiveFormula symmetricPremise = new PositiveFormula(premise.getFather());
        Set<TableAlias> symmetricAtoms = dependency.getSymmetricAtoms().getSymmetricAliases();
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + symmetricAtoms);
        addSymmetricRelationalAtom(premise, symmetricAtoms, symmetricPremise);
        Map<FormulaVariable, FormulaVariable> substitutionMap = makeVariablesSymmetric(premise, symmetricPremise, dependency);
        addNonRelationalAtoms(premise, symmetricPremise, substitutionMap);
        removeUselessNonRelationalAtoms(symmetricPremise);
        equivalenceClassFinder.findVariableEquivalenceClasses(symmetricPremise);
        if (logger.isDebugEnabled()) logger.debug("Result: " + symmetricPremise.toLongString());
        return symmetricPremise;
    }

    private void addSymmetricRelationalAtom(PositiveFormula premise, Set<TableAlias> symmetricAtoms, PositiveFormula symmetricPremise) {
        for (IFormulaAtom atom : premise.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationAtom = ((RelationalAtom) atom).clone();
            if (logger.isDebugEnabled()) logger.debug("Analyzing atom: " + relationAtom);
            if (symmetricAtoms.contains(relationAtom.getTableAlias())) {
                if (logger.isDebugEnabled()) logger.debug("Atom must be made symmetric...");
                symmetricPremise.addAtom(makeSymmetricByRemovingAlias(relationAtom));
            }
        }
    }

    private RelationalAtom makeSymmetricByRemovingAlias(RelationalAtom atom) {
        RelationalAtom symmetricAtom = new RelationalAtom(atom.getTableName());
        for (FormulaAttribute attribute : atom.getAttributes()) {
            symmetricAtom.addAttribute(attribute.clone());
        }
//        symmetricAtom.setAlias("");
        symmetricAtom.setSource(atom.isSource());
        return symmetricAtom;
    }

    private Map<FormulaVariable, FormulaVariable> makeVariablesSymmetric(PositiveFormula premise, PositiveFormula symmetricPremise, Dependency dependency) {
        Map<FormulaVariable, FormulaVariable> substitutionMap = new HashMap<FormulaVariable, FormulaVariable>();
        for (FormulaVariable variable : premise.getLocalVariables()) {
            FormulaVariable newVariable = makeSymmetric(variable, dependency.getSymmetricAtoms());
            symmetricPremise.addLocalVariable(newVariable);
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

    private void addNonRelationalAtoms(PositiveFormula premise, PositiveFormula symmetricPremise, Map<FormulaVariable, FormulaVariable> substitutionMap) {
        for (IFormulaAtom atom : premise.getAtoms()) {
            if ((atom instanceof RelationalAtom)) {
                continue;
            }
            IFormulaAtom atomClone = atom.clone();
            for (int i = 0; i < atomClone.getVariables().size(); i++) {
                FormulaVariable variable = atomClone.getVariables().get(i);
                FormulaVariable variableClone = substitutionMap.get(variable);
                if (variableClone != null) {
                    atomClone.getVariables().set(i, variableClone);
                    variableClone.getNonRelationalOccurrences().remove(atom);
                    variableClone.getNonRelationalOccurrences().add(atomClone);
                    if(atomClone instanceof ComparisonAtom){
                        ComparisonAtom comparisonAtom = (ComparisonAtom)atomClone;
                        comparisonAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                    if(atomClone instanceof BuiltInAtom){
                        BuiltInAtom builtInAtom = (BuiltInAtom)atomClone;
                        builtInAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                    if (logger.isDebugEnabled()) logger.debug("Replacing variable in atom " + atom.toLongString() + "\n Old variable " + variable.toLongString() + " \nwith " + variableClone.toLongString());
                    if (logger.isDebugEnabled()) logger.debug("New atom " + atomClone.toLongString());
                }
            }
            symmetricPremise.addAtom(atomClone);
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
