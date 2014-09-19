package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.extendedegdanalysis.SymmetricAtoms;
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
        if (logger.isDebugEnabled()) logger.debug("Generating symmetring premise for dependency " + dependency);
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + dependency.getSymmetricAtoms());
        PositiveFormula premise = dependency.getPremise().getPositiveFormula();
        PositiveFormula symmetricPremise = new PositiveFormula(premise.getFather());
        Set<TableAlias> symmetricAtoms = dependency.getSymmetricAtoms().getSymmetricAliases();
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + symmetricAtoms);
        for (IFormulaAtom atom : premise.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                symmetricPremise.addAtom(atom);
                continue;
            }
//            RelationalAtom relationAtom = (RelationalAtom) atom;
            RelationalAtom relationAtom = ((RelationalAtom) atom).clone();
            if (logger.isDebugEnabled()) logger.debug("Analyzing atom: " + relationAtom);
            if (symmetricAtoms.contains(relationAtom.getTableAlias())) {
                if (logger.isDebugEnabled()) logger.debug("Atom must be made symmetric...");
                symmetricPremise.addAtom(makeSymmetric(relationAtom));
            }
        }
//        String symmetricTable = dependency.getTableNameForSymmetricAtom();
        for (FormulaVariable variable : premise.getLocalVariables()) {
            symmetricPremise.addLocalVariable(makeSymmetric(variable, dependency.getSymmetricAtoms()));
        }
        equivalenceClassFinder.findVariableEquivalenceClasses(symmetricPremise);
        if (logger.isDebugEnabled()) logger.debug("Result: " + symmetricPremise);
        return symmetricPremise;
    }

    private RelationalAtom makeSymmetric(RelationalAtom atom) {
        RelationalAtom symmetricAtom = new RelationalAtom(atom.getTableName());
        for (FormulaAttribute attribute : atom.getAttributes()) {
            symmetricAtom.addAttribute(attribute.clone());
        }
//        symmetricAtom.setAlias("");
        symmetricAtom.setSource(atom.isSource());
        return symmetricAtom;
    }

    private FormulaVariable makeSymmetric(FormulaVariable variable, SymmetricAtoms symmetricAtoms) {
        if (logger.isDebugEnabled()) logger.debug("Making symmetric variable: " + variable + " with symmetric atoms: " + symmetricAtoms);
        FormulaVariable symmetricVariable = new FormulaVariable(variable.getId());
//        symmetricVariable.setNonRelationalOccurrences(variable.getNonRelationalOccurrences());
        for (FormulaVariableOccurrence premiseOccurrence : variable.getPremiseRelationalOccurrences()) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing occurrence: " + premiseOccurrence);
            if (appearsInSymmetricAtom(premiseOccurrence.getTableAlias(), symmetricAtoms)) {
                AttributeRef symmetricAttribute = premiseOccurrence.getAttributeRef().clone();
                symmetricAttribute.getTableAlias().setAlias("");
                FormulaVariableOccurrence symmetricOccurrence = new FormulaVariableOccurrence(symmetricAttribute, premiseOccurrence.getVariableId());
                if (logger.isDebugEnabled()) logger.debug("Symmetric occurrence: " + symmetricOccurrence);
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
}
