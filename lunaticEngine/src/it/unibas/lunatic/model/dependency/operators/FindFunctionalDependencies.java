package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.FunctionalDependency;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FindFunctionalDependencies {

    private final static Logger logger = LoggerFactory.getLogger(FindFunctionalDependencies.class);

    public void findFunctionalDependencies(Scenario scenario) {
        findFunctionalDependenciesInEgds(scenario.getEGDs());
        findFunctionalDependenciesInEgds(scenario.getExtEGDs());
    }

    private void findFunctionalDependenciesInEgds(List<Dependency> egds) {
        for (Dependency egd : egds) {
            if (egd.hasNegations()) {
                continue;
            }
            if (egd.getSymmetricAtoms().getSize() != 1) {
                continue;
            }
            if (!premiseConsistsOfTwoRelationalAtoms(egd)) {
                continue;
            }
            String table = egd.getSymmetricAtoms().getSymmetricAliases().iterator().next().getTableName();
            List<String> leftAttributes = extractLeftAttributes(egd);
            List<String> rightAttributes = extractRightAttributes(egd);
            FunctionalDependency fd = new FunctionalDependency(table, leftAttributes, rightAttributes);
            egd.setFunctionalDependency(fd);
        }
    }

    private boolean premiseConsistsOfTwoRelationalAtoms(Dependency egd) {
        List<IFormulaAtom> premiseAtoms = egd.getPremise().getPositiveFormula().getAtoms();
        if (premiseAtoms.size() != 2) {
            return false;
        }
        if (!(premiseAtoms.get(0) instanceof RelationalAtom)
                || !(premiseAtoms.get(1) instanceof RelationalAtom)) {
            return false;
        }
        return true;
    }

    private List<String> extractLeftAttributes(Dependency egd) {
        VariableEquivalenceClass variableEC = egd.getSymmetricAtoms().getSelfJoin().getVariableEquivalenceClass();
        Set<String> leftAttributes = new HashSet<String>();
        for (FormulaVariableOccurrence occurrence : variableEC.getPremiseRelationalOccurrences()) {
            leftAttributes.add(ChaseUtility.unAlias(occurrence.getAttributeRef()).getName());
        }
        List<String> result = new ArrayList<String>(leftAttributes);
        Collections.sort(result);
        return result;
    }

    private List<String> extractRightAttributes(Dependency egd) {
        List<FormulaVariable> conclusionVariables = egd.getConclusion().getAtoms().get(0).getVariables();
        Set<String> leftAttributes = new HashSet<String>();
        for (FormulaVariable variable : conclusionVariables) {
            for (FormulaVariableOccurrence occurrence : variable.getPremiseRelationalOccurrences()) {
                leftAttributes.add(ChaseUtility.unAlias(occurrence.getAttributeRef()).getName());
            }
        }
        List<String> result = new ArrayList<String>(leftAttributes);
        Collections.sort(result);
        return result;
    }

}
