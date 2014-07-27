package it.unibas.lunatic.model.extendedegdanalysis.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.EquivalenceClassUtility;
import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedDependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.operators.AssignAdditionalAttributes;
import it.unibas.lunatic.model.extendedegdanalysis.DependencyStratification;
import it.unibas.lunatic.model.extendedegdanalysis.DependencyStratum;
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
        symmetryFinder.findSymmetricAtoms(scenario.getExtEGDs());
        findAttributesForBackwardChasing(scenario.getExtEGDs());
        findAllAffectedAttributes(scenario.getExtEGDs());
        assignAdditionalAttributes(scenario.getExtEGDs(), scenario);
        scenario.setStratification(stratification);
    }

    private void findAllQueriedAttributesForEGDs(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            List<AttributeRef> queriedAttributes = DependencyUtility.findQueriedAttributesInPremise(dependency);
            dependency.setQueriedAttributes(queriedAttributes);
        }
    }

    private void findAllQueriedAttributesForTGDs(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            List<AttributeRef> queriedAttributes = DependencyUtility.findQueriedAttributesForExtTGD(dependency);
            dependency.setQueriedAttributes(queriedAttributes);
        }
    }

    private DependencyStratification generateStratification(Scenario scenario) {
        List<ExtendedDependency> dependencies = dependencyBuilder.buildExtendedEGDs(scenario.getExtEGDs(), scenario);
        DirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph = initDependencyGraph(dependencies);
        StrongConnectivityInspector<ExtendedDependency, DefaultEdge> strongConnectivityInspector = new StrongConnectivityInspector<ExtendedDependency, DefaultEdge>(dependencyGraph);
        List<Set<ExtendedDependency>> stronglyConnectedComponents = strongConnectivityInspector.stronglyConnectedSets();
        DependencyStratification stratification = new DependencyStratification();
        for (Set<ExtendedDependency> extendedDependencySet : stronglyConnectedComponents) {
            Set<Dependency> dependencySet = buildDependencySet(extendedDependencySet);
            DependencyStratum stratum = new DependencyStratum(dependencySet);
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

    private void findAttributesForBackwardChasing(List<Dependency> extEGDs) {
        for (Dependency egd : extEGDs) {
            List<BackwardAttribute> attributesForBackwardChasing = new ArrayList<BackwardAttribute>();
            for (ExtendedDependency extendedDependency : egd.getExtendedDependencies()) {
                if (extendedDependency.isForward()) {
                    continue;
                }
                AttributeRef occurrenceAttribute = EquivalenceClassUtility.correctAttributeForSymmetricEGDs(extendedDependency.getOccurrence().getAttributeRef(), egd);
                FormulaVariable variable = LunaticUtility.findPremiseVariableInDepedency(extendedDependency.getOccurrence(), egd);
                BackwardAttribute backwardAttribute = new BackwardAttribute(occurrenceAttribute, variable);
                if (attributesForBackwardChasing.contains(backwardAttribute)) {
                    continue;
                }
                attributesForBackwardChasing.add(backwardAttribute);
            }
            egd.setAttributesForBackwardChasing(attributesForBackwardChasing);
        }
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
