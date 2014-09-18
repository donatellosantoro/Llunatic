package it.unibas.lunatic.utility.graph;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualGaifmanGraph {

    private static Logger logger = LoggerFactory.getLogger(DualGaifmanGraph.class);

    public UndirectedGraph<RelationalAtom, DefaultEdge> getDualGaifmanGraph(Dependency dependency) {
        UndirectedGraph<RelationalAtom, DefaultEdge> graph = new SimpleGraph<RelationalAtom, DefaultEdge>(DefaultEdge.class);
        addVertices(graph, dependency);
        addEdges(graph, dependency);
        return graph;
    }

    private void addVertices(UndirectedGraph<RelationalAtom, DefaultEdge> graph, Dependency dependency) {
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                throw new IllegalArgumentException("Unable to normalize TGD " + dependency + ". Only relational atoms in conclusion are allowed");
            }
            graph.addVertex((RelationalAtom) atom);
        }
    }

    private void addEdges(UndirectedGraph<RelationalAtom, DefaultEdge> graph, Dependency dependency) {
        Map<FormulaVariable, List<RelationalAtom>> occurrencesGroup = new HashMap<FormulaVariable, List<RelationalAtom>>();
        for (FormulaVariable formulaVariable : dependency.getConclusion().getLocalVariables()) {
            if (!formulaVariable.getPremiseRelationalOccurrences().isEmpty()) {
                //Universal variable
                continue;
            }
            for (FormulaVariableOccurrence formulaVariableOccurrence : formulaVariable.getConclusionRelationalOccurrences()) {
                RelationalAtom relationalAtom = getRelationalAtom(dependency, formulaVariableOccurrence.getTableAlias());
                addOccurrence(formulaVariable, relationalAtom, occurrencesGroup);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variable occurrences " + LunaticUtility.printMap(occurrencesGroup));
        for (List<RelationalAtom> joinAtoms : occurrencesGroup.values()) {
            for (int i = 0; i < joinAtoms.size() - 1; i++) {
                for (int j = i + 1; j < joinAtoms.size(); j++) {
                    RelationalAtom atomi = joinAtoms.get(i);
                    RelationalAtom atomj = joinAtoms.get(j);
                    graph.addEdge(atomi, atomj);
                }
            }
        }
    }

    private void addOccurrence(FormulaVariable formulaVariable, RelationalAtom relationalAtom, Map<FormulaVariable, List<RelationalAtom>> occurrencesGroup) {
        List<RelationalAtom> occurrences = occurrencesGroup.get(formulaVariable);
        if (occurrences == null) {
            occurrences = new ArrayList<RelationalAtom>();
            occurrencesGroup.put(formulaVariable, occurrences);
        }
        if (!occurrences.contains(relationalAtom)) {
            occurrences.add(relationalAtom);
        }
    }

    private RelationalAtom getRelationalAtom(Dependency dependency, TableAlias tableAlias) {
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            if (relationalAtom.getTableAlias().equals(tableAlias)) {
                return relationalAtom;
            }
        }
        throw new IllegalArgumentException("Unable to find table alias " + tableAlias + " in conclusion " + dependency);
    }
}
