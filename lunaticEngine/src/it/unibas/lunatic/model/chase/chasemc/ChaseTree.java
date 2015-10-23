package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.Scenario;
import java.util.List;

public class ChaseTree {
    
    private final Scenario scenario;
    private DeltaChaseStep root;
    private NodeClustering clusters = new NodeClustering();
    private List<DeltaChaseStep> rankedSolutions; // only in root node

    public ChaseTree(Scenario scenario) {
        this.scenario = scenario;
    }

    public DeltaChaseStep getRoot() {
        return root;
    }

    public void setRoot(DeltaChaseStep root) {
        this.root = root;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public NodeClustering getClusters() {
        return clusters;
    }    

    public List<DeltaChaseStep> getRankedSolutions() {
        return rankedSolutions;
    }

    public void setRankedSolutions(List<DeltaChaseStep> rankedSolutions) {
        this.rankedSolutions = rankedSolutions;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(root.toShortStringWithSort()).append("\n");
        result.append("Node Clusters:\n");
        result.append(clusters).append("\n");
        result.append("Ranked solutions: ").append(rankedSolutions);
        return result.toString();
    }
    
}
