package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;

public class FindAttributesWithLabeledNulls {

    private final static Logger logger = LoggerFactory.getLogger(FindAttributesWithLabeledNulls.class);

    @SuppressWarnings("unchecked")
    public Set<AttributeRef> findAttributes(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, Scenario scenario) {
        if (dependencyGraph == null) {
            return Collections.EMPTY_SET;
        }
        if (logger.isDebugEnabled()) logger.debug("Finding attributes with null in dependency graph\n" + dependencyGraph);
        Set<AttributeRef> initialAttributes = findInitialAttributes(scenario);
        if (logger.isDebugEnabled()) logger.debug("Initial attributes with nulls: " + initialAttributes);
        Set<AttributeRef> result = findReachableAttribuesOnGraph(initialAttributes, dependencyGraph);
        if (logger.isDebugEnabled()) logger.debug("Attributes with nulls: " + result);
        return result;
    }

    private Set<AttributeRef> findInitialAttributes(Scenario scenario) {
        Set<AttributeRef> initialAttributes = new HashSet<AttributeRef>();
        for (Dependency stTGD : scenario.getSTTgds()) {
            processDependency(stTGD, initialAttributes);
        }
        for (Dependency targetTGD : scenario.getExtTGDs()) {
            processDependency(targetTGD, initialAttributes);
        }
        return initialAttributes;
    }

    private void processDependency(Dependency tgd, Set<AttributeRef> initialAttributes) {
        List<FormulaVariable> existentialVariables = DependencyUtility.getExistentialVariables(tgd);
        for (FormulaVariable existentialVariable : existentialVariables) {
            for (FormulaVariableOccurrence conclusionRelationalOccurrence : existentialVariable.getConclusionRelationalOccurrences()) {
                AttributeRef unaliasAttribute = ChaseUtility.unAlias(conclusionRelationalOccurrence.getAttributeRef());
                initialAttributes.add(unaliasAttribute);
            }
        }
    }

    private Set<AttributeRef> findReachableAttribuesOnGraph(Set<AttributeRef> initialAttributes, DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph) {
        Set<AttributeRef> result = new HashSet<AttributeRef>(initialAttributes);
        for (AttributeRef attribute : dependencyGraph.vertexSet()) {
            if (result.contains(attribute)) {
                continue;
            }
            if (isReachable(attribute, initialAttributes, dependencyGraph)) {
                result.add(attribute);
            }
        }
        return result;
    }

    private boolean isReachable(AttributeRef attribute, Set<AttributeRef> initialAttributes, DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph) {
        for (AttributeRef initialAttribute : initialAttributes) {
            if (logger.isTraceEnabled()) logger.trace("Checking reachability of " + attribute + " from " + initialAttribute);
            if (!dependencyGraph.containsVertex(initialAttribute)) {
                continue;
            }
            List path = DijkstraShortestPath.findPathBetween(dependencyGraph, initialAttribute, attribute);
            if (path != null) {
                if (logger.isTraceEnabled()) logger.trace("Found!");
                return true;
            }
        }
        return false;
    }

}
