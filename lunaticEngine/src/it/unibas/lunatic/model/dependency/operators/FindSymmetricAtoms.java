package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.BuiltInAtom;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import it.unibas.lunatic.model.dependency.LabeledEdge;
import it.unibas.lunatic.model.dependency.NullFormula;
import it.unibas.lunatic.model.dependency.SelfJoin;
import it.unibas.lunatic.model.dependency.SymmetricAtoms;
import it.unibas.lunatic.utility.LunaticUtility;
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

public class FindSymmetricAtoms {

    private static Logger logger = LoggerFactory.getLogger(FindSymmetricAtoms.class);

    public void findSymmetricAtoms(List<Dependency> dependencies, Scenario scenario) {
        if (!scenario.getConfiguration().isUseSymmetricOptimization()) {
            return;
        }
        for (Dependency dependency : dependencies) {
            findSymmetricAtoms(dependency);
        }
    }

    private void findSymmetricAtoms(Dependency dependency) {
        if (logger.isDebugEnabled()) logger.debug("Finding symmetric atoms for dependency: " + dependency);
        if (hasInequalitiesOrBuiltIns(dependency) || hasNegation(dependency)) {
            return;
        }
        List<VariableEquivalenceClass> joinVariables = ChaseUtility.findJoinVariablesInTarget(dependency);
        if (logger.isDebugEnabled()) logger.debug("Join variables: " + joinVariables);
        UndirectedGraph<TableAlias, LabeledEdge> joinGraph = initJoinGraph(dependency, joinVariables);
        if (joinGraph == null) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Join graph: " + joinGraph.toString());
        List<SelfJoin> selfJoins = findSelfJoins(joinVariables);
        if (logger.isDebugEnabled()) logger.debug("Self joins: " + selfJoins);
        if (selfJoins.size() != 1) {
            return;
        }
        SelfJoin selfJoin = selfJoins.get(0);
        if (selfJoin.getAtoms().size() > 2) {
            return;
        }
        SymmetricAtoms symmetricAtoms = analyzeAtoms(selfJoin, joinGraph);
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + symmetricAtoms);
        if (checkIfConclusionVariablesAreSymmetric(dependency, symmetricAtoms)) {
            dependency.setSymmetricAtoms(symmetricAtoms);
        }
    }

    private boolean hasInequalitiesOrBuiltIns(Dependency dependency) {
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            if (atom instanceof RelationalAtom) {
                continue;
            }
            if (atom instanceof BuiltInAtom) {
                return true;
            }
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                if (!comparisonAtom.isEqualityComparison()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasNegation(Dependency dependency) {
        return !(dependency.getPremise().getNegatedSubFormulas().isEmpty());
    }

    private UndirectedGraph<TableAlias, LabeledEdge> initJoinGraph(Dependency dependency, List<VariableEquivalenceClass> joinVariableClasses) {
        UndirectedGraph<TableAlias, LabeledEdge> joinGraph = new SimpleGraph<TableAlias, LabeledEdge>(LabeledEdge.class);
        if (logger.isDebugEnabled()) logger.debug("Find symmetric atoms for dependency " + dependency);
        if (logger.isDebugEnabled()) logger.debug("Join variables: " + joinVariableClasses);
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            joinGraph.addVertex(relationalAtom.getTableAlias());
        }
        if (logger.isDebugEnabled()) logger.debug("Vertices: " + joinGraph.vertexSet());
        for (VariableEquivalenceClass joinVariableClass : joinVariableClasses) {
            List<FormulaVariableOccurrence> occurrences = joinVariableClass.getPremiseRelationalOccurrences();
            for (int i = 0; i < occurrences.size() - 1; i++) {
                FormulaVariableOccurrence occurrencei = occurrences.get(i);
                TableAlias aliasi = occurrencei.getAttributeRef().getTableAlias();
                for (int j = i + 1; j < occurrences.size(); j++) {
                    FormulaVariableOccurrence occurrencej = occurrences.get(j);
                    TableAlias aliasj = occurrencej.getAttributeRef().getTableAlias();
                    String edgeLabel = buildEdgeLabel(occurrencei, occurrencej);
                    try {
                        joinGraph.addEdge(aliasi, aliasj, new LabeledEdge(aliasi.toString(), aliasj.toString(), edgeLabel));
                    } catch (IllegalArgumentException ex) {
                        // graph is cyclic
                        dependency.setJoinGraphIsCyclic(true);
                        return null;
                    }
                }
            }
        }
        return joinGraph;
    }

    private List<SelfJoin> findSelfJoins(List<VariableEquivalenceClass> joinVariableClass) {
        List<SelfJoin> result = new ArrayList<SelfJoin>();
        for (VariableEquivalenceClass variable : joinVariableClass) {
            Map<AttributeRef, List<TableAlias>> atomMap = buildSelfJoinAtomMap(variable);
            if (logger.isDebugEnabled()) logger.debug("Self join atom map: " + LunaticUtility.printMap(atomMap));
            for (List<TableAlias> selfJoinTables : atomMap.values()) {
                if (selfJoinTables.size() < 2) {
                    continue;
                }
                SelfJoin selfJoin = new SelfJoin(selfJoinTables);
                LunaticUtility.addIfNotContained(result, selfJoin);
            }
        }
        return result;
    }

    private Map<AttributeRef, List<TableAlias>> buildSelfJoinAtomMap(VariableEquivalenceClass variableClass) {
        Map<AttributeRef, List<TableAlias>> atomMap = new HashMap<AttributeRef, List<TableAlias>>();
        for (FormulaVariableOccurrence occurrence : ChaseUtility.findTargetOccurrences(variableClass)) {
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

    private SymmetricAtoms analyzeAtoms(SelfJoin selfJoin, UndirectedGraph<TableAlias, LabeledEdge> joinGraph) {
        Set<TableAlias> symmetricAtoms = new HashSet<TableAlias>();
        if (logger.isDebugEnabled()) logger.debug("Analyzing self join " + selfJoin);
        for (int i = 0; i < selfJoin.getAtoms().size() - 1; i++) {
            TableAlias aliasi = selfJoin.getAtoms().get(i);
            for (int j = i + 1; j < selfJoin.getAtoms().size(); j++) {
                TableAlias aliasj = selfJoin.getAtoms().get(j);
                UndirectedGraph<TableAlias, LabeledEdge> joinGraphForTableAliasi = buildSubGraph(aliasi, aliasj, joinGraph);
                UndirectedGraph<TableAlias, LabeledEdge> joinGraphForTableAliasj = buildSubGraph(aliasj, aliasi, joinGraph);
                Set<LabeledEdge> edgesi = joinGraphForTableAliasi.edgeSet();
                Set<LabeledEdge> edgesj = joinGraphForTableAliasj.edgeSet();
                if (containsAll(edgesi, edgesj) && containsAll(edgesj, edgesi)) {
                    symmetricAtoms.addAll(joinGraphForTableAliasi.vertexSet());
                }
            }
        }
        return new SymmetricAtoms(symmetricAtoms, selfJoin);
    }

    private UndirectedGraph<TableAlias, LabeledEdge> buildSubGraph(TableAlias alias, TableAlias otherAlias, UndirectedGraph<TableAlias, LabeledEdge> graph) {
        Set<TableAlias> vertices = new HashSet<TableAlias>(graph.vertexSet());
        vertices.remove(otherAlias);
        UndirectedSubgraph<TableAlias, LabeledEdge> subgraph = new UndirectedSubgraph<TableAlias, LabeledEdge>(graph, vertices, graph.edgeSet());
        ConnectivityInspector<TableAlias, LabeledEdge> inspector = new ConnectivityInspector<TableAlias, LabeledEdge>(subgraph);
        Set<TableAlias> connectedVertices = inspector.connectedSetOf(alias);
        UndirectedSubgraph<TableAlias, LabeledEdge> connectedSubgraph = new UndirectedSubgraph<TableAlias, LabeledEdge>(graph, connectedVertices, graph.edgeSet());
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

    private boolean checkIfConclusionVariablesAreSymmetric(Dependency dependency, SymmetricAtoms symmetricAtoms) {
        IFormula conclusion = dependency.getConclusion();
        if (conclusion.getAtoms().size() > 1) {
            return false;
        }
        if (conclusion instanceof NullFormula) {
            return true;
        }
        ComparisonAtom comparison = (ComparisonAtom) conclusion.getAtoms().get(0);
        FormulaVariable v1 = comparison.getVariables().get(0);
        FormulaVariable v2 = comparison.getVariables().get(1);
        if (!haveOccurrencesInSelfJoinAtoms(symmetricAtoms.getSelfJoin(), v1, v2)) {
            return false;
        }
        return true;
    }

    private boolean haveOccurrencesInSelfJoinAtoms(SelfJoin selfJoin, FormulaVariable v1, FormulaVariable v2) {
        TableAlias firstTable = selfJoin.getAtoms().get(0);
        TableAlias secondTable = selfJoin.getAtoms().get(1);
        if (hasOccurrenceInTable(v1, firstTable) && hasOccurrenceInTable(v2, secondTable)
                || hasOccurrenceInTable(v2, firstTable) && hasOccurrenceInTable(v1, secondTable)) {
            return true;
        }
        return false;
    }

    private boolean hasOccurrenceInTable(FormulaVariable v, TableAlias tableAlias) {
        for (FormulaVariableOccurrence occurrence : v.getPremiseRelationalOccurrences()) {
            if (occurrence.getAttributeRef().getTableAlias().equals(tableAlias)) {
                return true;
            }
        }
        return false;
    }

}
