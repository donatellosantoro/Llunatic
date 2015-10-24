package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.similarity.SimilarityConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.AttributeRef;

public class CostManagerConfiguration {

    private String type = LunaticConstants.COST_MANAGER_STANDARD;
    private boolean doBackward = true;
    private boolean doPermutations = true;
    private int chaseBranchingThreshold = 50;
    private int potentialSolutionsThreshold = 50;
    private int dependencyLimit = -1;
    private int numberOfCandidateValuesForSimilarity = 5;
    // Similarity
    private SimilarityConfiguration defaultSimilarityConfiguration = new SimilarityConfiguration(SimilarityFactory.LEVENSHTEIN_STRATEGY, 0.8);
    private Map<AttributeRef, SimilarityConfiguration> similarityConfigurationForAttribute = new HashMap<AttributeRef, SimilarityConfiguration>();
    private boolean requestMajorityInSimilarityCostManager = true;
    private final List<String> noBackwardDependencies = new ArrayList<String>();

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

    public SimilarityConfiguration getDefaultSimilarityConfiguration() {
        return defaultSimilarityConfiguration;
    }

    public void setDefaultSimilarityConfiguration(SimilarityConfiguration defaultSimilarityConfiguration) {
        this.defaultSimilarityConfiguration = defaultSimilarityConfiguration;
    }

    public SimilarityConfiguration getSimilarityConfiguration(AttributeRef attribute) {
        SimilarityConfiguration similarityConfiguration = similarityConfigurationForAttribute.get(attribute);
        if (similarityConfiguration != null) {
            return similarityConfiguration;
        }
        return defaultSimilarityConfiguration;
    }

    public void setSimilarityConfigurationForAttribute(AttributeRef attribute, SimilarityConfiguration similarityConfiguration) {
        this.similarityConfigurationForAttribute.put(attribute, similarityConfiguration);
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

    public int getNumberOfCandidateValuesForSimilarity() {
        return numberOfCandidateValuesForSimilarity;
    }

    public void setNumberOfCandidateValuesForSimilarity(int numberOfCandidateValuesForSimilarity) {
        this.numberOfCandidateValuesForSimilarity = numberOfCandidateValuesForSimilarity;
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
                + "\n\t defaultSimilarityConfigurationThreshold=" + defaultSimilarityConfiguration
                + "\n\t similarityConfigurationForAttribute=" + similarityConfigurationForAttribute
                + "\n\t requestMajorityInSimilarityCostManager=" + requestMajorityInSimilarityCostManager
                + "\n\t numberOfCandidateValuesForSimilarity=" + numberOfCandidateValuesForSimilarity
                + "\n\t noBackwardDependencies=" + noBackwardDependencies;
    }
}
