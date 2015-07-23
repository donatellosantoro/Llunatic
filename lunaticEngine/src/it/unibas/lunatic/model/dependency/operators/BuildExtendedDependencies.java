package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedDependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildExtendedDependencies {

    private static Logger logger = LoggerFactory.getLogger(BuildExtendedDependencies.class);

//    private FindSubsumedAtoms subsumptionFinder = new FindSubsumedAtoms();

    public List<ExtendedDependency> buildExtendedEGDs(List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Original dependencies: \n" + LunaticUtility.printCollection(dependencies));
        List<ExtendedDependency> result = new ArrayList<ExtendedDependency>();
        for (Dependency dependency : dependencies) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing dependency: " + dependency);
            if (dependency.getConclusion().getAtoms().size() > 1) {
                throw new IllegalArgumentException("The chase algorithm requires normalized depdendencies: " + dependency);
            }
            List<ExtendedDependency> extendedDependencies = new ArrayList<ExtendedDependency>();
            if (logger.isDebugEnabled()) logger.debug("Building forward egd...");
            extendedDependencies.add(buildForwardEGD(dependency));
            if (LunaticUtility.canDoBackward(scenario)) {
                if (logger.isDebugEnabled()) logger.debug("Building backward egds...");
                extendedDependencies.addAll(buildBackwardEGDs(dependency));
            } 
            findQueriedAndLocalAffectedAttributes(dependency, extendedDependencies);
            result.addAll(extendedDependencies);
            dependency.setExtendedDependencies(extendedDependencies);
            Collections.sort(dependency.getExtendedDependencies(), new ExtendedDependencyComparator());
        }
        findAffectedAttributes(result);
        if (logger.isDebugEnabled()) logger.debug("Extended dependencies: \n" + printDependencies(result));
        return result;
    }

    //////////////////////////////////////////////////////////////////////////////////
    ///////
    ///////                    BUILD DEPENDENCIES
    ///////
    //////////////////////////////////////////////////////////////////////////////////
    private ExtendedDependency buildForwardEGD(Dependency dependency) {
        String id = dependency.getId() + LunaticConstants.CHASE_FORWARD;
        ExtendedDependency forward = new ExtendedDependency(id, dependency, LunaticConstants.CHASE_FORWARD);
        return forward;
    }

    private List<ExtendedDependency> buildBackwardEGDs(Dependency dependency) {
        if (logger.isDebugEnabled()) logger.debug("Building backward dependencies for egd: " + dependency);
        // backward chasing is only for joins (which includes equalities and selections, via const tables)
        // backward chasing is not doable on other comparisons eg: a != 3 and built-ins
        List<ExtendedDependency> result = new ArrayList<ExtendedDependency>();
        List<VariableEquivalenceClass> relevantVariableClasses = ChaseUtility.findJoinVariablesInTarget(dependency);
        if (logger.isDebugEnabled()) logger.debug("Join variables in target: " + relevantVariableClasses);
        int i = 0;
        for (VariableEquivalenceClass variableClass : relevantVariableClasses) {
            for (FormulaVariableOccurrence occurrence : ChaseUtility.findTargetOccurrences(variableClass)) {
                String id = dependency.getId() + LunaticConstants.CHASE_BACKWARD + i++;
                ExtendedDependency backward = new ExtendedDependency(id, dependency, LunaticConstants.CHASE_BACKWARD, occurrence);
                result.add(backward);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return result;
    }

    //////////////////////////////////////////////////////////////////////////////////
    ///////
    ///////                  FIND QUERY AND AFFECTED ATTRIBUTES
    ///////
    //////////////////////////////////////////////////////////////////////////////////
    private void findQueriedAndLocalAffectedAttributes(Dependency dependency, List<ExtendedDependency> extendedDependencies) {
//        List<AttributeRef> queriedAttributes = DependencyUtility.findQueriedAttributes(dependency);
//        dependency.setQueriedAttributes(queriedAttributes);
        List<AttributeRef> forwardAffectedAttributes = findAffectedAttributesForForwardDependency(dependency);
        for (ExtendedDependency extendedDependency : extendedDependencies) {
            if (extendedDependency.isBackward()) {
                FormulaVariableOccurrence occurrence = extendedDependency.getOccurrence();
                List<AttributeRef> affectedAttributes = Arrays.asList(new AttributeRef[]{ChaseUtility.unAlias(occurrence.getAttributeRef())});
                extendedDependency.setLocalAffectedAttributes(affectedAttributes);
            }
            if (extendedDependency.isForward()) {
                extendedDependency.setLocalAffectedAttributes(forwardAffectedAttributes);
            }
        }
    }

    private List<AttributeRef> findAffectedAttributesForForwardDependency(Dependency dependency) throws ChaseException {
        List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                throw new ChaseException("Illegal egd. Only comparisons are allowed in the conclusion: " + dependency);
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.getVariables().size() != 2) {
                throw new ChaseException("Unable to handle extended egd: constants appear in conclusion; \n" + dependency.toLongString() + "\n - Comparison atom: " + comparison + "\n - Number of variables: " + comparison.getVariables().size());
            }
            FormulaVariable v1 = comparison.getVariables().get(0);
            if (v1.getPremiseRelationalOccurrences().size() > 1) {
                dependency.setOverlapBetweenAffectedAndQueried(true);
            }
            FormulaVariable v2 = comparison.getVariables().get(1);
            if (v2.getPremiseRelationalOccurrences().size() > 1) {
                dependency.setOverlapBetweenAffectedAndQueried(true);
            }
            addAttributesForVariable(v1, affectedAttributes);
            addAttributesForVariable(v2, affectedAttributes);
        }
        return affectedAttributes;
    }

    private void addAttributesForVariable(FormulaVariable v, List<AttributeRef> attributes) {
        for (FormulaVariableOccurrence occurrence : v.getPremiseRelationalOccurrences()) {
            AttributeRef attribute = occurrence.getAttributeRef();
            if (attribute.getTableAlias().isSource()) {
                continue;
            }
            AttributeRef unaliasedAttribute = ChaseUtility.unAlias(attribute);
            if (attributes.contains(unaliasedAttribute)) {
                continue;
            }
            attributes.add(unaliasedAttribute);
        }
    }

    private void findAffectedAttributes(List<ExtendedDependency> dependencies) {
        // affected attributes must take into account also clustering of values, not only local affected attributes
        // eg: e1 -> AB, e2: -> A
        Map<AttributeRef, List<ExtendedDependency>> attributeMap = initAttributeMap(dependencies);
        UndirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph = initDependencyGraph(dependencies, attributeMap);
        ConnectivityInspector<ExtendedDependency, DefaultEdge> inspector = new ConnectivityInspector<ExtendedDependency, DefaultEdge>(dependencyGraph);
        List<Set<ExtendedDependency>> connectedComponents = inspector.connectedSets();
        for (Set<ExtendedDependency> connectedComponent : connectedComponents) {
            List<AttributeRef> affectedAttributesForComponent = extractAttributesForComponent(connectedComponent);
            for (ExtendedDependency dependency : connectedComponent) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency + "\nAffected: " + affectedAttributesForComponent);
                dependency.setAffectedAttributes(affectedAttributesForComponent);
            }
        }
    }

    private Map<AttributeRef, List<ExtendedDependency>> initAttributeMap(List<ExtendedDependency> dependencies) {
        Map<AttributeRef, List<ExtendedDependency>> attributeMap = new HashMap<AttributeRef, List<ExtendedDependency>>();
        for (ExtendedDependency dependency : dependencies) {
            for (AttributeRef localAffectedAttribute : dependency.getLocalAffectedAttributes()) {
                List<ExtendedDependency> dependenciesForAttribute = attributeMap.get(localAffectedAttribute);
                if (dependenciesForAttribute == null) {
                    dependenciesForAttribute = new ArrayList<ExtendedDependency>();
                    attributeMap.put(localAffectedAttribute, dependenciesForAttribute);
                }
                dependenciesForAttribute.add(dependency);
            }
        }
        return attributeMap;
    }

    private UndirectedGraph<ExtendedDependency, DefaultEdge> initDependencyGraph(List<ExtendedDependency> dependencies, Map<AttributeRef, List<ExtendedDependency>> attributeMap) {
        UndirectedGraph<ExtendedDependency, DefaultEdge> dependencyGraph = new SimpleGraph<ExtendedDependency, DefaultEdge>(DefaultEdge.class);
        for (ExtendedDependency dependency : dependencies) {
            dependencyGraph.addVertex(dependency);
        }
        for (ExtendedDependency dependency : dependencies) {
            if (dependency.getAffectedAttributes() != null) {
                continue;
            }
            for (AttributeRef localAffected : dependency.getLocalAffectedAttributes()) {
                List<ExtendedDependency> otherDependencies = attributeMap.get(localAffected);
                for (ExtendedDependency otherDependency : otherDependencies) {
                    if (dependency.equals(otherDependency)) {
                        continue;
                    }
                    if (dependencyGraph.containsEdge(dependency, otherDependency)) {
                        continue;
                    }
                    dependencyGraph.addEdge(dependency, otherDependency);
                }
            }
        }
        return dependencyGraph;
    }

    private List<AttributeRef> extractAttributesForComponent(Set<ExtendedDependency> connectedComponent) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (ExtendedDependency dependency : connectedComponent) {
            for (AttributeRef affectedAttribute : dependency.getLocalAffectedAttributes()) {
                if (!result.contains(affectedAttribute)) {
                    result.add(affectedAttribute);
                }
            }
        }
        return result;
    }

    private String printDependencies(List<ExtendedDependency> dependencies) {
        StringBuilder result = new StringBuilder();
        for (ExtendedDependency dependency : dependencies) {
            result.append(dependency.toLongString()).append("\n");
        }
        return result.toString();
    }

}
