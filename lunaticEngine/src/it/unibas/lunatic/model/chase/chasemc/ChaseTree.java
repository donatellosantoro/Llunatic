package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.Scenario;

public class ChaseTree {
    
    private Scenario scenario;
    private DeltaChaseStep root;
    private NodeClustering clusters = new NodeClustering();

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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(root.toShortStringWithSort()).append("\n");
        result.append("Node Clusters:\n");
        result.append(clusters);
        return result.toString();
    }
    
}
