package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.exceptions.ChaseException;
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
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckWeaklyAcyclicityInTGDs {

    private static Logger logger = LoggerFactory.getLogger(CheckWeaklyAcyclicityInTGDs.class);

    public void check(List<Dependency> extTGDs) {
        if (extTGDs.isEmpty()) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("**** Checking weakly acyclicity");
        DirectedGraph<String, ExtendedEdge> dependencyGraph = new DefaultDirectedGraph<String, ExtendedEdge>(ExtendedEdge.class);
        Set<String> positionsAdded = new HashSet<String>();
        for (Dependency extTGD : extTGDs) {
            addNodes(dependencyGraph, extTGD.getPremise(), positionsAdded);
            addNodes(dependencyGraph, extTGD.getConclusion(), positionsAdded);
            addEdges(dependencyGraph, extTGD);
        }
//        CycleDetector<String, ExtendedEdge> cycleDetector = new CycleDetector<String, ExtendedEdge>(dependencyGraph);
        TarjanSimpleCycles<String, ExtendedEdge> cycleDetector = new TarjanSimpleCycles<String, ExtendedEdge>(dependencyGraph);
        List<List<String>> cycles = cycleDetector.findSimpleCycles();
        if (logger.isDebugEnabled()) logger.debug("*** Cycles: " + cycles);
        for (List<String> cycle : cycles) {
            checkCycleBetweenSpecialEdge(cycle, dependencyGraph);
        }
        if (logger.isDebugEnabled()) logger.debug("Dependency graph: " + dependencyGraph);
        if (logger.isDebugEnabled()) logger.debug("**** Weakly acyclicity checked!");
    }

    private void addNodes(DirectedGraph<String, ExtendedEdge> dependencyGraph, IFormula formula, Set<String> positionsAdded) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            String tableName = relationalAtom.getTableAlias().getTableName();
            for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                String attributeName = attribute.getAttributeName();
                String position = buildPosition(tableName, attributeName);
                if (positionsAdded.contains(position)) {
                    continue;
                }
                dependencyGraph.addVertex(position);
                positionsAdded.add(position);
            }
        }
    }

    private void addEdges(DirectedGraph<String, ExtendedEdge> dependencyGraph, Dependency extTGD) {
        for (FormulaVariable variable : extTGD.getPremise().getLocalVariables()) {
            if (variable.getConclusionRelationalOccurrences().isEmpty()) {
                continue;
            }
            for (FormulaVariableOccurrence premiseRelationalOccurrence : variable.getPremiseRelationalOccurrences()) {
                String premiseNode = getPosition(premiseRelationalOccurrence);
                for (FormulaVariableOccurrence conclusionRelationalOccurrence : variable.getConclusionRelationalOccurrences()) {
                    String conclusionNode = getPosition(conclusionRelationalOccurrence);
                    if (dependencyGraph.containsEdge(premiseNode, conclusionNode)) {
                        continue;
                    }
                    dependencyGraph.addEdge(premiseNode, conclusionNode);
                }
                List<FormulaVariable> existentialVariables = DependencyUtility.getExistentialVariables(extTGD);
                for (FormulaVariable existentialVariable : existentialVariables) {
                    for (FormulaVariableOccurrence existentialOccurrence : existentialVariable.getConclusionRelationalOccurrences()) {
                        String conclusionNode = getPosition(existentialOccurrence);
                        if (dependencyGraph.containsEdge(premiseNode, conclusionNode)) {
                            dependencyGraph.removeEdge(premiseNode, conclusionNode);
                        }
                        ExtendedEdge specialEdge = new ExtendedEdge();
                        specialEdge.setSpecial(true);
                        dependencyGraph.addEdge(premiseNode, conclusionNode, specialEdge);
                    }
                }
            }
        }
    }

    private void checkCycleBetweenSpecialEdge(List<String> cycle, DirectedGraph<String, ExtendedEdge> dependencyGraph) {
        cycle.add(cycle.get(0));
        for (int i = 0; i < cycle.size() - 1; i++) {
            String nodei = cycle.get(i);
            String nodej = cycle.get(i + 1);
            ExtendedEdge edge = dependencyGraph.getEdge(nodei, nodej);
            if (edge.isSpecial()) {
                throw new ChaseException("TGDs are not weacky acyclic. Termination is not guarantee");
            }
        }
    }

    private String buildPosition(String tableName, String attributeName) {
        return tableName + "." + attributeName;
    }

    private String getPosition(FormulaVariableOccurrence relationalOccurrence) {
        return buildPosition(relationalOccurrence.getAttributeRef().getTableName(), relationalOccurrence.getAttributeRef().getName());
    }

}
