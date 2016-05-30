package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;

public class BuildFaginDependencyGraph {

    private static Logger logger = LoggerFactory.getLogger(BuildFaginDependencyGraph.class);

    public DirectedGraph<AttributeRef, ExtendedEdge> buildGraph(List<Dependency> extTGDs) {
        if (extTGDs.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) logger.debug("**** Checking weakly acyclicity");
        DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph = new DefaultDirectedGraph<AttributeRef, ExtendedEdge>(ExtendedEdge.class);
        Set<AttributeRef> positionsAdded = new HashSet<AttributeRef>();
        for (Dependency extTGD : extTGDs) {
            addNodes(dependencyGraph, extTGD.getPremise(), positionsAdded);
            addNodes(dependencyGraph, extTGD.getConclusion(), positionsAdded);
            addEdges(dependencyGraph, extTGD);
        }
        if (logger.isDebugEnabled()) logger.debug("Dependency graph: " + dependencyGraph);
        return dependencyGraph;
    }

    private void addNodes(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, IFormula formula, Set<AttributeRef> positionsAdded) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            if (relationalAtom.isSource()) {
                continue;
            }
            String tableName = relationalAtom.getTableAlias().getTableName();
            for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                String attributeName = attribute.getAttributeName();
                AttributeRef position = buildPosition(tableName, attributeName);
                if (positionsAdded.contains(position)) {
                    continue;
                }
                dependencyGraph.addVertex(position);
                positionsAdded.add(position);
            }
        }
    }

    private void addEdges(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, Dependency extTGD) {
        for (FormulaVariable variable : extTGD.getPremise().getLocalVariables()) {
            if (variable.getConclusionRelationalOccurrences().isEmpty()) {
                continue;
            }
            for (FormulaVariableOccurrence premiseRelationalOccurrence : variable.getPremiseRelationalOccurrences()) {
                AttributeRef premiseNode = getPosition(premiseRelationalOccurrence);
                if (premiseNode.isSource()) {
                    continue;
                }
                for (FormulaVariableOccurrence conclusionRelationalOccurrence : variable.getConclusionRelationalOccurrences()) {
                    AttributeRef conclusionNode = getPosition(conclusionRelationalOccurrence);
                    ExtendedEdge existingEdge = dependencyGraph.getEdge(premiseNode, conclusionNode);
                    if (existingEdge != null) {
                        existingEdge.setNormal(true);
                    } else {
                        ExtendedEdge normalEdge = new ExtendedEdge();
                        normalEdge.setNormal(true);
                        dependencyGraph.addEdge(premiseNode, conclusionNode, normalEdge);
                    }
                }
                List<FormulaVariable> existentialVariables = DependencyUtility.getExistentialVariables(extTGD);
                for (FormulaVariable existentialVariable : existentialVariables) {
                    for (FormulaVariableOccurrence existentialOccurrence : existentialVariable.getConclusionRelationalOccurrences()) {
                        AttributeRef conclusionNode = getPosition(existentialOccurrence);
                        ExtendedEdge existingEdge = dependencyGraph.getEdge(premiseNode, conclusionNode);
                        if (existingEdge != null) {
                            existingEdge.setSpecial(true);
                        } else {
                            ExtendedEdge specialEdge = new ExtendedEdge();
                            specialEdge.setSpecial(true);
                            dependencyGraph.addEdge(premiseNode, conclusionNode, specialEdge);
                        }
                    }
                }
            }
        }
    }

    private AttributeRef buildPosition(String tableName, String attributeName) {
        return new AttributeRef(tableName, attributeName);
    }

    private AttributeRef getPosition(FormulaVariableOccurrence relationalOccurrence) {
        return ChaseUtility.unAlias(relationalOccurrence.getAttributeRef());
    }
}
