package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.utility.graph.DualGaifmanGraph;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalizeConclusionsInEGDs {

    private static Logger logger = LoggerFactory.getLogger(NormalizeConclusionsInEGDs.class);

    private DualGaifmanGraph dualGaifmanGraphGenerator = new DualGaifmanGraph();

    public List<Dependency> normalizeEGDs(List<Dependency> eTGDs) {
        List<Dependency> normalizedTgds = new ArrayList<Dependency>();
        for (Dependency eTGD : eTGDs) {
            normalizedTgds.addAll(normalizeEGD(eTGD));
        }
        return normalizedTgds;
    }

    public List<Dependency> normalizeEGD(Dependency egd) {
        if (logger.isDebugEnabled()) logger.debug("Analyzing egd: " + egd);
        List<Dependency> normalizedEgds = new ArrayList<Dependency>();
        if (egd.getConclusion().getAtoms().size() == 1) {
            if (logger.isDebugEnabled()) logger.debug("Egd has single target variable, adding...");
            normalizedEgds.add(egd);
            return normalizedEgds;
        }
        if (logger.isDebugEnabled()) logger.debug("Egd is not normalized...");
        for (int i = 0; i < egd.getConclusion().getAtoms().size(); i++) {
            ComparisonAtom atom = (ComparisonAtom) egd.getConclusion().getAtoms().get(i);
            Set<IFormulaAtom> atomsToRemove = new HashSet<IFormulaAtom>(egd.getConclusion().getAtoms());
            atomsToRemove.remove(atom);
            String suffixId = "_norm_" + (i + 1);
            normalizedEgds.add(cloneDependency(egd, atom, atomsToRemove, suffixId));
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting set of normalized tgds: " + normalizedEgds);
        return normalizedEgds;
    }

    private Dependency cloneDependency(Dependency egd, ComparisonAtom atomToKeep, Set<IFormulaAtom> atomsToRemove, String suffixId) {
        Dependency clone = egd.clone();
        clone.addSuffixId(suffixId);
        IFormula conclusion = clone.getConclusion();
        for (Iterator<IFormulaAtom> it = conclusion.getAtoms().iterator(); it.hasNext();) {
            ComparisonAtom atom = (ComparisonAtom) it.next();
            if (isContained(atom, atomsToRemove)) {
                it.remove();
            }
        }
        //Removes variables
        for (Iterator<FormulaVariable> it = conclusion.getLocalVariables().iterator(); it.hasNext();) {
            FormulaVariable localVariable = it.next();
            boolean containsOccurrences = findOccurrencesInAtom(localVariable, atomToKeep);
            if (!containsOccurrences) {
                it.remove();
            }
        }
        //Clean occurrences
        for (FormulaVariable formulaVariable : clone.getPremise().getLocalVariables()) {
            if (logger.isDebugEnabled()) logger.debug("Non relational occurrences of variable " + formulaVariable + ": " + formulaVariable.getNonRelationalOccurrences());
            for (Iterator<IFormulaAtom> it = formulaVariable.getNonRelationalOccurrences().iterator(); it.hasNext();) {
                IFormulaAtom formulaVariableOccurrenceAtom = it.next();
                if(formulaVariableOccurrenceAtom == null){
                    continue; //TODO
                }
                if (isContained(formulaVariableOccurrenceAtom, atomsToRemove)) {
                    it.remove();
                }
            }
        }
        return clone;
    }

    private boolean isContained(IFormulaAtom atom, Set<IFormulaAtom> atomsToRemove) {
        for (IFormulaAtom comparisonAtom : atomsToRemove) {
            if (comparisonAtom.toString().equals(atom.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean findOccurrencesInAtom(FormulaVariable localVariable, ComparisonAtom atomToKeep) {
        for (IFormulaAtom formulaVariableOccurrenceAtom : localVariable.getNonRelationalOccurrences()) {
            if (formulaVariableOccurrenceAtom.toString().equals(atomToKeep.toString())) {
                return true;
            }
        }
        return false;
    }

}
