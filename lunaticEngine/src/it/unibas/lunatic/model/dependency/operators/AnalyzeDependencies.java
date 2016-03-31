package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedEGD;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
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

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeDependencies.class);
    private final CheckWeaklyAcyclicityInTGDs weaklyAcyclicityChecker = new CheckWeaklyAcyclicityInTGDs();
    private final BuildExtendedDependencies dependencyBuilder = new BuildExtendedDependencies();
    private final FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private final AssignAdditionalAttributes additionalAttributesAssigner = new AssignAdditionalAttributes();
    private final BuildTGDStratification tgdStratificationBuilder = new BuildTGDStratification();

    public void prepareDependenciesAndGenerateStratification(Scenario scenario) {
        if (scenario.getStratification() != null) {
            return;
        }
        weaklyAcyclicityChecker.check(scenario.getExtTGDs());
        findAllQueriedAttributesForEGDs(scenario.getExtEGDs());
        findAllQueriedAttributesForTGDs(scenario.getExtTGDs());
        DependencyStratification stratification = generateStratification(scenario);
        findDependenciesForAttributes(stratification, scenario.getExtEGDs());
        findDependenciesForAttributes(stratification, scenario.getExtTGDs());
        symmetryFinder.findSymmetricAtoms(scenario.getExtEGDs(), scenario);
        findAllAffectedAttributes(scenario.getExtEGDs());
        assignAdditionalAttributes(scenario.getExtEGDs(), scenario);
        tgdStratificationBuilder.buildTGDStratification(scenario.getExtTGDs(), stratification);
        scenario.setStratification(stratification);
        checkAuthoritativeSources(scenario.getExtEGDs(), scenario);
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

    private void findAllAffectedAttributes(List<Dependency> extEGDs) {
        for (Dependency egd : extEGDs) {
            for (ExtendedEGD extendedDependency : egd.getExtendedDependencies()) {
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
                stratification.addEGDDependencyForAttribute(attribute, dependency);
            }
            for (AttributeRef attribute : dependency.getAffectedAttributes()) {
                stratification.addEGDDependencyForAttribute(attribute, dependency);
            }
        }
    }

    private void checkAuthoritativeSources(List<Dependency> extEGDs, Scenario scenario) {
        for (Dependency egd : extEGDs) {
            List<String> sourceAtoms = DependencyUtility.findSourceAtoms(egd, scenario);
            for (String tableName : sourceAtoms) {
                if (!scenario.getAuthoritativeSources().contains(tableName)) {
                    logger.warn("**** WARNING: egd " + egd.getId() + " contain a source non-authoritative atom:\n" + egd);
//                    throw new IllegalArgumentException("**** WARNING: egd " + egd.getId() + " contain a source non-authoritative atom:\n" + egd);
                }
            }
        }
    }

}

class EGDStratumComparator implements Comparator<EGDStratum> {

    private ConnectivityInspector<ExtendedEGD, DefaultEdge> inspector;

    public EGDStratumComparator(DirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph) {
        this.inspector = new ConnectivityInspector<ExtendedEGD, DefaultEdge>(dependencyGraph);
    }

    public int compare(EGDStratum t1, EGDStratum t2) {
        if (existsPath(t1, t2)) {
            return -1;
        } else if (existsPath(t2, t1)) {
            return 1;
        }
        return 0;
    }

    private boolean existsPath(EGDStratum t1, EGDStratum t2) {
        for (ExtendedEGD dependency1 : t1.getExtendedDependencies()) {
            for (ExtendedEGD dependency2 : t2.getExtendedDependencies()) {
                if (inspector.pathExists(dependency1, dependency2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
