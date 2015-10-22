package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import java.util.ArrayList;
import java.util.List;

public class CostManagerConfiguration {

    private String type = LunaticConstants.COST_MANAGER_STANDARD;
    private boolean doBackward = true;
    private boolean doPermutations = true;
    private int chaseBranchingThreshold = 50;
    private int potentialSolutionsThreshold = 50;
    private int dependencyLimit = -1;
    // Similarity
    private double similarityThreshold = 0.8;
//    private String similarityStrategy = SimilarityFactory.SIMPLE_EDITS;
    private String similarityStrategy = SimilarityFactory.LEVENSHTEIN_STRATEGY;
    private boolean requestMajorityInSimilarityCostManager = true;
    private List<String> noBackwardDependencies = new ArrayList<String>();

    public boolean isDoBackwardOnDependency(Dependency dependency) {
        return this.doBackward && !this.noBackwardDependencies.contains(dependency.getId());
    }

    public boolean isDoBackwardForAllDependencies() {
        return this.doBackward;
    }

    public void setDoBackward(boolean doBackward) {
        this.doBackward = doBackward;
    }

    public boolean isDoPermutations() {
        return doPermutations;
    }

    public void setDoPermutations(boolean doPermutations) {
        this.doPermutations = doPermutations;
        if (doPermutations == false) {
            this.dependencyLimit = 1;
        }
    }

    public int getChaseBranchingThreshold() {
        return chaseBranchingThreshold;
    }

    public void setChaseBranchingThreshold(int chaseBranchingThreshold) {
        this.chaseBranchingThreshold = chaseBranchingThreshold;
    }

    public int getPotentialSolutionsThreshold() {
        return potentialSolutionsThreshold;
    }

    public void setPotentialSolutionsThreshold(int potentialSolutionsThreshold) {
        this.potentialSolutionsThreshold = potentialSolutionsThreshold;
    }

    public int getDependencyLimit() {
        return dependencyLimit;
    }

    public void setDependencyLimit(int dependencyLimit) {
        this.dependencyLimit = dependencyLimit;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getSimilarityStrategy() {
        return similarityStrategy;
    }

    public void setSimilarityStrategy(String similarityStrategy) {
        this.similarityStrategy = similarityStrategy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequestMajorityInSimilarityCostManager() {
        return requestMajorityInSimilarityCostManager;
    }

    public void setRequestMajorityInSimilarityCostManager(boolean requestMajorityInSimilarityCostManager) {
        this.requestMajorityInSimilarityCostManager = requestMajorityInSimilarityCostManager;
    }
    
    public void addNoBackwardDependency(String id) {
        this.noBackwardDependencies.add(id);
    }

    public List<String> getNoBackwardDependencies() {
        return noBackwardDependencies;
    }    

    @Override
    public String toString() {
        return "Cost manager configuration"
                + "\n\t type=" + type
                + "\n\t doBackward=" + doBackward
                + "\n\t doPermutations=" + doPermutations
                + "\n\t chaseTreeSizeThreshold=" + chaseBranchingThreshold
                + "\n\t potentialSolutionsThreshold=" + potentialSolutionsThreshold
                + "\n\t dependencyLimit=" + dependencyLimit
                + "\n\t similarityThreshold=" + similarityThreshold
                + "\n\t similarityStrategy=" + similarityStrategy
                + "\n\t requestMajorityInSimilarityCostManager=" + requestMajorityInSimilarityCostManager
                + "\n\t noBackwardDependencies=" + noBackwardDependencies;
    }
}
