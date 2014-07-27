/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.core;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.model.chasemc.costmanager.FrequencyPartitionCostManager;
import it.unibas.lunatic.model.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chasemc.costmanager.MinCostRepairCostManager;
import it.unibas.lunatic.model.chasemc.costmanager.SamplingCostManager;
import it.unibas.lunatic.model.chasemc.costmanager.SimilarityToMostFrequentCostManager;
import it.unibas.lunatic.model.chasemc.costmanager.SimilarityToMostFrequentUserInputCostManager;
import it.unibas.lunatic.model.chasemc.costmanager.StandardCostManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Antonio Galotta
 */
public class CostManagerProvider {
    
    private static CostManagerProvider instance;
    
    public static CostManagerProvider getInstance() {
        if (instance == null) {
            instance = new CostManagerProvider();
        }
        return instance;
    }
    
    public FrequencyPartitionCostManager getFrequencyPartitionCostManager() {
        return new FrequencyPartitionCostManager();
    }
    
    public MinCostRepairCostManager getMinCostRepairCostManager() {
        return new MinCostRepairCostManager();
    }
    
    public SamplingCostManager getSamplingCostManager() {
        return new SamplingCostManager();
    }
    
    public SimilarityToMostFrequentCostManager getSimilarityToMostFrequentCostManager() {
        return new SimilarityToMostFrequentCostManager();
    }
    
    public SimilarityToMostFrequentUserInputCostManager getSimilarityToMostFrequentUserInputCostManager() {
        return new SimilarityToMostFrequentUserInputCostManager();
    }
    
    public StandardCostManager getStandardCostManager() {
        return new StandardCostManager();
    }
    
    public Collection<ICostManager> getAll() {
        List<ICostManager> list = new ArrayList<ICostManager>();
        list.add(getStandardCostManager());
        list.add(getFrequencyPartitionCostManager());
        list.add(getMinCostRepairCostManager());
        list.add(getSimilarityToMostFrequentCostManager());
        list.add(getSimilarityToMostFrequentUserInputCostManager());
        list.add(getSamplingCostManager());
        return list;
    }
}
