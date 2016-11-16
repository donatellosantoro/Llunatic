package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;

public class CheckWeaklyAcyclicityInTGDs {

    private static Logger logger = LoggerFactory.getLogger(CheckWeaklyAcyclicityInTGDs.class);

    public void check(DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, List<Dependency> extTGDs, LunaticConfiguration conf) {
        if (extTGDs.isEmpty()) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("**** Checking weakly acyclicity");
        TarjanSimpleCycles<AttributeRef, ExtendedEdge> cycleDetector = new TarjanSimpleCycles<AttributeRef, ExtendedEdge>(dependencyGraph);
        List<List<AttributeRef>> cycles = cycleDetector.findSimpleCycles();
        if (logger.isDebugEnabled()) logger.debug("*** Cycles: " + cycles);
        for (List<AttributeRef> cycle : cycles) {
            checkCycleBetweenSpecialEdge(cycle, dependencyGraph, conf);
        }
        if (logger.isDebugEnabled()) logger.debug("Dependency graph: " + dependencyGraph);
        if (logger.isDebugEnabled()) logger.debug("**** Weakly acyclicity checked!");
    }

    private void checkCycleBetweenSpecialEdge(List<AttributeRef> cycle, DirectedGraph<AttributeRef, ExtendedEdge> dependencyGraph, LunaticConfiguration conf) {
        cycle.add(cycle.get(0));
        for (int i = 0; i < cycle.size() - 1; i++) {
            AttributeRef nodei = cycle.get(i);
            AttributeRef nodej = cycle.get(i + 1);
            ExtendedEdge edge = dependencyGraph.getEdge(nodei, nodej);
            if (edge.isSpecial()) {
                String message = "TGDs are not weakly acyclic. Termination is not guarantee. Cycle with special edge(s): " + cycle;
                if (conf.isStopOnNotWA()) {
                    throw new ChaseException(message);
                } else {
                    logger.error(message);
                    break;
                }
            }
        }
    }

}
