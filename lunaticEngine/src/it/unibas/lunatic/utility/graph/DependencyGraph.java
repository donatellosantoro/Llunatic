package it.unibas.lunatic.utility.graph;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyGraph {

    private static Logger logger = LoggerFactory.getLogger(DependencyGraph.class);

    public Multigraph<IFormulaAtom, DefaultEdge> getDependencyGraph(Scenario scenario) {
        Multigraph<IFormulaAtom, DefaultEdge> graph = new Multigraph<IFormulaAtom, DefaultEdge>(DefaultEdge.class);
//        handleDependency(graph, scenario.getEGDs().get(0));
        for (Dependency dependency : scenario.getSTTgds()) {
            handleDependency(graph, dependency);
        }
        for (Dependency dependency : scenario.getEGDs()) {
            handleDependency(graph, dependency);
        }
        for (Dependency dependency : scenario.getExtEGDs()) {
            handleDependency(graph, dependency);
        }
        for (Dependency dependency : scenario.getExtTGDs()) {
            handleDependency(graph, dependency);
        }
        return graph;
    }

    private void handleDependency(Multigraph<IFormulaAtom, DefaultEdge> graph, Dependency dependency) {
        addVertices(graph, dependency);
        addEdges(graph, dependency);
    }

    private void addVertices(UndirectedGraph<IFormulaAtom, DefaultEdge> graph, Dependency dependency) {
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            graph.addVertex((IFormulaAtom) atom);
        }
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            graph.addVertex((IFormulaAtom) atom);
        }
    }

    private void addEdges(UndirectedGraph<IFormulaAtom, DefaultEdge> graph, Dependency dependency) {
        for (FormulaVariable formulaVariable : dependency.getPremise().getAllVariables()) {
            handleFormulaVariable(formulaVariable, graph, dependency);
        }
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            handleFormulaVariable(formulaVariable, graph, dependency);
        }
        for (FormulaVariable formulaVariable : dependency.getConclusion().getLocalVariables()) {
            handleFormulaVariable(formulaVariable, graph, dependency);
        }
    }

    private void handleFormulaVariable(FormulaVariable formulaVariable, UndirectedGraph<IFormulaAtom, DefaultEdge> graph, Dependency dependency) {
        List<IFormulaAtom> formulaAtoms = new ArrayList<IFormulaAtom>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : formulaVariable.getPremiseOccurrences()) {
            IFormulaAtom atom = getRelationalAtom(dependency, formulaVariableOccurrence.getTableAlias());
            formulaAtoms.add(atom);
        }
        for (FormulaVariableOccurrence formulaVariableOccurrence : formulaVariable.getConclusionOccurrences()) {
            IFormulaAtom atom = getRelationalAtom(dependency, formulaVariableOccurrence.getTableAlias());
            formulaAtoms.add(atom);
        }
        for (IFormulaAtom atom : formulaVariable.getNonRelationalOccurrences()) {
            formulaAtoms.add(atom);
        }
        for (int i = 0; i < formulaAtoms.size() - 1; i++) {
            for (int j = i + 1; j < formulaAtoms.size(); j++) {
                IFormulaAtom atomi = formulaAtoms.get(i);
                IFormulaAtom atomj = formulaAtoms.get(j);

                if (formulaVariable.isUniversal() && isConclusionAtom(atomi, dependency) && isConclusionAtom(atomj, dependency)) {
                    //Exclude edge between universal variable in conclusion
                    continue;
                }
                graph.addEdge(atomi, atomj, new LabeledVariableEdge(atomi, atomj, formulaVariable.getId()));
                if (logger.isDebugEnabled()) logger.debug("Adding edge between: " + atomi + " and " + atomj + " on variable " + formulaVariable.getId());
            }
        }
    }

    private boolean isConclusionAtom(IFormulaAtom atom, Dependency dependency) {
        return dependency.getConclusion().getAtoms().contains(atom);
    }

    private IFormulaAtom getRelationalAtom(Dependency dependency, TableAlias tableAlias) {
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            IFormulaAtom relationalAtom = (IFormulaAtom) atom;
            if (relationalAtom instanceof RelationalAtom && (((RelationalAtom) relationalAtom).getTableAlias().equals(tableAlias))) {
                return relationalAtom;
            }
        }
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            if (relationalAtom.getTableAlias().equals(tableAlias)) {
                return relationalAtom;
            }
        }
        throw new IllegalArgumentException("Unable to find table alias " + tableAlias + " in conclusion " + dependency);
    }

    public class LabeledVariableEdge extends DefaultEdge {

        private IFormulaAtom v1;
        private IFormulaAtom v2;
        private String label;

        public LabeledVariableEdge(IFormulaAtom v1, IFormulaAtom v2, String label) {
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
        }

        public IFormulaAtom getV1() {
            return v1;
        }

        public IFormulaAtom getV2() {
            return v2;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
