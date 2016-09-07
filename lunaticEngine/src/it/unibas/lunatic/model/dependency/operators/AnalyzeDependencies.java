package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.GenerateStatsForScenario;
import it.unibas.lunatic.model.dependency.AttributesInSameCellGroups;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedEGD;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzeDependencies {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeDependencies.class);
    private final RewriteSTTGDs rewriter = new RewriteSTTGDs();
    private final BuildFaginDependencyGraph faginDependencyGraphBuilder = new BuildFaginDependencyGraph();
    private final CheckWeaklyAcyclicityInTGDs weaklyAcyclicityChecker = new CheckWeaklyAcyclicityInTGDs();
    private final FindSymmetricAtoms symmetryFinder = new FindSymmetricAtoms();
    private final AssignAdditionalAttributes additionalAttributesAssigner = new AssignAdditionalAttributes();
    private final BuildTGDStratification tgdStratificationBuilder = new BuildTGDStratification();
    private final FindAttributesWithLabeledNulls attributeWithNullsFinder = new FindAttributesWithLabeledNulls();
    private final BuildEGDStratification egdStratificationBuilder = new BuildEGDStratification();
    private final FindAttributesInSameCellGroup attributeInSameCellGroupFinder = new FindAttributesInSameCellGroup();
    private final FindInclusionDependencies inclusionDependencyFinder = new FindInclusionDependencies();
    private final FindFunctionalDependencies functionalDependencyFinder = new FindFunctionalDependencies();
    private final GenerateStatsForScenario statGenerator = new GenerateStatsForScenario();

    public void analyzeDependencies(Scenario scenario) {
        if (scenario.getStratification() != null) {
            return;
        }
        long start = new Date().getTime();
//        rewriter.rewrite(scenario);
        DirectedGraph<AttributeRef, ExtendedEdge> faginDependencyGraph = faginDependencyGraphBuilder.buildGraph(scenario.getExtTGDs());
        if (logger.isDebugEnabled()) logger.debug("Fagin Dependency graph " + faginDependencyGraph);
        weaklyAcyclicityChecker.check(faginDependencyGraph, scenario.getExtTGDs());
        DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph = removeSpecialEdges(faginDependencyGraph);
        if (logger.isDebugEnabled()) logger.debug("Dependency graph " + dependencyGraph);
        if (scenario.getConfiguration().isDeScenario() && !scenario.getExtEGDs().isEmpty()) {
            Set<AttributeRef> attributesWithLabeledNulls = attributeWithNullsFinder.findAttributes(dependencyGraph, scenario);
            scenario.setAttributesWithLabeledNulls(attributesWithLabeledNulls);
        }
        AttributesInSameCellGroups attributesInSameCellGroups = attributeInSameCellGroupFinder.findAttributes(dependencyGraph);
        scenario.setAttributesInSameCellGroups(attributesInSameCellGroups);
        findAllQueriedAttributesForEGDs(scenario.getExtEGDs());
        findAllQueriedAttributesForTGDs(scenario.getExtTGDs());
        //EGDs
        DependencyStratification stratification = egdStratificationBuilder.generateStratification(scenario);
        findDependenciesForAttributes(stratification, scenario.getExtEGDs());
        findDependenciesForAttributes(stratification, scenario.getExtTGDs());
        symmetryFinder.findSymmetricAtoms(scenario.getExtEGDs(), scenario);
        findAllAffectedAttributes(scenario.getExtEGDs());
        functionalDependencyFinder.findFunctionalDependencies(scenario);
        assignAdditionalAttributes(scenario.getExtEGDs(), scenario);
        // S-T TGD Rewriting
        rewriter.rewrite(scenario);
        // T-TGD Stratification
        tgdStratificationBuilder.buildTGDStratification(scenario.getExtTGDs(), stratification);
        inclusionDependencyFinder.findInclusionDependenciesAndLinearTgds(scenario);
        scenario.setStratification(stratification);
        // FINAL
        checkAuthoritativeSources(scenario.getExtEGDs(), scenario);
        statGenerator.generateStats(scenario);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.ANALYZE_DEPENDENCIES_TIME, end - start);
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

    private DirectedGraph<AttributeRef, ExtendedEdge> removeSpecialEdges(DirectedGraph<AttributeRef, ExtendedEdge> faginDependencyGraph) {
        DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph = new DefaultDirectedGraph<AttributeRef, ExtendedEdge>(ExtendedEdge.class);
        if (faginDependencyGraph == null) {
            return dependencyGraph;
        }
        Graphs.addGraph(dependencyGraph, faginDependencyGraph);
        for (ExtendedEdge edge : faginDependencyGraph.edgeSet()) {
            if (edge.isSpecial() && !edge.isNormal()) {
                dependencyGraph.removeEdge(edge);
            }
        }
        return dependencyGraph;
    }

}

class EGDStratumComparator implements Comparator<EGDStratum> {

    private DirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph;

    public EGDStratumComparator(DirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
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
                List<DefaultEdge> path = DijkstraShortestPath.findPathBetween(dependencyGraph, dependency1, dependency2);
                if (path != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
