package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.utility.graph.DualGaifmanGraph;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalizeConclusionsInTGDs {

    private static Logger logger = LoggerFactory.getLogger(NormalizeConclusionsInTGDs.class);

    //////////////////////////////////////////////////////////////////////////////////////////    
    /////                             TGDS
    //////////////////////////////////////////////////////////////////////////////////////////    
    
    private DualGaifmanGraph dualGaifmanGraphGenerator = new DualGaifmanGraph();

    public List<Dependency> normalizeTGDs(List<Dependency> eTGDs) {
        List<Dependency> normalizedTgds = new ArrayList<Dependency>();
        for (Dependency eTGD : eTGDs) {
            normalizedTgds.addAll(normalizeTGD(eTGD));
        }
        return normalizedTgds;
    }

    private List<Dependency> normalizeTGD(Dependency tgd) {
        if (logger.isDebugEnabled()) logger.debug("Analyzing tgd: " + tgd);
        List<Dependency> normalizedTgds = new ArrayList<Dependency>();
        if (tgd.getConclusion().getAtoms().size() == 1) {
            if (logger.isDebugEnabled()) logger.debug("Tgd has single target variable, adding...");
            normalizedTgds.add(tgd);
            return normalizedTgds;
        }
        UndirectedGraph<RelationalAtom, DefaultEdge> graph = dualGaifmanGraphGenerator.getDualGaifmanGraph(tgd);
        ConnectivityInspector<RelationalAtom, DefaultEdge> inspector = new ConnectivityInspector<RelationalAtom, DefaultEdge>(graph);
        List<Set<RelationalAtom>> connectedComponents = inspector.connectedSets();
        if (connectedComponents.size() == 1) {
            if (logger.isDebugEnabled()) logger.debug("Tgd is normalized...");
            normalizedTgds.add(tgd);
            return normalizedTgds;
        }
        if (logger.isDebugEnabled()) logger.debug("Tgd is not normalized...");
        for (int i = 0; i < connectedComponents.size(); i++) {
            Set<RelationalAtom> connectedComponent = connectedComponents.get(i);
            String suffixId = "_NORM_" + (i + 1);
            normalizedTgds.add(separateComponent(tgd, connectedComponent, suffixId));
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting set of normalized tgds: " + normalizedTgds);
        return normalizedTgds;
    }

    private Dependency separateComponent(Dependency tgd, Set<RelationalAtom> connectedComponent, String suffixId) {
        Dependency clone = tgd.clone();
        clone.addSuffixId(suffixId);
        IFormula conclusion = clone.getConclusion();
        for (Iterator<IFormulaAtom> it = conclusion.getAtoms().iterator(); it.hasNext();) {
            RelationalAtom atom = (RelationalAtom) it.next();
//            if (connectedComponent.contains(atom)) {
            if (isContained(atom, connectedComponent)) {
                continue;
            }
            it.remove();
        }
        //Removes variables
        for (Iterator<FormulaVariable> it = conclusion.getLocalVariables().iterator(); it.hasNext();) {
            FormulaVariable localVariable = it.next();
            boolean containsOccurrences = checkOccurrences(localVariable, connectedComponent);
            if (!containsOccurrences) {
                it.remove();
            }
        }
        //Clean occurrences
        for (FormulaVariable formulaVariable : clone.getPremise().getLocalVariables()) {
            for (Iterator<FormulaVariableOccurrence> it = formulaVariable.getConclusionRelationalOccurrences().iterator(); it.hasNext();) {
                FormulaVariableOccurrence formulaVariableOccurrence = it.next();
                boolean containsOccurrences = containsOccurrences(formulaVariableOccurrence.getTableAlias(), connectedComponent);
                if (!containsOccurrences) {
                    it.remove();
                }
            }
        }
        return clone;
    }

    private boolean isContained(RelationalAtom atom, Set<RelationalAtom> connectedComponent) {
        for (RelationalAtom relationalAtom : connectedComponent) {
            if (relationalAtom.toString().equals(atom.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOccurrences(FormulaVariable localVariable, Set<RelationalAtom> connectedComponent) {
        for (FormulaVariableOccurrence formulaVariableOccurrence : localVariable.getConclusionRelationalOccurrences()) {
            if (containsOccurrences(formulaVariableOccurrence.getTableAlias(), connectedComponent)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOccurrences(TableAlias tableAlias, Set<RelationalAtom> connectedComponent) {
        for (RelationalAtom relationalAtom : connectedComponent) {
            if (relationalAtom.getTableAlias().equals(tableAlias)) {
                return true;
            }
        }
        return false;
    }
    
}
