package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedDependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.DependencyStratum;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzeDependencies {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeDependencies.class);
    private BuildExtendedDependencies dependencyBuilder = new BuildExtendedDependencies();
    private FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private AssignAdditionalAttributes additionalAttributesAssigner = new AssignAdditionalAttributes();

    public void prepareDependenciesAndGenerateStratification(Scenario scenario) {
        if (scenario.getStratification() != null) {
            return;
        }
        findAllQueriedAttributesForEGDs(scenario.getExtEGDs());
        findAllQueriedAttributesForTGDs(scenario.getExtTGDs());
        DependencyStratification stratification = generateStratification(scenario);
        findDependenciesForAttributes(stratification, scenario.getExtEGDs());
        findDependenciesForAttributes(stratification, scenario.getExtTGDs());
        symmetryFinder.findSymmetricAtoms(scenario.getExtEGDs(), scenario);
        findAllAffectedAttributes(scenario.getExtEGDs());
        assignAdditionalAttributes(scenario.getExtEGDs(), scenario);
        scenario.setStratification(stratification);
    }

    private void findAllQueriedAttributesForEGDs(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            List<AttributeRef> queriedAttributes = DependencyUtility.findTargetQueriedAttributesInPremise(dependency);
            dependency.setQueriedAttributes(queriedAttributes);
        }
    }

    private void findAllQueriedAttributesForTGDs(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            List<AttributeRef> queriedAttributes = DependencyUtility.findTargetQueriedAttributesForExtTGD(dependency);
            dependency.setQueriedAttributes(queriedAttributes);
        }
    }

    private DependencyStratification generateStratification(Scenario scenario) {
        List<ExtendedDependency> extendedDependencies = dependencyBuilder.buildExtendedEGDs(scenario.getExtEGDs(), scenario);
        List<ExtendedDependency> extendedDependenciesToProcess = extractDependenciesToProcess(extendedDependencies, scenario);
        DirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph = initDependencyGraph(extendedDependenciesToProcess);
        StrongConnectivityInspector<ExtendedDependency, DefaultEdge> strongConnectivityInspector = new StrongConnectivityInspector<ExtendedDependency, DefaultEdge>(dependencyGraph);
        List<Set<ExtendedDependency>> stronglyConnectedComponents = strongConnectivityInspector.stronglyConnectedSets();
        DependencyStratification stratification = new DependencyStratification();
        for (Set<ExtendedDependency> extendedDependencySet : stronglyConnectedComponents) {
            Set<Dependency> dependencySet = buildDependencySet(extendedDependencySet);
            DependencyStratum stratum = new DependencyStratum(dependencySet, extendedDependencySet);
            Collections.sort(stratum.getDependencies(), new DependencyComparator());
            stratification.addStratum(stratum);
        }
        Collections.sort(stratification.getStrata(), new StratumComparator(dependencyGraph));
        int counter = 0;
        for (DependencyStratum stratum : stratification.getStrata()) {
            stratum.setId(++counter + "");
        }
        if (logger.isDebugEnabled()) logger.debug("Stratification: " + stratification);
        return stratification;
    }

    private List<ExtendedDependency> extractDependenciesToProcess(List<ExtendedDependency> extendedDependencies, Scenario scenario) {
        if (scenario.getCostManagerConfiguration().isDoBackward()) {
            return extendedDependencies;
        }
        List<ExtendedDependency> forwardDependencies = new ArrayList<ExtendedDependency>();
        for (ExtendedDependency dependency : extendedDependencies) {
            if (dependency.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
                continue;
            }
            forwardDependencies.add(dependency);
        }
        return forwardDependencies;
    }

    private DirectedGraph<ExtendedDependency, DefaultEdge> initDependencyGraph(List<ExtendedDependency> dependencies) {
        DirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<ExtendedDependency, DefaultEdge>(DefaultEdge.class);
        for (ExtendedDependency dependency : dependencies) {
            dependencyGraph.addVertex(dependency);
        }
        Map<AttributeRef, List<ExtendedDependency>> queryAttributeMap = initQueryAttributeMap(dependencies);
        for (ExtendedDependency dependency : dependencies) {
            for (AttributeRef affectedAttribute : dependency.getAffectedAttributes()) {
                List<ExtendedDependency> dependenciesThatQueryAttribute = queryAttributeMap.get(affectedAttribute);
                if (dependenciesThatQueryAttribute == null) {
                    continue;
                }
                for (ExtendedDependency queryDepenency : dependenciesThatQueryAttribute) {
                    dependencyGraph.addEdge(dependency, queryDepenency);
                }
            }
        }
        return dependencyGraph;
    }

    private Map<AttributeRef, List<ExtendedDependency>> initQueryAttributeMap(List<ExtendedDependency> dependencies) {
        Map<AttributeRef, List<ExtendedDependency>> attributeMap = new HashMap<AttributeRef, List<ExtendedDependency>>();
        for (ExtendedDependency dependency : dependencies) {
            for (AttributeRef queryAttributes : dependency.getQueriedAttributes()) {
                List<ExtendedDependency> dependenciesForAttribute = attributeMap.get(queryAttributes);
                if (dependenciesForAttribute == null) {
                    dependenciesForAttribute = new ArrayList<ExtendedDependency>();
                    attributeMap.put(queryAttributes, dependenciesForAttribute);
                }
                dependenciesForAttribute.add(dependency);
            }
        }
        return attributeMap;
    }

    private Set<Dependency> buildDependencySet(Set<ExtendedDependency> extendedDependencySet) {
        Set<Dependency> result = new HashSet<Dependency>();
        for (ExtendedDependency extendedDependency : extendedDependencySet) {
            result.add(extendedDependency.getDependency());
        }
        return result;
    }

    private void findAllAffectedAttributes(List<Dependency> extEGDs) {
        for (Dependency egd : extEGDs) {
            for (ExtendedDependency extendedDependency : egd.getExtendedDependencies()) {
                List<AttributeRef> affectedAttributes = extendedDependency.getAffectedAttributes();
                for (AttributeRef affectedAttribute : affectedAttributes) {
                    LunaticUtility.addIfNotContained(egd.getAffectedAttributes(), affectedAttribute);
                }
            }
        }
    }

    private void assignAdditionalAttributes(List<Dependency> extEGDs, Scenario scenario) {
        for (Dependency egd : extEGDs) {
            additionalAttributesAssigner.assignAttributes(egd, scenario);
        }
    }

    private void findDependenciesForAttributes(DependencyStratification stratification, List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            for (AttributeRef attribute : dependency.getQueriedAttributes()) {
                stratification.addDependencyForAttribute(attribute, dependency);
            }
            for (AttributeRef attribute : dependency.getAffectedAttributes()) {
                stratification.addDependencyForAttribute(attribute, dependency);
            }
        }
    }

}

class StratumComparator implements Comparator<DependencyStratum> {

    private ConnectivityInspector<ExtendedDependency, DefaultEdge> inspector;

    public StratumComparator(DirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph) {
        this.inspector = new ConnectivityInspector<ExtendedDependency, DefaultEdge>(dependencyGraph);
    }

    public int compare(DependencyStratum t1, DependencyStratum t2) {
        if (existsPath(t1, t2)) {
            return -1;
        } else if (existsPath(t2, t1)) {
            return 1;
        }
        return 0;
    }

    private boolean existsPath(DependencyStratum t1, DependencyStratum t2) {
        for (ExtendedDependency dependency1 : t1.getExtendedDependencies()) {
            for (ExtendedDependency dependency2 : t2.getExtendedDependencies()) {
                if (inspector.pathExists(dependency1, dependency2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
