package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.TGDStratum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import speedy.utility.SpeedyUtility;

public class BuildTGDStratification {

    private final static Logger logger = LoggerFactory.getLogger(BuildTGDStratification.class);

    public void buildTGDStratification(List<Dependency> extTGDs, DependencyStratification stratification) {
        if (logger.isDebugEnabled()) logger.debug("Building TGD stratification...");
        Map<Dependency, Set<Dependency>> affectedTGDsMap = findAffectedDependenciesForTGDs(extTGDs);
        stratification.setAffectedTGDsMap(affectedTGDsMap);
        if (logger.isDebugEnabled()) logger.debug("Affected TGDs Map: \n" + SpeedyUtility.printMap(affectedTGDsMap));
        DirectedGraph<Dependency, DefaultEdge> chaseGraph = buildChaseGraph(affectedTGDsMap);
        if (logger.isDebugEnabled()) logger.debug("Chase graph: " + chaseGraph);
        StrongConnectivityInspector<Dependency, DefaultEdge> strongConnectivityInspector = new StrongConnectivityInspector<Dependency, DefaultEdge>(chaseGraph);
        List<Set<Dependency>> stronglyConnectedComponents = strongConnectivityInspector.stronglyConnectedSets();
        for (Set<Dependency> connectedComponent : stronglyConnectedComponents) {
            TGDStratum stratum = buildTGDStratum(connectedComponent, chaseGraph);
            stratification.addTGDStratum(stratum);
        }
        Collections.sort(stratification.getTGDStrata(), new TGDStratumComparator(chaseGraph));
        int counter = 0;
        for (TGDStratum stratum : stratification.getTGDStrata()) {
            stratum.setId(++counter + "");
        }
        if (logger.isDebugEnabled()) logger.debug(stratification.toString());
        DirectedGraph<TGDStratum, DefaultEdge> strataGraph = buildStrataGraph(chaseGraph, stratification.getTGDStrata());
        stratification.setStrataGraph(strataGraph);
    }

    private Map<Dependency, Set<Dependency>> findAffectedDependenciesForTGDs(List<Dependency> tgds) {
        Map<Dependency, Set<Dependency>> result = new HashMap<Dependency, Set<Dependency>>();
        Map<String, Set<Dependency>> relationInDependencyPremiseMap = mapTgdsWithAtomInPremise(tgds);
        if (logger.isDebugEnabled()) logger.debug("Relation in Dependency: \n" + SpeedyUtility.printMap(relationInDependencyPremiseMap));
        for (Dependency tgd : tgds) {
            Set<Dependency> affectedDependenciesForTGD = findAffectedDependenciesForTGD(tgd, relationInDependencyPremiseMap);
            result.put(tgd, affectedDependenciesForTGD);
        }
        return result;
    }

    private Map<String, Set<Dependency>> mapTgdsWithAtomInPremise(List<Dependency> tgds) {
        Map<String, Set<Dependency>> result = new HashMap<String, Set<Dependency>>();
        for (Dependency tgd : tgds) {
            for (IFormulaAtom atom : tgd.getPremise().getAtoms()) {
                if (!(atom instanceof RelationalAtom)) {
                    continue;
                }
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                String key = relationalAtom.getTableAlias().getTableName();
                Set<Dependency> dependencies = result.get(key);
                if (dependencies == null) {
                    dependencies = new HashSet<Dependency>();
                    result.put(key, dependencies);
                }
                dependencies.add(tgd);
            }
        }
        return result;
    }

    private Set<Dependency> findAffectedDependenciesForTGD(Dependency tgd, Map<String, Set<Dependency>> tgdsWithAtomInPremiseMap) {
        Set<Dependency> result = new HashSet<Dependency>();
        for (IFormulaAtom atom : tgd.getConclusion().getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            String key = relationalAtom.getTableAlias().getTableName();
            Set<Dependency> involvedDependencies = tgdsWithAtomInPremiseMap.get(key);
            if (involvedDependencies == null) {
                continue;
            }
            result.addAll(involvedDependencies);
        }
        return result;
    }

    private DirectedGraph<Dependency, DefaultEdge> buildChaseGraph(Map<Dependency, Set<Dependency>> affectedDependencies) {
        DirectedGraph<Dependency, DefaultEdge> chaseGraph = new DefaultDirectedGraph<Dependency, DefaultEdge>(DefaultEdge.class);
        for (Dependency tgd : affectedDependencies.keySet()) {
            chaseGraph.addVertex(tgd);
        }
        for (Dependency dependency : affectedDependencies.keySet()) {
            for (Dependency connectedDependency : affectedDependencies.get(dependency)) {
                chaseGraph.addEdge(dependency, connectedDependency);
            }
        }
        return chaseGraph;
    }

    private TGDStratum buildTGDStratum(Set<Dependency> connectedComponent, DirectedGraph<Dependency, DefaultEdge> chaseGraph) {
        List<Dependency> sortedTGDs = new ArrayList<Dependency>(connectedComponent);
        Collections.sort(sortedTGDs, new SortDependenciesByInputDegree(chaseGraph));
        return new TGDStratum(sortedTGDs);
    }

    private DirectedGraph<TGDStratum, DefaultEdge> buildStrataGraph(DirectedGraph<Dependency, DefaultEdge> dependencyGraph, List<TGDStratum> tgdStrata) {
        DirectedGraph<TGDStratum, DefaultEdge> strataGraph = new DefaultDirectedGraph<TGDStratum, DefaultEdge>(DefaultEdge.class);
        for (TGDStratum stratum : tgdStrata) {
            strataGraph.addVertex(stratum);
        }
        for (TGDStratum stratumA : tgdStrata) {
            for (TGDStratum stratumB : tgdStrata) {
                if(stratumA == stratumB){
                    continue;
                }
                if (existsPath(dependencyGraph, stratumA, stratumB)) {
                    strataGraph.addEdge(stratumA, stratumB);
                }
            }
        }
        return strataGraph;
    }

    private boolean existsPath(DirectedGraph<Dependency, DefaultEdge> dependencyGraph, TGDStratum t1, TGDStratum t2) {
        for (Dependency dependency1 : t1.getTgds()) {
            for (Dependency dependency2 : t2.getTgds()) {
                if (dependencyGraph.containsEdge(dependency1, dependency2)) {
                    return true;
                }
            }
        }
        return false;
    }

    class SortDependenciesByInputDegree implements Comparator<Dependency> {

        private DirectedGraph<Dependency, DefaultEdge> chaseGraph;

        public SortDependenciesByInputDegree(DirectedGraph<Dependency, DefaultEdge> chaseGraph) {
            this.chaseGraph = chaseGraph;
        }

        public int compare(Dependency d1, Dependency d2) {
            int inputDegree1 = chaseGraph.inDegreeOf(d1);
            int inputDegree2 = chaseGraph.inDegreeOf(d2);
            return inputDegree1 - inputDegree2;
        }
    }

    class TGDStratumComparator implements Comparator<TGDStratum> {

        private DirectedGraph<Dependency, DefaultEdge> dependencyGraph;

        public TGDStratumComparator(DirectedGraph<Dependency, DefaultEdge> dependencyGraph) {
            this.dependencyGraph = dependencyGraph;
        }

        public int compare(TGDStratum t1, TGDStratum t2) {
            if (existsPath(dependencyGraph, t1, t2)) {
                return -1;
            } else if (existsPath(dependencyGraph, t2, t1)) {
                return 1;
            }
            return 0;
        }
    }

}
