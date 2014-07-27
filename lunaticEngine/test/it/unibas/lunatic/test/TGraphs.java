package it.unibas.lunatic.test;

import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TGraphs extends TestCase {
    
    private static Logger logger = LoggerFactory.getLogger(TGraphs.class);

    public TGraphs(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
    
    public void testGraph() {
        UndirectedGraph<String, LabeledEdge> graph = new SimpleGraph<String, LabeledEdge>(LabeledEdge.class);
        Set<String> vertices = new HashSet<String>();
        vertices.add("R1");
        vertices.add("R2");
        vertices.add("T1");
        vertices.add("T2");
        vertices.add("S");
        vertices.add("V");
        graph.addVertex("R1");
        graph.addVertex("R2");
        graph.addVertex("T1");
        graph.addVertex("T2");
        graph.addVertex("S");
        graph.addVertex("V");
        graph.addEdge("R1", "S", new LabeledEdge("R1", "S", "R.A,S.A"));
        graph.addEdge("R2", "S", new LabeledEdge("R2", "S", "R.A,S.A"));
        graph.addEdge("R1", "T1", new LabeledEdge("R1", "T1", "R.B,T.B"));
        graph.addEdge("R2", "T2", new LabeledEdge("R2", "T2", "R.B,T.B"));
        graph.addEdge("R1", "R2", new LabeledEdge("R1", "R2", "R.A,R.A"));
//        graph.addEdge("T1", "V", new LabeledEdge("T1", "V", "T.C,V.C"));
        Set<String> vertices1 = new HashSet<String>(vertices);
        vertices1.remove("R2");
        UndirectedSubgraph<String, LabeledEdge> subgraph1 = new UndirectedSubgraph<String, LabeledEdge>(graph, vertices1, graph.edgeSet());
        ConnectivityInspector<String, LabeledEdge> inspector1 = new ConnectivityInspector<String, LabeledEdge>(subgraph1);
        Set<String> connectedVertices1 = inspector1.connectedSetOf("R1");
        UndirectedSubgraph<String, LabeledEdge> connectedSubgraph1 = new UndirectedSubgraph<String, LabeledEdge>(graph, connectedVertices1, graph.edgeSet());
        Set<String> vertices2 = new HashSet<String>(vertices);
        vertices2.remove("R1");
        UndirectedSubgraph<String, LabeledEdge> subgraph2 = new UndirectedSubgraph<String, LabeledEdge>(graph, vertices2, graph.edgeSet());
        ConnectivityInspector<String, LabeledEdge> inspector2 = new ConnectivityInspector<String, LabeledEdge>(subgraph2);
        Set<String> connectedVertices2 = inspector2.connectedSetOf("R2");
        UndirectedSubgraph<String, LabeledEdge> connectedSubgraph2 = new UndirectedSubgraph<String, LabeledEdge>(graph, connectedVertices2, graph.edgeSet());
        Set<LabeledEdge> edges1 = connectedSubgraph1.edgeSet();
        Set<LabeledEdge> edges2 = connectedSubgraph2.edgeSet();
        if (containsAll(edges1, edges2)) {
            logger.debug("R1 is contained in R2");
        }
        if (containsAll(edges2, edges1)) {
            logger.debug("R2 is contained in R1");
        }
    }

    private boolean containsAll(Set<LabeledEdge> edges1, Set<LabeledEdge> edges2) {
        for (LabeledEdge edge2 : edges2) {
            if (!contains(edges1, edge2)) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(Set<LabeledEdge> edges1, LabeledEdge edge2) {
        for (LabeledEdge edge1 : edges1) {
            if (edge1.getLabel().equals(edge2.getLabel())) {
                return true;
            }
        }
        return false;
    }
}
class LabeledEdge extends DefaultEdge {

    private String v1;
    private String v2;
    private String label;

    public LabeledEdge(String v1, String v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public String getV1() {
        return v1;
    }

    public String getV2() {
        return v2;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return label;
    }
}