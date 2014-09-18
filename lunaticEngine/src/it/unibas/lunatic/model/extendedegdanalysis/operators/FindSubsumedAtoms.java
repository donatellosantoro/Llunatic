package it.unibas.lunatic.model.extendedegdanalysis.operators;

import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.extendedegdanalysis.LabeledEdge;
import it.unibas.lunatic.model.extendedegdanalysis.SelfJoin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FindSubsumedAtoms {
    
    private static Logger logger = LoggerFactory.getLogger(FindSubsumedAtoms.class);

    public List<TableAlias> findSubsumedAtoms(Dependency dependency) {
        List<TableAlias> subsumedAtoms = new ArrayList<TableAlias>();
        List<FormulaVariable> joinVariables = ChaseUtility.findJoinVariablesInTarget(dependency);
        UndirectedGraph<String, LabeledEdge> joinGraph = initJoinGraph(dependency, joinVariables);
        List<SelfJoin> selfJoins = findSelfJoins(joinVariables);
        for (SelfJoin selfJoin : selfJoins) {
            analyzeAtoms(selfJoin, joinGraph, subsumedAtoms);
        }
//        dependency.setSubsumedAtoms(subsumedAtoms);
        return subsumedAtoms;
    }

    private UndirectedGraph<String, LabeledEdge> initJoinGraph(Dependency dependency, List<FormulaVariable> joinVariables) {
        UndirectedGraph<String, LabeledEdge> joinGraph = new SimpleGraph<String, LabeledEdge>(LabeledEdge.class);
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            joinGraph.addVertex(relationalAtom.getTableAlias().toString());
        }
        for (FormulaVariable joinVariable : joinVariables) {
            List<FormulaVariableOccurrence> occurrences = joinVariable.getPremiseRelationalOccurrences();
            for (int i = 0; i < occurrences.size() - 1; i++) {
                FormulaVariableOccurrence occurrencei = occurrences.get(i);
                TableAlias aliasi = occurrencei.getAttributeRef().getTableAlias();
                for (int j = i + 1; j < occurrences.size(); j++) {
                    FormulaVariableOccurrence occurrencej = occurrences.get(j);
                    TableAlias aliasj = occurrencej.getAttributeRef().getTableAlias();
                    String edgeLabel = buildEdgeLabel(occurrencei, occurrencej);
                    joinGraph.addEdge(aliasi.toString(), aliasj.toString(), new LabeledEdge(aliasi.toString(), aliasj.toString(), edgeLabel));
                }
            }
        }
        return joinGraph;
    }

    private List<SelfJoin> findSelfJoins(List<FormulaVariable> joinVariables) {
        List<SelfJoin> result = new ArrayList<SelfJoin>();
        for (FormulaVariable variable : joinVariables) {
            Map<AttributeRef, List<TableAlias>> atomMap = buildSelfJoinAtomMap(variable);
            for (List<TableAlias> selfJoinTables : atomMap.values()) {
                SelfJoin selfJoin = new SelfJoin(selfJoinTables);
                result.add(selfJoin);
            }
        }
        return result;
    }

    private Map<AttributeRef, List<TableAlias>> buildSelfJoinAtomMap(FormulaVariable variable) {
        Map<AttributeRef, List<TableAlias>> atomMap = new HashMap<AttributeRef, List<TableAlias>>();
        for (FormulaVariableOccurrence occurrence : ChaseUtility.findTargetOccurrences(variable)) {
            AttributeRef occurrenceAttribute = occurrence.getAttributeRef();
            AttributeRef unaliasedOccurrence = ChaseUtility.unAlias(occurrenceAttribute);
            List<TableAlias> atomsForAttribute = atomMap.get(unaliasedOccurrence);
            if (atomsForAttribute == null) {
                atomsForAttribute = new ArrayList<TableAlias>();
                atomMap.put(unaliasedOccurrence, atomsForAttribute);
            }
            atomsForAttribute.add(occurrence.getAttributeRef().getTableAlias());
        }
        return atomMap;
    }

    private String buildEdgeLabel(FormulaVariableOccurrence occurrencei, FormulaVariableOccurrence occurrencej) {
        List<String> attributes = new ArrayList<String>();
        attributes.add(ChaseUtility.unAlias(occurrencei.getAttributeRef()).toString());
        attributes.add(ChaseUtility.unAlias(occurrencej.getAttributeRef()).toString());
        Collections.sort(attributes);
        StringBuilder result = new StringBuilder();
        result.append(attributes.get(0)).append("-").append(attributes.get(1));
        return result.toString();
    }

    private void analyzeAtoms(SelfJoin selfJoin, UndirectedGraph<String, LabeledEdge> joinGraph, List<TableAlias> subsumedAtoms) {
        for (int i = 0; i < selfJoin.getAtoms().size() - 1; i++) {
            TableAlias aliasi = selfJoin.getAtoms().get(i);
            if (subsumedAtoms.contains(aliasi)) {
                continue;
            }
            for (int j = i + 1; j < selfJoin.getAtoms().size(); j++) {
                TableAlias aliasj = selfJoin.getAtoms().get(j);
                if (subsumedAtoms.contains(aliasi)) {
                    continue;
                }
                UndirectedGraph<String, LabeledEdge> joinGraphForTableAliasi = buildGraph(aliasi, aliasj, joinGraph);
                UndirectedGraph<String, LabeledEdge> joinGraphForTableAliasj = buildGraph(aliasj, aliasi, joinGraph);
                Set<LabeledEdge> edgesi = joinGraphForTableAliasi.edgeSet();
                Set<LabeledEdge> edgesj = joinGraphForTableAliasj.edgeSet();
                if (containsAll(edgesi, edgesj)) {
                    if (logger.isDebugEnabled()) logger.debug("R1 is contained in R2");
                    subsumedAtoms.add(aliasi);
                } else if (containsAll(edgesj, edgesi)) {
                    if (logger.isDebugEnabled()) logger.debug("R2 is contained in R1");
                    subsumedAtoms.add(aliasj);
                }
            }
        }
    }

    private UndirectedGraph<String, LabeledEdge> buildGraph(TableAlias alias, TableAlias otherAlias, UndirectedGraph<String, LabeledEdge> graph) {
        Set<String> vertices = new HashSet<String>(graph.vertexSet());
        vertices.remove(otherAlias.toString());
        UndirectedSubgraph<String, LabeledEdge> subgraph = new UndirectedSubgraph<String, LabeledEdge>(graph, vertices, graph.edgeSet());
        ConnectivityInspector<String, LabeledEdge> inspector = new ConnectivityInspector<String, LabeledEdge>(subgraph);
        Set<String> connectedVertices = inspector.connectedSetOf(alias.toString());
        UndirectedSubgraph<String, LabeledEdge> connectedSubgraph = new UndirectedSubgraph<String, LabeledEdge>(graph, connectedVertices, graph.edgeSet());
        return connectedSubgraph;
    }
    
    private boolean containsAll(Set<LabeledEdge> edges1, Set<LabeledEdge> edges2) {
        for (LabeledEdge edge2 : edges2) {
            if (!contains(edges1, edge2)) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(Set<LabeledEdge> edges1, LabeledEdge edge2) {
        for (LabeledEdge edge1 : edges1) {
            if (edge1.getLabel().equals(edge2.getLabel())) {
                return true;
            }
        }
        return false;
    }
}

