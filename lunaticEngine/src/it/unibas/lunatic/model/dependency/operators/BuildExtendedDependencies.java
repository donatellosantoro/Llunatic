package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedEGD;
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

    private static final Logger logger = LoggerFactory.getLogger(BuildExtendedDependencies.class);

    public List<ExtendedEGD> buildExtendedEGDs(List<Dependency> dependencies, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Original dependencies: \n" + LunaticUtility.printCollection(dependencies));
        List<ExtendedEGD> result = new ArrayList<ExtendedEGD>();
        for (Dependency dependency : dependencies) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing dependency: " + dependency);
            if (dependency.getConclusion().getAtoms().size() > 1) {
                throw new IllegalArgumentException("The chase algorithm requires normalized depdendencies: " + dependency);
            }
            List<ExtendedEGD> extendedDependencies = new ArrayList<ExtendedEGD>();
            if (logger.isDebugEnabled()) logger.debug("Building forward egd...");
            extendedDependencies.add(buildForwardEGD(dependency));
            findBackwardAttributes(dependency);
            if (scenario.getCostManagerConfiguration().isDoBackwardOnDependency(dependency)) {
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
    private ExtendedEGD buildForwardEGD(Dependency dependency) {
        String id = dependency.getId() + LunaticConstants.CHASE_FORWARD;
        ExtendedEGD forward = new ExtendedEGD(id, dependency, LunaticConstants.CHASE_FORWARD);
        return forward;
    }

    private void findBackwardAttributes(Dependency dependency) {
        if (logger.isDebugEnabled()) logger.debug("Building backward attributes for egd: " + dependency);
        // backward chasing is only for joins (which includes equalities and selections, via const tables)
        // backward chasing is not doable on other comparisons eg: a != 3 and built-ins
        List<FormulaVariableOccurrence> result = new ArrayList<FormulaVariableOccurrence>();
        List<VariableEquivalenceClass> relevantVariableClasses = ChaseUtility.findJoinVariablesInTarget(dependency);
        if (logger.isDebugEnabled()) logger.debug("Join variables in target: " + relevantVariableClasses);
        for (VariableEquivalenceClass variableClass : relevantVariableClasses) {
            List<FormulaVariableOccurrence> targetOccurrencesForEqvClass = ChaseUtility.findTargetOccurrences(variableClass);
            List<FormulaVariableOccurrence> positiveTargetOccurrencesForEqvClass = ChaseUtility.findPositiveOccurrences(dependency.getPremise().getPositiveFormula(), targetOccurrencesForEqvClass);
            for (FormulaVariableOccurrence occurrence : positiveTargetOccurrencesForEqvClass) {
                result.add(occurrence);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        dependency.setBackwardAttributes(result);
    }

    private List<ExtendedEGD> buildBackwardEGDs(Dependency dependency) {
        List<ExtendedEGD> result = new ArrayList<ExtendedEGD>();
        if (logger.isDebugEnabled()) logger.debug("Building backward dependencies for egd: " + dependency);
        int i = 0;
        for (FormulaVariableOccurrence backwardAttribute : dependency.getBackwardAttributes()) {
            String id = dependency.getId() + LunaticConstants.CHASE_BACKWARD + i++;
            ExtendedEGD backward = new ExtendedEGD(id, dependency, LunaticConstants.CHASE_BACKWARD, backwardAttribute);
            result.add(backward);
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return result;
    }

    //////////////////////////////////////////////////////////////////////////////////
    ///////
    ///////                  FIND QUERY AND AFFECTED ATTRIBUTES
    ///////
    //////////////////////////////////////////////////////////////////////////////////
    private void findQueriedAndLocalAffectedAttributes(Dependency dependency, List<ExtendedEGD> extendedDependencies) {
        List<AttributeRef> forwardAffectedAttributes = findAffectedAttributesForForwardDependency(dependency);
        for (ExtendedEGD extendedDependency : extendedDependencies) {
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

    private void findAffectedAttributes(List<ExtendedEGD> dependencies) {
        // affected attributes must take into account also clustering of values, not only local affected attributes
        // eg: e1 -> AB, e2: -> A
        Map<AttributeRef, List<ExtendedEGD>> attributeMap = initAttributeMap(dependencies);
        UndirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph = initDependencyGraph(dependencies, attributeMap);
        ConnectivityInspector<ExtendedEGD, DefaultEdge> inspector = new ConnectivityInspector<ExtendedEGD, DefaultEdge>(dependencyGraph);
        List<Set<ExtendedEGD>> connectedComponents = inspector.connectedSets();
        for (Set<ExtendedEGD> connectedComponent : connectedComponents) {
            List<AttributeRef> affectedAttributesForComponent = extractAttributesForComponent(connectedComponent);
            for (ExtendedEGD dependency : connectedComponent) {
                if (logger.isDebugEnabled()) logger.debug("Dependency " + dependency + "\nAffected: " + affectedAttributesForComponent);
                dependency.setAffectedAttributes(affectedAttributesForComponent);
            }
        }
    }

    private Map<AttributeRef, List<ExtendedEGD>> initAttributeMap(List<ExtendedEGD> dependencies) {
        Map<AttributeRef, List<ExtendedEGD>> attributeMap = new HashMap<AttributeRef, List<ExtendedEGD>>();
        for (ExtendedEGD dependency : dependencies) {
            for (AttributeRef localAffectedAttribute : dependency.getLocalAffectedAttributes()) {
                List<ExtendedEGD> dependenciesForAttribute = attributeMap.get(localAffectedAttribute);
                if (dependenciesForAttribute == null) {
                    dependenciesForAttribute = new ArrayList<ExtendedEGD>();
                    attributeMap.put(localAffectedAttribute, dependenciesForAttribute);
                }
                dependenciesForAttribute.add(dependency);
            }
        }
        return attributeMap;
    }

    private UndirectedGraph<ExtendedEGD, DefaultEdge> initDependencyGraph(List<ExtendedEGD> dependencies, Map<AttributeRef, List<ExtendedEGD>> attributeMap) {
        UndirectedGraph<ExtendedEGD, DefaultEdge> dependencyGraph = new SimpleGraph<ExtendedEGD, DefaultEdge>(DefaultEdge.class);
        for (ExtendedEGD dependency : dependencies) {
            dependencyGraph.addVertex(dependency);
        }
        for (ExtendedEGD dependency : dependencies) {
            if (dependency.getAffectedAttributes() != null) {
                continue;
            }
            for (AttributeRef localAffected : dependency.getLocalAffectedAttributes()) {
                List<ExtendedEGD> otherDependencies = attributeMap.get(localAffected);
                for (ExtendedEGD otherDependency : otherDependencies) {
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

    private List<AttributeRef> extractAttributesForComponent(Set<ExtendedEGD> connectedComponent) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (ExtendedEGD dependency : connectedComponent) {
            for (AttributeRef affectedAttribute : dependency.getLocalAffectedAttributes()) {
                if (!result.contains(affectedAttribute)) {
                    result.add(affectedAttribute);
                }
            }
        }
        return result;
    }

    private String printDependencies(List<ExtendedEGD> dependencies) {
        StringBuilder result = new StringBuilder();
        for (ExtendedEGD dependency : dependencies) {
            result.append(dependency.toLongString()).append("\n");
        }
        return result.toString();
    }

}
