package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.AttributesInSameCellGroups;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;

public class FindAttributesInSameCellGroup {

    private final static Logger logger = LoggerFactory.getLogger(FindAttributesInSameCellGroup.class);

    public AttributesInSameCellGroups findAttributes(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph) {
        AttributesInSameCellGroups attributesInSameCallGroups = new AttributesInSameCellGroups();
        Set<String> pathCache = new HashSet<String>();
        for (AttributeRef attributeRef : dependencyGraph.vertexSet()) {
            Set<AttributeRef> relatedAttributes = findRelatedAttributes(attributeRef, pathCache, dependencyGraph);
            attributesInSameCallGroups.addAttribute(attributeRef, relatedAttributes);
        }
        if (logger.isDebugEnabled()) logger.debug("Map for dependency graph " + dependencyGraph + "\n:" + attributesInSameCallGroups.toString());
        return attributesInSameCallGroups;
    }

    private Set<AttributeRef> findRelatedAttributes(AttributeRef attribute, Set<String> pathCache, DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph) {
        //All attributes corresponding to vertices of paths that pass throught the vertex of attribute
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (AttributeRef otherAttribute : dependencyGraph.vertexSet()) {
            if (attribute.equals(otherAttribute)) {
                result.add(otherAttribute);
                continue;
            }
            String attributePair = buildAttributePair(attribute, otherAttribute);
            if (pathCache.contains(attributePair)) {
                result.add(otherAttribute);
                continue;
            }
            List<ExtendedEdge> outPath = DijkstraShortestPath.findPathBetween(dependencyGraph, attribute, otherAttribute);
            if (logger.isDebugEnabled()) logger.debug("Finding path between " + attribute + " and " + otherAttribute);
            if (outPath != null) {
                if (logger.isDebugEnabled()) logger.debug("Path found");
                addVerticesInPath(outPath, result, pathCache, dependencyGraph);
                continue;
            }
            List<ExtendedEdge> inPath = DijkstraShortestPath.findPathBetween(dependencyGraph, otherAttribute, attribute);
            if (logger.isDebugEnabled()) logger.debug("Finding path between " + otherAttribute + " and " + attribute);
            if (inPath != null) {
                if (logger.isDebugEnabled()) logger.debug("Path found");
                addVerticesInPath(inPath, result, pathCache, dependencyGraph);
            }
        }
        return result;
    }

    private String buildAttributePair(AttributeRef attribute, AttributeRef otherAttribute) {
        String s1 = attribute.toString();
        String s2 = otherAttribute.toString();
        if (s1.compareTo(s2) < 0) {
            return s1 + "|" + s2;
        }
        return s2 + "|" + s1;
    }

    private void addVerticesInPath(List<ExtendedEdge> path, Set<AttributeRef> result, Set<String> pathCache, DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph) {
        List<AttributeRef> vertices = findVerticesInPath(dependencyGraph, path);
        result.addAll(vertices);
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                AttributeRef sourceVertex = vertices.get(i);
                AttributeRef targetVertex = vertices.get(j);
                pathCache.add(buildAttributePair(sourceVertex, targetVertex));
            }
        }
    }

    private List<AttributeRef> findVerticesInPath(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, List<ExtendedEdge> path) {
        List<AttributeRef> vertices = new ArrayList<AttributeRef>();
        vertices.add(dependencyGraph.getEdgeSource(path.get(0)));
        for (ExtendedEdge extendedEdge : path) {
            vertices.add(dependencyGraph.getEdgeTarget(extendedEdge));
        }
        return vertices;
    }

}
