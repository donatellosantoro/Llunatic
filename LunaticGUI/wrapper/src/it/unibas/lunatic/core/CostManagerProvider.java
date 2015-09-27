package it.unibas.lunatic.core;

import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.SimilarityToMostFrequentCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.StandardCostManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CostManagerProvider {

    private static CostManagerProvider instance;

    public static CostManagerProvider getInstance() {
        if (instance == null) {
            instance = new CostManagerProvider();
        }
        return instance;
    }

    public SimilarityToMostFrequentCostManager getSimilarityToMostFrequentCostManager() {
        return new SimilarityToMostFrequentCostManager();
    }

    public StandardCostManager getStandardCostManager() {
        return new StandardCostManager();
    }

    public Collection<ICostManager> getAll() {
        List<ICostManager> list = new ArrayList<ICostManager>();
        list.add(getStandardCostManager());
        list.add(getSimilarityToMostFrequentCostManager());
        return list;
    }
}
