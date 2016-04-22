package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
import it.unibas.lunatic.model.dependency.ExtendedEGD;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;

public class BuildEGDStratification {

    private final static Logger logger = LoggerFactory.getLogger(BuildEGDStratification.class);
    private final BuildExtendedDependencies dependencyBuilder = new BuildExtendedDependencies();

    public DependencyStratification generateStratification(Scenario scenario) {
        List<ExtendedEGD> extendedDependencies = dependencyBuilder.buildExtendedEGDs(scenario.getExtEGDs(), scenario);
        DirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph = initDependencyGraph(extendedDependencies);
        StrongConnectivityInspector<ExtendedEGD, DefaultEdge> strongConnectivityInspector = new StrongConnectivityInspector<ExtendedEGD, DefaultEdge>(dependencyGraph);
        List<Set<ExtendedEGD>> stronglyConnectedComponents = strongConnectivityInspector.stronglyConnectedSets();
        DependencyStratification stratification = new DependencyStratification();
        for (Set<ExtendedEGD> extendedDependencySet : stronglyConnectedComponents) {
            Set<Dependency> dependencySet = buildDependencySet(extendedDependencySet);
            EGDStratum stratum = new EGDStratum(dependencySet, extendedDependencySet);
            Collections.sort(stratum.getDependencies(), new DependencyComparator(scenario));
            stratification.addEGDStratum(stratum);
        }
        Collections.sort(stratification.getEGDStrata(), new EGDStratumComparator(dependencyGraph));
        int counter = 0;
        for (EGDStratum stratum : stratification.getEGDStrata()) {
            stratum.setId(++counter + "");
        }
        if (logger.isDebugEnabled()) logger.debug("Stratification: " + stratification);
        return stratification;
    }

    private DirectedGraph<ExtendedEGD, DefaultEdge> initDependencyGraph(List<ExtendedEGD> dependencies) {
        DirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<ExtendedEGD, DefaultEdge>(DefaultEdge.class);
        for (ExtendedEGD dependency : dependencies) {
            dependencyGraph.addVertex(dependency);
        }
        Map<AttributeRef, List<ExtendedEGD>> queryAttributeMap = initQueryAttributeMap(dependencies);
        for (ExtendedEGD dependency : dependencies) {
            for (AttributeRef affectedAttribute : dependency.getAffectedAttributes()) {
                List<ExtendedEGD> dependenciesThatQueryAttribute = queryAttributeMap.get(affectedAttribute);
                if (dependenciesThatQueryAttribute == null) {
                    continue;
                }
                for (ExtendedEGD queryDepenency : dependenciesThatQueryAttribute) {
                    dependencyGraph.addEdge(dependency, queryDepenency);
                }
            }
        }
        return dependencyGraph;
    }

    private Map<AttributeRef, List<ExtendedEGD>> initQueryAttributeMap(List<ExtendedEGD> dependencies) {
        Map<AttributeRef, List<ExtendedEGD>> attributeMap = new HashMap<AttributeRef, List<ExtendedEGD>>();
        for (ExtendedEGD dependency : dependencies) {
            for (AttributeRef queryAttributes : dependency.getQueriedAttributes()) {
                List<ExtendedEGD> dependenciesForAttribute = attributeMap.get(queryAttributes);
                if (dependenciesForAttribute == null) {
                    dependenciesForAttribute = new ArrayList<ExtendedEGD>();
                    attributeMap.put(queryAttributes, dependenciesForAttribute);
                }
                dependenciesForAttribute.add(dependency);
            }
        }
        return attributeMap;
    }

    private Set<Dependency> buildDependencySet(Set<ExtendedEGD> extendedDependencySet) {
        Set<Dependency> result = new HashSet<Dependency>();
        for (ExtendedEGD extendedDependency : extendedDependencySet) {
            result.add(extendedDependency.getDependency());
        }
        return result;
    }
}
